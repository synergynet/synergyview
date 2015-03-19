package de.jaret.examples.timebars.simple.swt.renderer;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import de.jaret.util.date.Interval;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.swt.renderer.DefaultRenderer;

public class OtherIntervalRenderer extends DefaultRenderer{
	private final static int PREFWIDTH = 4;
	
	@Override
	public Rectangle getPreferredDrawingBounds(Rectangle intervalDrawingArea,
			TimeBarViewerDelegate delegate, Interval interval,
			boolean selected, boolean printing, boolean overlap) {

	if (intervalDrawingArea.width<PREFWIDTH) {
		int diff = (PREFWIDTH-intervalDrawingArea.width/2);
		return new Rectangle(intervalDrawingArea.x-diff, intervalDrawingArea.y, intervalDrawingArea.width+2*diff, intervalDrawingArea.height);
	} else {
		return intervalDrawingArea;
	}
	
	}
	
	@Override
	public void draw(GC gc, Rectangle drawingArea,
			TimeBarViewerDelegate delegate, Interval interval,
			boolean selected, boolean printing, boolean overlap) {

		if (drawingArea.width<PREFWIDTH) {
			int diff = (PREFWIDTH-drawingArea.width/2);
			drawingArea =  new Rectangle(drawingArea.x-diff, drawingArea.y, drawingArea.width+2*diff, drawingArea.height);
		} 
		
		super.draw(gc, drawingArea, delegate, interval, selected, printing, overlap);
	}
	
}
