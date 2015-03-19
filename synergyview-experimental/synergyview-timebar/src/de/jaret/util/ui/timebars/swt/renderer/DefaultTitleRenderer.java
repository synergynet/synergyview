/*
 *  File: DefaultTitleRenderer.java 
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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.ResourceImageDescriptor;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;

/**
 * A default title renderer. Prints the text centered in the area using a bigger font. Can additionally render an image
 * as background.
 * 
 * @author Peter Kliem
 * @version $Id: DefaultTitleRenderer.java 396 2007-05-01 12:03:44Z olk $
 */
public class DefaultTitleRenderer extends RendererBase implements TitleRenderer {

    /** fontsize to use. */
    private static final int FONTSIZE = 14;

    /** key for image registry. */
    protected static final String BACKGROUND = "background";
    /** image registry for holding the images. */
    private ImageRegistry _imageRegistry;

    /** path of the background image ressource. */
    protected String _backgroundRscName;

    /** font to use. */
    protected Font _titleFont;

    /**
     * Constructor for printing renderer.
     * 
     * @param printer printer device
     */
    public DefaultTitleRenderer(Printer printer) {
        super(printer);
        FontData fontdata = new FontData("Arial", FONTSIZE, SWT.BOLD);
        _titleFont = new Font(printer, fontdata);
    }

    /**
     * Constructor for display rendering.
     * 
     */
    public DefaultTitleRenderer() {
        super(null);
        FontData fontdata = new FontData("Arial", FONTSIZE, SWT.BOLD);
        _titleFont = new Font(Display.getCurrent(), fontdata);
    }

    /**
     * {@inheritDoc}
     */
    public void draw(GC gc, Rectangle drawingArea, TimeBarViewerDelegate delegate, String title, boolean printing) {
        // draw background image if set
        Image img = null;
        ImageRegistry registry = getImageRegistry();
        if (registry != null) {
            img = getImageRegistry().get(BACKGROUND);
            if (img != null && drawingArea.width > 0 && drawingArea.height > 0) {
                gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, drawingArea.x, drawingArea.y,
                        drawingArea.width - 1, drawingArea.height - 1);
            }
        }

        if (title != null) {
            Font oldfont = gc.getFont();
            gc.setFont(_titleFont);
            if (img == null) {
                gc.fillRectangle(drawingArea.x, drawingArea.y, drawingArea.width - 1, drawingArea.height - 1);
            }
            SwtGraphicsHelper.drawStringCentered(gc, title, drawingArea);
            gc.setFont(oldfont);
        }

        // draw a closing line
        if (delegate.getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_TOP) {
            gc.drawLine(drawingArea.x, drawingArea.y + drawingArea.height - 1, drawingArea.x + drawingArea.width,
                    drawingArea.y + drawingArea.height - 1);
        } else if (delegate.getTimeScalePosition() == TimeBarViewerInterface.TIMESCALE_POSITION_BOTTOM) {
            gc.drawLine(drawingArea.x, drawingArea.y, drawingArea.x + drawingArea.width, drawingArea.y);
        }

    }

    /**
     * {@inheritDoc}
     */
    public TitleRenderer createPrintRenderer(Printer printer) {
        DefaultTitleRenderer renderer = new DefaultTitleRenderer(printer);
        renderer.setBackgroundRscName(getBackgroundRscName());
        return renderer;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        _titleFont.dispose();
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
            _imageRegistry = null;
        }
    }

    /**
     * Retrieve name of ressource for the background image.
     * 
     * @return ressource path
     */
    public String getBackgroundRscName() {
        return _backgroundRscName;
    }

    /**
     * Set background ressource path. Can be problematic in an eclipse RCP.Use the setBackgroundImageDescriptor instead.
     * 
     * @param backgroundRscName rsc path
     */
    public void setBackgroundRscName(String backgroundRscName) {
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
        }
        _backgroundRscName = backgroundRscName;
    }

    /**
     * Set the bakground imgae by setting an imgae descriptor.
     * 
     * @param descriptor imgae descriptor
     */
    public void setBackgroundImageDescriptor(ImageDescriptor descriptor) {
        if (_imageRegistry != null) {
            _imageRegistry.dispose();
        }
        _imageRegistry = new ImageRegistry();
        _imageRegistry.put(BACKGROUND, descriptor);
    }

    /**
     * Retrieve initialized image registry.
     * 
     * @return initialized registry
     */
    private ImageRegistry getImageRegistry() {
        if (_imageRegistry == null && _backgroundRscName != null) {
            _imageRegistry = new ImageRegistry();
            ImageDescriptor imgDesc = new ResourceImageDescriptor(_backgroundRscName, this.getClass());
            _imageRegistry.put(BACKGROUND, imgDesc.createImage());
        }
        return _imageRegistry;
    }

}
