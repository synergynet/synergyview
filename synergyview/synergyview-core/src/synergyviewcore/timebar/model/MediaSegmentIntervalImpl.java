package synergyviewcore.timebar.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionMediaClipRowModel;
import synergyviewcore.collections.model.CollectionNode;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;


/**
 * The Class MediaSegmentIntervalImpl.
 */
public class MediaSegmentIntervalImpl extends IntervalImpl {
	
	/** The label. */
	protected String label;
	
	/** The collection node. */
	protected CollectionNode collectionNode;
	
	/** The collection media clip. */
	private CollectionMediaClip collectionMediaClip;
	
	/** The owner. */
	private CollectionMediaClipRowModel owner; 
	
	/** The listener. */
	private PropertyChangeListener listener;
	
    /**
     * Instantiates a new media segment interval impl.
     */
    protected MediaSegmentIntervalImpl() {
        super();
    }
    
    /**
     * Instantiates a new media segment interval impl.
     *
     * @param begin the begin
     * @param end the end
     * @param collectionNode the collection node
     * @param collectionMediaClip the collection media clip
     * @param owner the owner
     */
    public MediaSegmentIntervalImpl(JaretDate begin, JaretDate end, CollectionNode collectionNode, CollectionMediaClip collectionMediaClip, CollectionMediaClipRowModel owner) {
        super(begin, end);
        this.collectionNode = collectionNode;
        this.collectionMediaClip = collectionMediaClip;
        this.owner = owner;
        
        init();
    }
    
    /**
     * Inits the.
     */
    private void init() {
    	
    	listener = new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent arg0) {
				MediaSegmentIntervalImpl.this.setLabel(collectionMediaClip.getClipName());
			}
    		
    	};
    	collectionMediaClip.addPropertyChangeListener(CollectionMediaClip.PROP_CLIP_NAME, listener);
    }
    
    /**
     * Gets the collection media clip.
     *
     * @return the collection media clip
     */
    public CollectionMediaClip getCollectionMediaClip() {
    	return collectionMediaClip;
    }
    
    /* (non-Javadoc)
     * @see de.jaret.util.date.IntervalImpl#setEnd(de.jaret.util.date.JaretDate)
     */
    @Override
    public void setEnd(JaretDate end) {
    	super.setEnd(end);
    	
    }

    /**
     * Sets the label.
     *
     * @param label the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }
    
	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public DefaultTimeBarRowModel getOwner() {
		return owner;
	}
    
    /* (non-Javadoc)
     * @see de.jaret.util.date.IntervalImpl#toString()
     */
    @Override
    public String toString() {
        return label!=null?label:super.toString();
    }
    
    /**
     * Dispose.
     */
    public void dispose() {
    	collectionMediaClip.removePropertyChangeListener(CollectionMediaClip.PROP_CLIP_NAME, listener);
    }
    
    /**
     * Gets the collection node.
     *
     * @return the collection node
     */
    public CollectionNode getCollectionNode() {
    	return collectionNode;
    }

}
