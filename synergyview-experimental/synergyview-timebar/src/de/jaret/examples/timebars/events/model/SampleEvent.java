package de.jaret.examples.timebars.events.model;

import de.jaret.util.date.Event;
import de.jaret.util.date.JaretDate;

/**
 * An extension of the general event holding a label.
 * 
 * @author kliem
 * @version $Id: FancyEvent.java 563 2007-09-15 18:52:33Z olk $
 */
public class SampleEvent extends Event {
    String _label;

    
    public SampleEvent(JaretDate date) {
        super(date);
    }

    
    public String getLabel() {
        return _label;
    }

    public void setLabel(String label) {
        String oldVal = _label;
        _label = label;
        if (isRealModification(oldVal, label)) {
        	firePropertyChange("Label", null, label);
        }
    }
}
