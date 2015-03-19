package de.jaret.examples.timebars.fancy.model;

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
        FancyInterval interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy().advanceMinutes(length+10));
        interval.setEnd(date.copy().advanceMinutes(2*length));
        tbr.addInterval(interval);

        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy().advanceMinutes(2*length-20));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length*3));
        tbr.addInterval(interval);

        model.addRow(tbr);

        header = new DefaultRowHeader("r2");
        tbr = new DefaultTimeBarRowModel(header);
        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy().advanceMinutes(120));
        interval.setEnd(date.copy().advanceMinutes(length + length));
        tbr.addInterval(interval);

        model.addRow(tbr);

        header = new DefaultRowHeader("r3");
        tbr = new DefaultTimeBarRowModel(header);
        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy());
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy().advanceMinutes(30));
        interval.setEnd(date.copy().advanceMinutes(length));
        tbr.addInterval(interval);

        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy().advanceMinutes(length*3));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length*4));
        tbr.addInterval(interval);

        interval = new FancyInterval();
        interval.setPercentage((int)(Math.random()*100));
        interval.setBegin(date.copy().advanceMinutes(length*5));
        interval.setEnd(interval.getBegin().copy().advanceMinutes(length*6));
        tbr.addInterval(interval);

        model.addRow(tbr);

        header = new DefaultRowHeader("r4");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);
        FancyEvent event = new FancyEvent(date.copy().advanceMinutes(length));
        event.setLabel("label 1");
        tbr.addInterval(event);
        
        
        header = new DefaultRowHeader("r5");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);
        event = new FancyEvent(date.copy().advanceMinutes(length));
        event.setLabel("label 2");
        tbr.addInterval(event);
        event = new FancyEvent(date.copy().advanceMinutes(length*2));
        event.setLabel("label 3");
        tbr.addInterval(event);

        header = new DefaultRowHeader("r6");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r7");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r8");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r9");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r10");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r11");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r12");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r13");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        header = new DefaultRowHeader("r14");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        return model;
    }

}
