package de.jaret.examples.timebars.simple.model;

import de.jaret.examples.timebars.simple.OtherIntervalImpl;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;

public class ModelCreator {
    public static TimeBarModel createModel() {
        DefaultTimeBarModel model = new DefaultTimeBarModel();

        
        JaretDate date = new JaretDate();

        int length = 120;

        DefaultRowHeader header = new DefaultRowHeader("r1");
        DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
        IntervalImpl interval = new IntervalImpl();
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(30));
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(60));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length));
        tbr.addInterval(interval);

        // very short interval
        interval = new OtherIntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(60+120+10));
        interval.setEnd(interval.getBegin().copy().advanceMillis(20));
        tbr.addInterval(interval);

        model.addRow(tbr);

        header = new DefaultRowHeader("r2");
        tbr = new DefaultTimeBarRowModel(header);
        interval = new IntervalImpl();
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new IntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(120));
        interval.setEnd(date.copy().advanceMinutes(length + length));
        tbr.addInterval(interval);

        model.addRow(tbr);

        header = new DefaultRowHeader("r3");
        tbr = new DefaultTimeBarRowModel(header);
        interval = new OtherIntervalImpl();
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new OtherIntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(30));
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new OtherIntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(60));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new OtherIntervalImpl();
        interval.setBegin(date.copy().advanceMinutes(90));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length));
        tbr.addInterval(interval);

        model.addRow(tbr);

        // create a row with intervals hitting the dst dates (germany)
//        
//        header = new DefaultRowHeader("DST");
//        tbr = new DefaultTimeBarRowModel(header);
//
//        
//        interval = new OtherIntervalImpl();
//        interval.setBegin(new JaretDate(28, 3, 2009, 23, 0, 0));
//        interval.setEnd(interval.getBegin().copy().advanceMinutes(120));
//        tbr.addInterval(interval);
//
//        interval = new OtherIntervalImpl();
//        interval.setBegin(new JaretDate(29, 3, 2009, 3, 0, 0));
//        interval.setEnd(interval.getBegin().copy().advanceMinutes(120));
//        tbr.addInterval(interval);
//
//        
//        interval = new OtherIntervalImpl();
//        interval.setBegin(new JaretDate(24, 10, 2009, 23,0,0 ));
//        interval.setEnd(interval.getBegin().copy().advanceMinutes(120));
//        tbr.addInterval(interval);
//
//        interval = new OtherIntervalImpl();
//        interval.setBegin(new JaretDate(24,10,2009, 23, 0, 0));
//        interval.setEnd(interval.getBegin().copy().advanceMinutes(180));
//        tbr.addInterval(interval);
//
//        interval = new OtherIntervalImpl();
//        interval.setBegin(new JaretDate(25, 10, 2009, 2, 0, 0));
//        interval.setEnd(interval.getBegin().copy().advanceMinutes(120));
//        tbr.addInterval(interval);
//
//        interval = new OtherIntervalImpl();
//        interval.setBegin(new JaretDate(25, 10, 2009, 3, 0, 0));
//        interval.setEnd(interval.getBegin().copy().advanceMinutes(120));
//        tbr.addInterval(interval);
//        
//        model.addRow(tbr);

        // add some empty rows for drag&drop fun
        for(int rowNumber=4;rowNumber<=20;rowNumber++) {
            header = new DefaultRowHeader("r"+rowNumber);
            tbr = new DefaultTimeBarRowModel(header);
            model.addRow(tbr);
            
        }

        return model;
    }
    
    
    public static TimeBarModel createLargeModel() {
        DefaultTimeBarModel model = new DefaultTimeBarModel();

        JaretDate date = new JaretDate();

        int length = 120;
        double delta = 5.0;

        int rows = 10;
        int intervals = 10;

        for (int r = 0; r < rows; r++) {
            DefaultRowHeader header = new DefaultRowHeader("r" + r);
            DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
            for (int i = 0; i < intervals; i++) {
                IntervalImpl interval = new IntervalImpl();
                interval.setBegin(date.copy().advanceMinutes(Math.random() * delta));
                interval.setEnd(interval.getBegin().copy().advanceMinutes(length));
                tbr.addInterval(interval);
            }
            model.addRow(tbr);

        }

        return model;
    }

}
