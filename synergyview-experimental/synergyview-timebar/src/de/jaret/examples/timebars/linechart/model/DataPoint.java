package de.jaret.examples.timebars.linechart.model;

import de.jaret.util.date.JaretDate;

/**
 * Simple data point time/value.
 * 
 * @author kliem
 * @version $Id: DataPoint.java 764 2008-05-22 16:56:02Z kliem $
 */
public class DataPoint {
    private JaretDate _time;
    private int _value;
    
    public DataPoint(JaretDate time, int value) {
        _time = time;
        _value = value;
    }
    public JaretDate getTime() {
        return _time;
    }
    public void setTime(JaretDate time) {
        _time = time;
    }
    public int getValue() {
        return _value;
    }
    public void setValue(int value) {
        _value = value;
    }
    
    
    
}
