package de.jaret.examples.timebars.events.model;

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

        DefaultRowHeader header = new DefaultRowHeader("r0");
        DefaultTimeBarRowModel tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);

        IntervalImpl interval = new IntervalImpl(date.copy(), date.copy().advanceMinutes(30));
        tbr.addInterval(interval);
        
        SampleEvent event = new SampleEvent(date.copy().advanceMinutes(length));
        event.setLabel("label 1");
        tbr.addInterval(event);
        
        
        header = new DefaultRowHeader("r1");
        tbr = new DefaultTimeBarRowModel(header);
        model.addRow(tbr);
        event = new SampleEvent(date.copy().advanceMinutes(length));
        event.setLabel("label 2");
        tbr.addInterval(event);
        event = new SampleEvent(date.copy().advanceMinutes(length*2));
        event.setLabel("label 3");
        tbr.addInterval(event);

        // add some empty rows
        for(int i=2;i<=16;i++) {
            header = new DefaultRowHeader("r"+i);
            tbr = new DefaultTimeBarRowModel(header);
            model.addRow(tbr);
        }

        return model;
    }

}
