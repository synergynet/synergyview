package de.jaret.examples.timebars.linechart.model;

import java.util.ArrayList;
import java.util.List;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;

/**
 * Simple model creator creating a model conatinaing a single row that contains a LineChartInterval filled with some
 * data.
 * 
 * @author kliem
 * @version $Id: ModelCreator.java 766 2008-05-28 21:36:48Z kliem $
 */
public class ModelCreator {
    /** maximum value for the line chart values. */
    public static final double MAX=100.0;
    /**
     * Create the model.
     * 
     * @return a filled time bar model
     */
    public static TimeBarModel createModel() {
        DefaultTimeBarModel model = new DefaultTimeBarModel();

        DefaultTimeBarRowModel row = new DefaultTimeBarRowModel(new DefaultRowHeader("write a renderer"));

        List<DataPoint> points = new ArrayList<DataPoint>();

        JaretDate date = new JaretDate();

        for (int i = 0; i < 5000; i++) {
            int value = (int) (Math.random() * MAX);
            DataPoint dp = new DataPoint(date.copy(), value);
            points.add(dp);
            date.advanceHours(1);
        }

        LineChartInterval interval = new LineChartInterval(points);

        row.addInterval(interval);
        model.addRow(row);

        return model;

    }
}
