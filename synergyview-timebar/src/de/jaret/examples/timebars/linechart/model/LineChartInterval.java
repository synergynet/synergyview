package de.jaret.examples.timebars.linechart.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

/**
 * Interval holding data points. Begin and end will be adjusted by the first and last data point.
 * 
 * @author kliem
 * @version $Id: LineChartInterval.java 856 2009-04-02 18:54:40Z kliem $
 */
public class LineChartInterval extends IntervalImpl {
    // raw points
    private List<DataPoint> _dataPoints;

    // navigable map for quicker lookup
    private TreeMap<Long, DataPoint> _dataMap = new TreeMap<Long, DataPoint>();
    
    public LineChartInterval(List<DataPoint> points) {
        _dataPoints = points;
        if (_dataPoints.size() < 2) {
            throw new RuntimeException("supply at least two points");
        }
        
        for (DataPoint dataPoint : points) {
            _dataMap.put(dataPoint.getTime().getDate().getTime(), dataPoint);
        }
        
    }

    public List<DataPoint> getDataPoints() {
        return _dataPoints;
    }

    /**
     * {@inheritDoc} Always return the time of the first data point.
     */
    public JaretDate getBegin() {
        return _dataPoints.get(0).getTime();
    }

    /**
     * {@inheritDoc} Always return the time of the last data point.
     */
    public JaretDate getEnd() {
        return _dataPoints.get(_dataPoints.size() - 1).getTime();
    }

    public List<DataPoint> getDataPoints(JaretDate startDate, JaretDate endDate) {
        List<DataPoint> result = new ArrayList<DataPoint>();
        
// 1.6        result.addAll(_dataMap.subMap(startDate.getDate().getTime(), true, endDate.getDate().getTime(), true).values());
        result.addAll(_dataMap.subMap(startDate.getDate().getTime(), endDate.getDate().getTime()).values());
        
        return result;
    }

}
