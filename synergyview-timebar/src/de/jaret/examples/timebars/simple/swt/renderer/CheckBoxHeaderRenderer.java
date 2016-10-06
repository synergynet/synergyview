/*
 *  File: CheckBoxHeaderRenderer.java 
 *  Copyright (c) 2004-2008  Peter Kliem (Peter.Kliem@jaret.de)
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
package de.jaret.examples.timebars.simple.swt.renderer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.ResourceImageDescriptor;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import de.jaret.util.ui.timebars.swt.renderer.HeaderRenderer;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;

/**
 * Simple heder renderer rendering the selection with an imitated checkbox. Obviously best used with 
 * the sleection models row selction toggle mode.
 * 
 * @author Peter Kliem
 * @version $Id: CheckBoxHeaderRenderer.java 751 2008-04-22 20:10:24Z kliem $
 */
public class CheckBoxHeaderRenderer extends RendererBase implements HeaderRenderer {
    /** line width when printing. */
    private static final int PRINTING_LINEWIDTH = 3;
    /** distance of the checkbox. */
    private static final int INSETS = 2;

    /** rsc name for the checked state. */
    protected String _checkedRscName = "/de/jaret/examples/timebars/simple/swt/renderer/checked.gif";
    /** default rsc name for the unchecked state. */
    protected String _uncheckedRscName = "/de/jaret/examples/timebars/simple/swt/renderer/unchecked.gif";
    /** key for checked image in registry. */
    protected static final String CHECKED = "checked";
    /** key for unchecked image in registry. */
    protected static final String UNCHECKED = "unchecked";
    /** image registry for holding the images. */
    private ImageRegistry _imageRegistry;

    /**
     * Constructor for printing use.
     * 
     * @param printer printing device
     */
    public CheckBoxHeaderRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Constructor for screen use.
     * 
     */
    public CheckBoxHeaderRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, TimeBarRowHeader header,
            boolean selected, boolean printing) {

        if (printing) {
            gc.setLineWidth(PRINTING_LINEWIDTH);
        }

        // draw background
        Color bg = gc.getBackground();
        Color fg = gc.getForeground();
        gc.setForeground(gc.getDevice().getSystemColor(SWT.COLOR_WHITE));
        gc.fillRectangle(drawingArea);
        gc.setForeground(fg);

        // retrieve the checkbox image and draw it
        Image img = null;
        if (selected) {
            img = getImageRegistry().get(CHECKED);
        } else {
            img = getImageRegistry().get(UNCHECKED);
        }
        int x = drawingArea.x + scaleX(INSETS);
        int y = drawingArea.y + scaleY(INSETS);
        gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, x, y, scaleX(img.getBounds().width),
                scaleY(img.getBounds().height));

        int offx = scaleX(img.getBounds().width) + scaleX(INSETS);
        String str = header.toString();
        SwtGraphicsHelper.drawStringCenteredVCenter(gc, str, drawingArea.x + offx, drawingArea.x + drawingArea.width
                - offx, drawingArea.y + drawingArea.height / 2);

        gc.setBackground(bg);
        if (delegate.getDrawRowGrid()) {
            if (delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL) {
                gc.drawLine(drawingArea.x, drawingArea.y + drawingArea.height - 1, drawingArea.x + drawingArea.width,
                        drawingArea.y + drawingArea.height - 1);
            } else {
                gc.drawLine(drawingArea.x + drawingArea.width - 1, drawingArea.y,
                        drawingArea.x + drawingArea.width - 1, drawingArea.y + drawingArea.height - 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(TimeBarRow row, Rectangle drawingArea, int x, int y) {
        if (row == null) {
            return null;
        }
        return row.getRowHeader().getLabel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Rectangle drawingArea, int x, int y) {
        return true;
    }

    /**
     * Retrieve the image registry used by the renderer (lazy initializing).
     * 
     * @return initialized image regsitry containing the resources
     */
    private ImageRegistry getImageRegistry() {
        if (_imageRegistry == null) {
            _imageRegistry = new ImageRegistry();
            ImageDescriptor imgDesc = new ResourceImageDescriptor(_checkedRscName, this.getClass());
            _imageRegistry.put(CHECKED, imgDesc.createImage());
            imgDesc = new ResourceImageDescriptor(_uncheckedRscName, this.getClass());
            _imageRegistry.put(UNCHECKED, imgDesc.createImage());
        }
        return _imageRegistry;
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
     * {@inheritDoc}
     */
    public HeaderRenderer createPrintRenderer(Printer printer) {
        return new CheckBoxHeaderRenderer(printer);
    }
}
