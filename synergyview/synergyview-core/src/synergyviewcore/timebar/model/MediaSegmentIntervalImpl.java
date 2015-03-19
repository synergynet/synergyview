package synergyviewcore.timebar.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionMediaClipRowModel;
import synergyviewcore.collections.model.CollectionNode;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;

public class MediaSegmentIntervalImpl extends IntervalImpl {
	
	protected String label;
	protected CollectionNode collectionNode;
	private CollectionMediaClip collectionMediaClip;
	private CollectionMediaClipRowModel owner; 
	private PropertyChangeListener listener;
	
    protected MediaSegmentIntervalImpl() {
        super();
    }
    
    public MediaSegmentIntervalImpl(JaretDate begin, JaretDate end, CollectionNode collectionNode, CollectionMediaClip collectionMediaClip, CollectionMediaClipRowModel owner) {
        super(begin, end);
        this.collectionNode = collectionNode;
        this.collectionMediaClip = collectionMediaClip;
        this.owner = owner;
        
        init();
    }
    
    private void init() {
    	
    	listener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				MediaSegmentIntervalImpl.this.setLabel(collectionMediaClip.getClipName());
			}
    		
    	};
    	collectionMediaClip.addPropertyChangeListener(CollectionMediaClip.PROP_CLIP_NAME, listener);
    }
    
    public CollectionMediaClip getCollectionMediaClip() {
    	return collectionMediaClip;
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
		return owner;
	}
    
    @Override
    public String toString() {
        return label!=null?label:super.toString();
    }
    
    public void dispose() {
    	collectionMediaClip.removePropertyChangeListener(CollectionMediaClip.PROP_CLIP_NAME, listener);
    }
    
    public CollectionNode getCollectionNode() {
    	return collectionNode;
    }

}
