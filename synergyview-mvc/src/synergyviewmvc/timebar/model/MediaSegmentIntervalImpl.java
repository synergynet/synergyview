package synergyviewmvc.timebar.model;

import synergyviewmvc.collections.model.CollectionMediaClip;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

public class MediaSegmentIntervalImpl extends IntervalImpl {
	
	protected String label;
	protected CollectionMediaClip _collectionMediaClip;
	private DefaultTimeBarRowModel _owner; 

	
    protected MediaSegmentIntervalImpl() {
        super();
    }
    
    public MediaSegmentIntervalImpl(JaretDate begin, JaretDate end, CollectionMediaClip collectionMediaClipValue, DefaultTimeBarRowModel ownerValue) {
        super(begin, end);
        _collectionMediaClip = collectionMediaClipValue;
        _owner = ownerValue;
    }
    
    
    public CollectionMediaClip getCollectionMediaClip() {
    	return _collectionMediaClip;
    }
    
    @Override
    public void setEnd(JaretDate end) {
    	super.setEnd(end);
    	
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return this.label;
    }
    
	public DefaultTimeBarRowModel getOwner() {
		return _owner;
	}
    
    @Override
    public String toString() {
        return label!=null?label:super.toString();
    }

}
