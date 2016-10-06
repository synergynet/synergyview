package de.jaret.examples.timebars.millis.model;

import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarNode;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.PPSInterval;
import de.jaret.util.ui.timebars.model.TimeBarModel;

public class ModelCreator {
    static final long ROWLENGTH = 60 * 1000;
    static final long GAP = 10 * 1000;
    static final int BURSTCOUNT = 4;
    static final long LENGTH = 10;
    static final int ROWCOUNT = 3;
    
    List<JaretDate> _burstBegindates = new ArrayList<JaretDate>();

    public TimeBarModel createModel() {
        DefaultTimeBarModel model = new DefaultTimeBarModel();

        JaretDate start = new JaretDate(0);

        for (int r = 0; r < ROWCOUNT; r++) {
            DefaultRowHeader header = new DefaultRowHeader("r" + r);
            DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);

            JaretDate d = new JaretDate(0);

            while (d.diffMilliSeconds(start) < ROWLENGTH) {
                // remember burst begins
                if (r == 0) {
                    _burstBegindates.add(d.copy());
                }
                for (int i = 0; i < BURSTCOUNT; i++) {
                    IntervalImpl interval = new IntervalImpl();
                    JaretDate begin = d.copy().advanceMillis((long) ((double) LENGTH / 2 * Math.random()));
                    interval.setBegin(begin);
                    interval.setEnd(begin.copy().advanceMillis(1 + (long) ((double) LENGTH * Math.random())));
                    tbr.addInterval(interval);
                }

                // next burst
                d.advanceMillis(GAP);
            }
            model.addRow(tbr);
        }

        System.out.println("model created min " + model.getMinDate() + " max " + model.getMaxDate() + " = total ms "
                + (model.getMaxDate().getDate().getTime() - model.getMinDate().getDate().getTime()));
        System.out.println("diff ms " + model.getMaxDate().diffMilliSeconds(model.getMinDate()));
        return model;
    }

    public void createPPSIntervals(double pixelPerSecond, DefaultTimeBarNode scaleRow) {
        for (int i = 0; i < _burstBegindates.size() - 2; i++) {
            PPSInterval pps = new PPSInterval(pixelPerSecond / 200.0);
            pps.setBegin(_burstBegindates.get(i).copy().advanceMillis(LENGTH * 3));
            pps.setEnd(_burstBegindates.get(i + 1).copy().advanceMillis(-LENGTH * 3));
            // create one break
            if (i==1) {
                pps.setBreak(true);
            }
            System.out.println("pps interval " + pps.getBegin() + " -- " + pps.getEnd());
            scaleRow.addInterval(pps);
        }
    }

    
}
