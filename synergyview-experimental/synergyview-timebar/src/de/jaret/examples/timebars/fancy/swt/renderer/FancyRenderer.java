/*
 *  File: FancyRenderer.java 
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
package de.jaret.examples.timebars.fancy.swt.renderer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

import de.jaret.examples.timebars.fancy.model.FancyInterval;
import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.ResourceImageDescriptor;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swt.renderer.RendererBase;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer;
import de.jaret.util.ui.timebars.swt.renderer.TimeBarRenderer2;

/**
 * Renderer demonstrating some things a render can do ... Please note, that some of the drawing operations require
 * advanced graphics and are not performance optimized. In fact they can be quite slow.
 * 
 * @author Peter Kliem
 * @version $Id: FancyRenderer.java 863 2009-06-22 20:06:19Z kliem $
 */
public class FancyRenderer extends RendererBase implements TimeBarRenderer, TimeBarRenderer2 {
    /** width or height times this factor = percentage used as the non painted border. */
    protected static final double BORDERFACTOR = 0.2;

    /** the additional width requested to draw a label beside the interval. */
    private final static int ADDITONALWIDTH = 100;

    /** cache for the delegate supplying the orientation information. */
    protected TimeBarViewerDelegate _delegate;

    /** image registry for holding the icon. */
    protected ImageRegistry _imageRegistry;

    /** rounding for the drawing rectangles. */
    private static int _rounding = 1;
    /** 0 = normal, 1 = shadow, 2 = reflection. */
    private static int _drawMode = 0;

    /**
     * Create renderer for printing.
     * 
     * @param printer printer device
     */
    public FancyRenderer(Printer printer) {
        super(printer);
    }

    /**
     * Construct renderer for screen use.
     * 
     */
    public FancyRenderer() {
        super(null);
    }

    /**
     * {@inheritDoc} The preferred rendering area is the interval area plus an additional width.
     */
    public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea, TimeBarViewerDelegate delegate,
            Interval interval, boolean selected, boolean printing, boolean overlap) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

        if (horizontal) {
            return new Rectangle(intervalDrawingArea.x, intervalDrawingArea.y, intervalDrawingArea.width
                    + scaleX(ADDITONALWIDTH), intervalDrawingArea.height);
        } else {
            return new Rectangle(intervalDrawingArea.x, intervalDrawingArea.y, intervalDrawingArea.width,
                    intervalDrawingArea.height + scaleY(ADDITONALWIDTH));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval, boolean selected,
            boolean printing, boolean overlap) {
        _delegate = delegate;
        switch (_drawMode) {
        case 0:
            defaultDraw(gc, drawingArea, delegate, interval, selected, printing, overlap);
            break;
        case 1:
            shadowDraw(gc, drawingArea, delegate, interval, selected, printing, overlap);
            break;
        case 2:
            reflectDraw(gc, drawingArea, delegate, interval, selected, printing, overlap);
            break;

        default:
            break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getToolTipText(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return getToolTipText(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Interval interval, Rectangle drawingArea, int x, int y, boolean overlapping) {
        return contains(_delegate, interval, drawingArea, x, y, overlapping);
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle getContainingRectangle(Interval interval, Rectangle drawingArea, boolean overlapping) {
        return getContainingRectangle(_delegate, interval, drawingArea, overlapping);
    }

    /**
     * {@inheritDoc}. Will create print renderes for all registered renderers.
     */
    public TimeBarRenderer createPrintrenderer(Printer printer) {
        FancyRenderer renderer = new FancyRenderer(printer);
        return renderer;
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
     * Draw to an image, calculate reflection and the draw the complete image.
     * 
     * @param gc
     * @param drawingArea
     * @param delegate
     * @param interval
     * @param selected
     * @param printing
     * @param overlap
     */
    private void reflectDraw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean printing, boolean overlap) {

        Rectangle newArea = new Rectangle(drawingArea.x, drawingArea.y, drawingArea.width + scaleX(ADDITONALWIDTH),
                (drawingArea.height / 3) * 2);
        Rectangle na = new Rectangle(0, 0, newArea.width - scaleX(ADDITONALWIDTH), newArea.height);

        Image img = new Image(gc.getDevice(), newArea);
        // copy the background
        gc.copyArea(img, newArea.x, newArea.y);

        GC imageGC = new GC(img);
        defaultDraw(imageGC, na, delegate, interval, selected, printing, true);

        // create the reflection image
        Image reflection = SwtGraphicsHelper.reflect(img, gc.getDevice());

        gc.drawImage(img, drawingArea.x, drawingArea.y);
        gc.drawImage(reflection, drawingArea.x, drawingArea.y + newArea.height);

        img.dispose();
        reflection.dispose();
        imageGC.dispose();
    }

    private void shadowDraw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean printing, boolean overlap) {

        gc.setClipping((Rectangle) null);
        Rectangle newArea = new Rectangle(drawingArea.x, drawingArea.y, drawingArea.width, (drawingArea.height / 3) * 2);
        Rectangle na = new Rectangle(0, 0, newArea.width, newArea.height);

        Image img = new Image(gc.getDevice(), newArea);
        GC imageGC = new GC(img);
        defaultDraw(imageGC, na, delegate, interval, selected, printing, true);
        ImageData shadowImgData = SwtGraphicsHelper.dropShadow(img.getImageData(), gc.getDevice().getSystemColor(
                SWT.COLOR_BLACK), 5, 3, 65);

        Image shadowImg = new Image(gc.getDevice(), shadowImgData);
        gc.drawImage(shadowImg, drawingArea.x, drawingArea.y);

        imageGC.dispose();
        img.dispose();
        shadowImg.dispose();
    }

    /**
     * Drawing method for default rendering.
     * 
     * @param gc GC
     * @param drawingArea drawingArea
     * @param delegate delegate
     * @param interval interval to draw
     * @param selected true for selected drawing
     * @param printing true for printing
     * @param overlap true if the interval overlaps with other intervals
     */
    private void defaultDraw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, Interval interval,
            boolean selected, boolean printing, boolean overlap) {
        // we assume it's a fancy interval
        FancyInterval fi = (FancyInterval) interval;
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

        // draw focus
        drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);

        Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

        Color bg = gc.getBackground();
        String str = interval.toString();

        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_GRAY));

        Pattern pattern = new Pattern(gc.getDevice(), 0, iRect.y, 0, iRect.y + iRect.height * 2, gc.getDevice()
                .getSystemColor(SWT.COLOR_YELLOW), gc.getDevice().getSystemColor(SWT.COLOR_GRAY));

        gc.setBackgroundPattern(pattern);

        if (_rounding == 0) {
            gc.fillRectangle(iRect);
            gc.drawRectangle(iRect);
        } else {
            gc.fillRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
            gc.drawRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
        }
        if (horizontal) {
            SwtGraphicsHelper.drawStringCentered(gc, str, iRect);
        } else {
            SwtGraphicsHelper.drawStringVertical(gc, str, iRect.x + 2, iRect.y + 2);
        }
        gc.setBackgroundPattern(null);
        pattern.dispose();

        // draw text outside the interval
        if (horizontal) {
            SwtGraphicsHelper.drawStringVCentered(gc, fi.getPercentage() + "% complete", iRect.x + iRect.width + 10,
                    iRect.y, iRect.y + iRect.height);
        } else {
            SwtGraphicsHelper.drawStringVertical(gc, fi.getPercentage() + "% complete", iRect.x + 2, iRect.y + iRect.height + 10);
        }
        // draw the percentage bar
        int h = 3;
        gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_DARK_MAGENTA));
        int w = ((iRect.width - 2) * fi.getPercentage()) / 100;
        gc.fillRectangle(iRect.x + 1, iRect.y + iRect.height - 2 - h, w, h);

        if (selected && !printing) {
            gc.setBackground(gc.getDevice().getSystemColor(SWT.COLOR_BLUE));
            gc.setAlpha(60);
            if (_rounding == 0) {
                gc.fillRectangle(iRect);
            } else {
                gc.fillRoundRectangle(iRect.x, iRect.y, iRect.width, iRect.height, _rounding, _rounding);
            }
            gc.setAlpha(255);
        }

        gc.setBackground(bg);

        if (fi.getState()) {
            gc.drawImage(getImageRegistry().getDescriptor("smile").createImage(), iRect.x, iRect.y);
        }

    }

    public String getToolTipText(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x,
            int y, boolean overlapping) {
        if (contains(delegate, interval, drawingArea, x, y, overlapping)) {
            return interval.toString();
        }
        return null;
    }

    public boolean contains(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea, int x, int y,
            boolean overlapping) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect.contains(drawingArea.x + x, drawingArea.y + y);
    }

    public Rectangle getContainingRectangle(TimeBarViewerDelegate delegate, Interval interval, Rectangle drawingArea,
            boolean overlapping) {

        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;
        Rectangle iRect = getIRect(horizontal, drawingArea, overlapping);
        return iRect;
    }

    /**
     * Calculate the actual drawing rectangle for the interval usig the BORDERFACTOR to determine the border.
     * 
     * @param horizontal true for horizontal false for vertical
     * @param drawingArea drawingArea
     * @param overlap true if it is an overlapping interval
     * @return the actual drawing rectangle
     */
    protected Rectangle getIRect(boolean horizontal, Rectangle drawingArea, boolean overlap) {
        if (horizontal) {
            int borderHeight = (int) (drawingArea.height * BORDERFACTOR / 2);
            int height = drawingArea.height - (overlap ? 0 : 2 * borderHeight);
            int y = drawingArea.y + (overlap ? 0 : borderHeight);
            return new Rectangle(drawingArea.x, y, drawingArea.width - 1, height - 1);
        } else {
            int borderWidth = (int) (drawingArea.width * BORDERFACTOR / 2);
            int width = drawingArea.width - (overlap ? 0 : 2 * borderWidth);
            int x = drawingArea.x + (overlap ? 0 : borderWidth);
            return new Rectangle(x, drawingArea.y, width - 1, drawingArea.height - 1);
        }
    }

    /**
     * Retrieve the image registry (lazy creation).
     * 
     * @return the initialized registry
     */
    protected ImageRegistry getImageRegistry() {
        if (_imageRegistry == null) {
            _imageRegistry = new ImageRegistry();
            ImageDescriptor imgDesc = new ResourceImageDescriptor(
                    "/de/jaret/examples/timebars/fancy/swt/renderer/smile.gif");
            _imageRegistry.put("smile", imgDesc);
        }
        return _imageRegistry;
    }

    public static int getRounding() {
        return _rounding;
    }

    public static void setRounding(int rounding) {
        _rounding = rounding;
    }

    public static int getDrawMode() {
        return _drawMode;
    }

    public static void setDrawMode(int mode) {
        _drawMode = mode;
    }

}
