package synergyviewcore.collection.ui.timebar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.Rectangle;

import synergyviewcore.media.util.DateTimeHelper;
import de.jaret.util.date.Interval;
import de.jaret.util.swt.SwtGraphicsHelper;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.TimeBarViewerInterface;
import de.jaret.util.ui.timebars.swt.renderer.DefaultRenderer;


public class PlayableMediaRenderer extends DefaultRenderer {
	private static int _rounding = 3;
    protected int gradientStartColor = SWT.COLOR_WHITE;
    protected int gradientEndColor = SWT.COLOR_YELLOW;
    
	@Override
	public void draw(GC gc, Rectangle drawingArea,
			TimeBarViewerDelegate delegate, Interval interval,
			boolean selected, boolean printing, boolean overlap) {
		_delegate = delegate;
		
		PlayableMediaIntervalImpl playableMediaInterval = (PlayableMediaIntervalImpl) interval;
        
        boolean horizontal = delegate.getOrientation() == TimeBarViewerInterface.Orientation.HORIZONTAL;

        drawFocus(gc, drawingArea, delegate, interval, selected, printing, overlap);

        Rectangle iRect = getIRect(horizontal, drawingArea, overlap);

        Color bg = gc.getBackground();
        String str = String.format("%s (%s)",playableMediaInterval.getMedia().getId(), DateTimeHelper.getHMSFromMilli(playableMediaInterval.getMedia().getMediaInfo().getLengthInMilliSeconds()));

        Pattern pattern = new Pattern(gc.getDevice(), 0, iRect.y, 0, iRect.y + iRect.height * 2, gc.getDevice()
                .getSystemColor(this.gradientStartColor), gc.getDevice().getSystemColor(this.gradientEndColor));
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
	}

}
