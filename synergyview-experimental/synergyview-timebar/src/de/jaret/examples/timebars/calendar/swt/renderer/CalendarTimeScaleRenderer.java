package de.jaret.examples.timebars.calendar.swt.renderer;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TickScaler.Range;
import de.jaret.util.ui.timebars.swt.renderer.OldDefaultTimeScaleRenderer;

/**
 * Timescale renderer for the calendar example. This is a simple extension of the default timescale renderer supplying
 * another tooltip.
 * 
 * @author kliem
 * @version $Id: CalendarTimeScaleRenderer.java 841 2009-02-17 21:17:42Z kliem $
 */
public class CalendarTimeScaleRenderer extends OldDefaultTimeScaleRenderer {

    @Override
    protected String getToolTipTextForDate(JaretDate date, Range range) {
        return date.toDisplayStringTime();
    }
}
