/*
 *  File: DefaultHierarchyRenderer.java 
 *  Copyright (c) 2004-2007  Peter Kliem (Peter.Kliem@jaret.de)
 *  A commercial license is available, see http://www.jaret.de.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package de.jaret.util.ui.timebars.swt.renderer;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.ui.ResourceImageDescriptor;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.StdHierarchicalTimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarNode;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * Default implementation of a HierarchyRenderer. Can draw +/-/o for nodes or any configured image. Uses label providers
 * (regsiters for row implementing class) for labels and icons (icons replace standard node symbol).
 * 
 * @author Peter Kliem
 * @version $Id: DefaultHierarchyRenderer.java 800 2008-12-27 22:27:33Z kliem $
 */
public class DefaultHierarchyRenderer extends RendererBase implements HierarchyRenderer {
    /** size of the plus/minus signs. */
    protected static final int DEFAULT_SIZE = 12;

    /** insets for painting the +/. symbol. */
    protected static final int SYMBOLINSETS = 3;

    /** defaut level width. */
    protected static final int DEFAULT_LEVEL_WIDTH = 16;

    /** gap between tree and the label/icon provided by a label provider. */
    protected static final int LABELGAP = 4;

    /** actual size of the symbols. */
    protected int _size = DEFAULT_SIZE;

    /** actual value for the signinstes. */
    protected int _signInsets = SYMBOLINSETS;

    /** if true, tree lines will be drawn. */
    protected boolean _drawTreeLines = true;

    /** level width for fixed level width drawing. */
    protected int _levelWidth = DEFAULT_LEVEL_WIDTH;

    /** if true the level indentation is fixed. */
    protected boolean _fixedLevelWidth = false;

    /** true for enabling drawing of icons. */
    protected boolean _drawIcons = false;

    /** true for enabling label drawing. */
    protected boolean _drawLabels = false;

    /** label providers for labels and icons. */
    protected Map<Class<? extends TimeBarRow>, ILabelProvider> _labelProviderMap = new HashMap<Class<? extends TimeBarRow>, ILabelProvider>();

    /** key for image registry. */
    protected static final String PLUS = "plus";
    /** key for image registry. */
    protected static final String MINUS = "minus";
    /** key for image registry. */
    protected static final String LEAF = "leaf";

    /** image registry for holding the images. */
    private ImageRegistry _imageRegistry;

    /** path of the plus image ressource. */
    protected String _plusRscName;
    /** path of the minus image ressource. */
    protected String _minusRscName;
    /** path of the leaf image ressource. */
    protected String _leafRscName;

    /**
     * Create for a printer.
     * 
     * @param printer printer device
     */
    public DefaultHierarchyRenderer(Printer printer) {
        super(printer);
        _size = scaleX(DEFAULT_SIZE);
        _signInsets = scaleX(_signInsets);
        _levelWidth = scaleX(_levelWidth);
    }

    /**
     * Default constructor.
     */
    public DefaultHierarchyRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate tbv, TimeBarRow row, boolean selected,
            boolean expanded, boolean leaf, int level, int depth, boolean printing) {

        int offx;
        if (!_fixedLevelWidth && depth > 0) {
            offx = (drawingArea.width - _size) / (depth + 1);
        } else {
            offx = scaleX(_levelWidth);
        }

        int x = drawingArea.x + offx * level + _size / 2;

        int y = drawingArea.y + (drawingArea.height - _size) / 2;

        if (leaf && !_drawIcons) {
            drawLeafSymbol(gc, _size, x, y);
        } else if (expanded && !leaf) {
            drawExpanded(gc, _size, x, y);
        } else if (!leaf) {
            drawCollapsed(gc, _size, x, y);
        }
        x += _size + LABELGAP;

        ILabelProvider labelProvider = getLabelProvider(row.getClass());

        if (labelProvider != null && (_drawIcons || _drawLabels)) {
            int labelx = x;
            if (_drawIcons) {
                Image img = labelProvider.getImage(row);
                if (img != null) {
                    if (!printing) {
                        gc.drawImage(img, x, y);
                        labelx += img.getBounds().width;
                    } else {
                        gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, x, y, scaleX(img
                                .getBounds().width), scaleY(img.getBounds().height));
                        labelx += scaleX(img.getBounds().width);
                    }
                }
            }
            if (_drawLabels) {
                String label = labelProvider.getText(row);
                if (label != null) {
                    if (!printing && selected) {
                        Color bg = gc.getBackground();
                        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
                        gc.drawString(label, labelx, y);
                        gc.setBackground(bg);
                    } else {
                        gc.drawString(label, labelx, y);
                    }
                }
            }

        }

        // draw tree connections
        if (_drawTreeLines) {
            TimeBarNode node = (TimeBarNode) row;
            if (printing) {
                gc.setLineWidth(getDefaultLineWidth());
            }
            gc.setLineStyle(SWT.LINE_DOT);
            int midy = drawingArea.y + ((drawingArea.height - _size) / 2) + _size / 2;
            int icoy = drawingArea.y + ((drawingArea.height - _size) / 2) + _size;
            int icox = drawingArea.x + offx * (level) + _size - _size / 2;
            int midx = drawingArea.x + +offx * (level) + _size;
            int beginx = drawingArea.x + offx * (level - 1) + _size;
            // int endx = drawingArea.x + offx * (level + 1) + _size;

            // connection
            gc.drawLine(beginx, midy, icox, midy);

            // uplink
            gc.drawLine(beginx, drawingArea.y, beginx, midy);

            // downlink
            if ((!leaf && expanded)) {
                gc.drawLine(midx, icoy, midx, drawingArea.y + drawingArea.height);
            }

            // level lines
            if (tbv.getModel() instanceof StdHierarchicalTimeBarModel) {
                StdHierarchicalTimeBarModel model = (StdHierarchicalTimeBarModel) tbv.getModel();
                for (int i = 0; i < level; i++) {
                    if (model.moreSiblings(node, i)) {
                        x = drawingArea.x + offx * i + _size;
                        gc.drawLine(x, drawingArea.y, x, drawingArea.y + drawingArea.height);
                    }
                }
            }

            gc.setLineStyle(SWT.LINE_SOLID);
            gc.setLineWidth(1);
        }
    }

    /**
     * Draw the collapsed symbol.
     * 
     * @param gc GC
     * @param size size
     * @param x left x
     * @param y upper y
     */
    protected void drawCollapsed(GC gc, int size, int x, int y) {
        if (_plusRscName != null) {
            ImageRegistry registry = getImageRegistry();
            Image img = registry.get(PLUS);
            if (img != null) {
                gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, x, y, size, size);
            }
        } else {
            drawPlus(gc, size, x, y);
        }
    }

    /**
     * Draw the expanded symbol.
     * 
     * @param gc GC
     * @param size size
     * @param x left x
     * @param y upper y
     */
    protected void drawExpanded(GC gc, int size, int x, int y) {
        if (_minusRscName != null) {
            ImageRegistry registry = getImageRegistry();
            Image img = registry.get(MINUS);
            if (img != null) {
                gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, x, y, size, size);
            }
        } else {
            drawMinus(gc, size, x, y);
        }
    }

    /**
     * Draw the leaf symbol.
     * 
     * @param gc GC
     * @param size size
     * @param x left x
     * @param y upper y
     */
    protected void drawLeafSymbol(GC gc, int size, int x, int y) {
        if (_leafRscName != null) {
            ImageRegistry registry = getImageRegistry();
            Image img = registry.get(LEAF);
            if (img != null) {
                gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, x, y, size, size);
            }
        } else {
            drawLeaf(gc, size, x, y);
        }
    }

    /**
     * Draw the plus sign.
     * 
     * @param gc GC
     * @param size size
     * @param x left x
     * @param y upper y
     */
    protected void drawPlus(GC gc, int size, int x, int y) {
        gc.drawLine(x + _signInsets, y + size / 2, x + size - _signInsets, y + size / 2);
        gc.drawLine(x + size / 2, y + _signInsets, x + size / 2, y + size - _signInsets);
        gc.drawRectangle(x, y, size, size);
    }

    /**
     * Draw the minus sign.
     * 
     * @param gc GC
     * @param size size
     * @param x left x
     * @param y upper y
     */
    protected void drawMinus(GC gc, int size, int x, int y) {
        gc.drawLine(x + _signInsets, y + size / 2, x + size - _signInsets, y + size / 2);
        gc.drawRectangle(x, y, size, size);
    }

    /**
     * Draw the leaf symbol.
     * 
     * @param gc GC
     * @param size size
     * @param x left x
     * @param y upper y
     */
    protected void drawLeaf(GC gc, int size, int x, int y) {
        Color bg = gc.getBackground();
        gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        gc.fillOval(x + size / 2, y + size / 2, size / 2, size / 2);
        gc.setBackground(bg);
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarNode node, Rectangle drawingArea, int x, int y) {
        if (node != null) {
            return node.getRowHeader().getLabel();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInToggleArea(TimeBarViewerInterface tbv, TimeBarNode node, Rectangle drawingArea, int xx, int yy) {
        int depth = tbv.getHierarchicalModel().getDepth();
        int offx;
        if (!_fixedLevelWidth && depth > 0) {
            offx = (drawingArea.width - _size) / (depth + 1);
        } else {
            offx = scaleX(_levelWidth);
        }
        int level = node.getLevel();

        int x = drawingArea.x + offx * level + _size / 2;
        int y = drawingArea.y + (drawingArea.height - _size) / 2;

        return x <= xx && xx <= x + _size && y <= yy && yy <= y + _size;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isInHierarchySelectionArea(TimeBarViewer tbv, TimeBarNode node, Rectangle drawingArea, int xx, int yy) {
        int depth = tbv.getHierarchicalModel().getDepth();
        int offx;
        if (!_fixedLevelWidth && depth > 0) {
            offx = (drawingArea.width - _size) / (depth + 1);
        } else {
            offx = scaleX(_levelWidth);
        }
        int level = node.getLevel();

        int x = drawingArea.x + offx * level + _size / 2;
        int y = drawingArea.y + (drawingArea.height - _size) / 2;

        x += _size + LABELGAP;

        ILabelProvider labelProvider = getLabelProvider(node.getClass());

        if (labelProvider != null && (_drawIcons || _drawLabels)) {
            Rectangle selRect = new Rectangle(x, y, 0, 0);
            if (_drawIcons) {
                Image img = labelProvider.getImage(node);
                if (img != null) {
                    selRect.width += img.getBounds().width;
                    selRect.height += img.getBounds().height;
                }
            }
            if (_drawLabels) {
                String label = labelProvider.getText(node);
                if (label != null) {
                    selRect.width += drawingArea.width - selRect.x; // selection
                    // to end of
                    // hierarchy
                    // rendering
                    selRect.height = Math.max(selRect.height, 16); // 16 pixel
                    // minimium
                    // height
                }
            }
            return selRect.contains(xx, yy);

        }
        return false;

    }

    /**
     * {@inheritDoc}
     */
    public int getPreferredWidth() {
        return scaleX(_size + LABELGAP);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
        }
    }

    /**
     * Retrieve whether level indentation is done with a fixed width or variable.
     * 
     * @return true if the renderer uses a fixed width for the level indentation
     */
    public boolean getFixedLevelWidth() {
        return _fixedLevelWidth;
    }

    /**
     * Set method of level indentation.
     * 
     * @param fixedLevelWidth true for the use of a fixed indent for the levels
     */
    public void setFixedLevelWidth(boolean fixedLevelWidth) {
        _fixedLevelWidth = fixedLevelWidth;
    }

    /**
     * Get the default label provider to use for label texts and icons (TimeBarRow.class).
     * 
     * @return Returns the labelProvider.
     */
    public ILabelProvider getLabelProvider() {
        return getLabelProvider(TimeBarRow.class);
    }

    /**
     * Set the default label provider to use for label texts and icons (TimeBarRow.class).
     * 
     * @param labelProvider The labelProvider to set.
     */
    public void setLabelProvider(ILabelProvider labelProvider) {
        registerLabelProvider(TimeBarRow.class, labelProvider);
    }

    /**
     * Register a label provider for a class.
     * 
     * @param clazz time bar row implementing clas or extendin ginterface
     * @param labelProvider label provider
     */
    public void registerLabelProvider(Class<? extends TimeBarRow> clazz, ILabelProvider labelProvider) {
        _labelProviderMap.put(clazz, labelProvider);
    }

    /**
     * Set the complete label provider map (internal use).
     * 
     * @param map map of the label providers
     */
    protected void setLabelProviderMap(Map<Class<? extends TimeBarRow>, ILabelProvider> map) {
        _labelProviderMap = map;
    }

    /**
     * Retrieve a label provider for a given class. Checks all interfaces and all superclasses.
     * 
     * @param clazz class in question
     * @return label provider or null
     */
    protected ILabelProvider getLabelProvider(Class<? extends TimeBarRow> clazz) {
        ILabelProvider result = null;
        result = _labelProviderMap.get(clazz);
        if (result != null) {
            return result;
        }

        // direct interfaces
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> c : interfaces) {
            result = _labelProviderMap.get(c);
            if (result != null) {
                return result;
            }
        }

        // superclasses
        Class<?> sc = clazz.getSuperclass();

        while (sc != null) {
            result = _labelProviderMap.get(sc);
            if (result != null) {
                return result;
            }
            // interfaces of the superclass
            Class<?>[] scinterfaces = sc.getInterfaces();
            for (Class<?> c : scinterfaces) {
                result = _labelProviderMap.get(c);
                if (result != null) {
                    return result;
                }
            }
            sc = sc.getSuperclass();
        }

        return result;
    }

    /**
     * Retrieve the width for each level (only applicable if fixed level width is used).
     * 
     * @return Returns the levelWidth.
     */
    public int getLevelWidth() {
        return _levelWidth;
    }

    /**
     * @param levelWidth The levelWidth to set.
     */
    public void setLevelWidth(int levelWidth) {
        _levelWidth = levelWidth;
    }

    /**
     * @return Returns the drawIcons.
     */
    public boolean getDrawIcons() {
        return _drawIcons;
    }

    /**
     * @param drawIcons The drawIcons to set.
     */
    public void setDrawIcons(boolean drawIcons) {
        this._drawIcons = drawIcons;
    }

    /**
     * @return Returns the drawLabels.
     */
    public boolean getDrawLabels() {
        return _drawLabels;
    }

    /**
     * @param drawLabels The drawLabels to set.
     */
    public void setDrawLabels(boolean drawLabels) {
        this._drawLabels = drawLabels;
    }

    /**
     * {@inheritDoc}
     */
    public HierarchyRenderer createPrintRenderer(Printer printer) {
        DefaultHierarchyRenderer r = new DefaultHierarchyRenderer(printer);
        r.setDrawIcons(_drawIcons);
        r.setDrawLabels(_drawLabels);
        r.setFixedLevelWidth(_fixedLevelWidth);
        r.setLevelWidth(_levelWidth);
        r.setLabelProviderMap(_labelProviderMap);
        r.setRscNames(_plusRscName, _minusRscName, _leafRscName);
        return r;
    }

    /**
     * Set the paths for images to use a symbols. If set the will be used instead of the generic +/-/o symbols. Please
     * note that the resources are loaded bay getResourceAsStream whicj can be problematic when using it in a multi
     * class loader environment like eclipse rcp. Use the setImageDescriptors method instead.
     * 
     * @param plusRscPath ressource path for the collapsed symbol
     * @param minusRscPath ressource path for the expanded symbol
     * @param leafRscPath ressource path for the leaf symbol
     */
    public void setRscNames(String plusRscPath, String minusRscPath, String leafRscPath) {
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
            _imageRegistry = null;
        }
        _plusRscName = plusRscPath;
        _minusRscName = minusRscPath;
        _leafRscName = leafRscPath;
    }

    /**
     * Set image descriptors for the symbols.
     * 
     * @param plus image descriptor for the plus sign
     * @param minus image descriptor for the minus sign
     * @param leaf image descriptor for the leaf sign
     */
    public void setImageDescriptors(ImageDescriptor plus, ImageDescriptor minus, ImageDescriptor leaf) {
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
        }
        _imageRegistry = new ImageRegistry();
        _imageRegistry.put(PLUS, plus);
        _imageRegistry.put(MINUS, minus);
        _imageRegistry.put(LEAF, leaf);
        _plusRscName = "plusRscPath";
        _minusRscName = "minusRscPath";
        _leafRscName = "leafRscPath";
    }

    /**
     * Retrieve initialized image registry.
     * 
     * @return initialized registry
     */
    private ImageRegistry getImageRegistry() {
        if (_imageRegistry == null) {
            _imageRegistry = new ImageRegistry();
            ImageDescriptor imgDesc = new ResourceImageDescriptor(_plusRscName, this.getClass());
            _imageRegistry.put(PLUS, imgDesc.createImage());
            imgDesc = new ResourceImageDescriptor(_minusRscName, this.getClass());
            _imageRegistry.put(MINUS, imgDesc.createImage());
            imgDesc = new ResourceImageDescriptor(_leafRscName, this.getClass());
            _imageRegistry.put(LEAF, imgDesc.createImage());
        }
        return _imageRegistry;
    }

    /**
     * Retrieve the squae size of the symbols. Images will be scaled.
     * 
     * @return size
     */
    public int getSize() {
        return _size;
    }

    /**
     * Set the size of the symbols.
     * 
     * @param size size in pixel
     */
    public void setSize(int size) {
        _size = size;
    }

    /**
     * Retrieve whether tree lines shoul dbe drawn.
     * 
     * @return true if the tree lines should be drawn
     */
    public boolean getDrawTreeLines() {
        return _drawTreeLines;
    }

    /**
     * Set whether connecting lines in the tree should be drawn.
     * 
     * @param drawTreeLines true if tree lines should be drawn
     */
    public void setDrawTreeLines(boolean drawTreeLines) {
        _drawTreeLines = drawTreeLines;
    }

}
