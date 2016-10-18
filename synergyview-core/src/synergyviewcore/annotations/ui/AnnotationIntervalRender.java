/**
 * File: CaptionIntervalRender.java Copyright (c) 2010 phyokyaw This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.annotations.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.attributes.model.AttributeNode;
import synergyviewcore.attributes.ui.views.CodingExplorerViewPart;
import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;

/**
 * The Class AnnotationIntervalRender.
 * 
 * @author phyokyaw
 */
public class AnnotationIntervalRender extends RendererBase implements TimeBarRenderer, TimeBarRenderer2, ISelectionListener {

    /** The _rounding. */
    private static int _rounding = 3;

    /** The Constant BORDERFACTOR. */
    protected static final double BORDERFACTOR = 0.2;

    /** The Constant PREFWIDTH. */
    protected static final int PREFWIDTH = 10;

    /**
     * Gets the rounding.
     * 
     * @return the rounding
     */
    public static int getRounding() {
	return _rounding;
    }

    /**
     * Sets the rounding.
     * 
     * @param rounding
     *            the new rounding
     */
    public static void setRounding(int rounding) {
	_rounding = rounding;
    }

    /** The _delegate. */
    protected TimeBarViewerDelegate _delegate;

    /** The _image registry. */
    protected ImageRegistry _imageRegistry;

    /** The gradient end color. */
    protected int gradientEndColor = SWT.COLOR_GREEN;

    /** The gradient inrange end color. */
    protected int gradientInrangeEndColor = SWT.COLOR_YELLOW;

    /** The gradient start color. */
    protected int gradientStartColor = SWT.COLOR_WHITE;

    /** The selected attributes. */
    private Set<Attribute> selectedAttributes = new HashSet<Attribute>();

    /** The show attribute selection. */
    private boolean showAttributeSelection;

    /** The show border. */
    private boolean showBorder;

    /** The show text. */
    private boolean showText;

    /**
     * Instantiates a new annotation interval render.
     */
    public AnnotationIntervalRender() {
	super(null);

    }

    /**
     * Instantiates a new annotation interval render.
     * 
     * @param printer
     *            the printer
     */
    public AnnotationIntervalRender(Printer printer) {
	super(printer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#contains(de.jaret .util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
	return contains(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2#contains(de.jaret .util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public boolean contains(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {

	boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
	Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
	return iRect.contains(drawingArea.x + x, drawingArea.y + y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#createPrintrenderer (org.eclipse.swt.printing.Printer)
     */
    public TimeBarRenderer createPrintrenderer(Printer printer) {
	AnnotationIntervalRender renderer = new AnnotationIntervalRender(printer);
	return renderer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#dispose()
     */
    public void dispose() {
	if (_imageRegistry != null) {
	    _imageRegistry.dispose();
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#draw(org.eclipse .swt.graphics.GC, org.eclipse.swt.graphics.Rectangle, de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, boolean, boolean, boolean)
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected, boolean printing, boolean overlap) {
	_delegate = delegate;
	if (showAttributeSelection) {
	    if (interval instanceof AnnotationIntervalImpl) {
		AnnotationIntervalImpl annotationIntervalImpl = (AnnotationIntervalImpl) interval;
		for (Attribute attribute : annotationIntervalImpl.getAnnotation().getAttributes()) {
		    if (selectedAttributes.contains(attribute)) {
			drawAnnotationInterval(gc, drawingArea, delegate, interval, selected, printing, overlap);
		    }
		}
	    }
	} else {
	    drawAnnotationInterval(gc, drawingArea, delegate, interval, selected, printing, overlap);
	}

    }

    /**
     * Draw annotation interval.
     * 
     * @param gc
     *            the gc
     * @param drawingArea
     *            the drawing area
     * @param delegate
     *            the delegate
     * @param interval
     *            the interval
     * @param selected
     *            the selected
     * @param printing
     *            the printing
     * @param overlap
     *            the overlap
     */
    private void drawAnnotationInterval(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected, boolean printing, boolean overlap) {
	boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

	drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);

	Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

	Color bg = gc.getBackground();
	String str = interval.toString();

	gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));

	Pattern pattern = new Pattern(gc.getDevice(), 0, iRect.y, 0, iRect.y + (iRect.height * 2), gc.getDevice().getSystemColor(this.gradientStartColor), gc.getDevice().getSystemColor(this.gradientEndColor));

	gc.setBackgroundPattern(pattern);

	if (_rounding == 0) {
	    gc.fillRectangle(iRect);
	    if (showBorder) {
		gc.drawRectangle(iRect);
	    }
	} else {
	    gc.fillRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
	    if (showBorder) {
		gc.drawRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
	    }
	}
	if (showText) {
	    if (horizontal) {
		SwtGraphicsHelper.drawStringCentered(gc, str, iRect);
	    } else {
		SwtGraphicsHelper.drawStringVertical(gc, str, iRect.x + 2, iRect.y + 2);
	    }
	}
	gc.setBackgroundPattern(null);
	pattern.dispose();
	int h;
	if (showText) {
	    h = 3;
	} else {
	    h = 7;
	}
	if (interval instanceof AnnotationIntervalImpl) {
	    AnnotationIntervalImpl annotationIntervalImpl = (AnnotationIntervalImpl) interval;
	    int count = annotationIntervalImpl.getAnnotation().getAttributes().size();

	    if ((count > 0) && (iRect.width > 3)) {
		int w = (iRect.width - 3) / count;
		int startX = 2;
		for (Attribute attribute : annotationIntervalImpl.getAnnotation().getAttributes()) {
		    String[] rgb = attribute.getColorName().split(",");
		    Color currentColor = new Color(Display.getDefault(), new RGB(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2])));
		    gc.setBackground(currentColor);
		    gc.fillRectangle(iRect.x + startX, (iRect.y + iRect.height) - 2 - h, w, h);
		    startX += w;
		    currentColor.dispose();
		}
	    }

	}
	if (selected && !printing) {
	    gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_RED));
	    gc.setAlpha(90);
	    if (_rounding == 0) {
		gc.fillRectangle(iRect);
	    } else {
		gc.fillRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
	    }
	    gc.setAlpha(255);
	}

	gc.setBackground(bg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#getContainingRectangle (de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, boolean)
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
	return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2# getContainingRectangle(de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, boolean)
     */
    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, boolean overlapping) {

	boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
	Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
	return iRect;
    }

    /**
     * Gets the gradient end color.
     * 
     * @return the gradient end color
     */
    public int getGradientEndColor() {
	return gradientEndColor;
    }

    /**
     * Gets the gradient start color.
     * 
     * @return the gradient start color
     */
    public int getGradientStartColor() {
	return gradientStartColor;
    }

    /**
     * Gets the image registry.
     * 
     * @return the image registry
     */
    protected ImageRegistry getImageRegistry() {
	return _imageRegistry;
    }

    /**
     * Gets the i rect.
     * 
     * @param horizontal
     *            the horizontal
     * @param drawingArea
     *            the drawing area
     * @param overlap
     *            the overlap
     * @return the i rect
     */
    protected Rectangle getIRect(boolean horizontal, Rectangle drawingArea, boolean overlap) {
	if (horizontal) {
	    int borderHeight = (int) ((drawingArea.height * BORDERFACTOR) / 2);
	    int height = drawingArea.height - (overlap ? 0 : 2 * borderHeight);
	    int y = drawingArea.y + (overlap ? 0 : borderHeight);
	    return new Rectangle(drawingArea.x, y, drawingArea.width - 1, height - 1);
	} else {
	    int borderWidth = (int) ((drawingArea.width * BORDERFACTOR) / 2);
	    int width = drawingArea.width - (overlap ? 0 : 2 * borderWidth);
	    int x = drawingArea.x + (overlap ? 0 : borderWidth);
	    return new Rectangle(x, drawingArea.y, width - 1, drawingArea.height - 1);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2# getPreferredDrawingBounds(org.eclipse.swt.graphics.Rectangle, de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, boolean, boolean, boolean)
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected, boolean printing, boolean overlap) {

	if (intervalDrawingArea.width < PREFWIDTH) {
	    int diff = (PREFWIDTH - (intervalDrawingArea.width / 2));
	    return new Rectangle(intervalDrawingArea.x - diff, intervalDrawingArea.y, intervalDrawingArea.width + (2 * diff), intervalDrawingArea.height);
	} else {
	    return intervalDrawingArea;
	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer#getToolTipText (de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
	return getToolTipText(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2#getToolTipText (de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.Interval, org.eclipse.swt.graphics.Rectangle, int, int, boolean)
     */
    public String getToolTipText(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
	if (contains(delegate, interval, drawingArea, x, y, overlapping)) {
	    return interval.toString();
	}
	return null;
    }

    /**
     * Checks if is show attribute selection.
     * 
     * @return true, if is show attribute selection
     */
    public boolean isShowAttributeSelection() {
	return showAttributeSelection;
    }

    /**
     * Checks if is show border.
     * 
     * @return true, if is show border
     */
    public boolean isShowBorder() {
	return showBorder;
    }

    /**
     * Checks if is show text.
     * 
     * @return true, if is show text
     */
    public boolean isShowText() {
	return showText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui. IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	if ((part instanceof CodingExplorerViewPart) && (selection instanceof IStructuredSelection)) {
	    selectedAttributes.clear();
	    if (selection.isEmpty() || (!(((IStructuredSelection) selection).getFirstElement() instanceof AttributeNode))) {
		return;
	    }
	    @SuppressWarnings("unchecked")
	    Iterator<AttributeNode> iterator = ((IStructuredSelection) selection).iterator();
	    while (iterator.hasNext()) {
		AttributeNode node = iterator.next();
		if (!selectedAttributes.contains(node.getResource())) {
		    selectedAttributes.add(node.getResource());
		}

	    }
	}
    }

    /**
     * Sets the gradient end color.
     * 
     * @param gradientEndColor
     *            the new gradient end color
     */
    public void setGradientEndColor(int gradientEndColor) {
	this.gradientEndColor = gradientEndColor;
    }

    /**
     * Sets the gradient start color.
     * 
     * @param gradientStartColor
     *            the new gradient start color
     */
    public void setGradientStartColor(int gradientStartColor) {
	this.gradientStartColor = gradientStartColor;
    }

    /**
     * Sets the show attribute selection.
     * 
     * @param showAttributeSelection
     *            the new show attribute selection
     */
    public void setShowAttributeSelection(boolean showAttributeSelection) {
	this.showAttributeSelection = showAttributeSelection;
    }

    /**
     * Sets the show border.
     * 
     * @param showBorder
     *            the new show border
     */
    public void setShowBorder(boolean showBorder) {
	this.showBorder = showBorder;
    }

    /**
     * Sets the show text.
     * 
     * @param showText
     *            the new show text
     */
    public void setShowText(boolean showText) {
	this.showText = showText;
    }

}
