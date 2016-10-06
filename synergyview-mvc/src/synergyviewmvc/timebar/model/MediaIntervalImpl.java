package synergyviewmvc.timebar.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import synergyviewmvc.collections.model.CollectionMedia;
import synergyviewmvc.media.model.AbstractMedia;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class MediaIntervalImpl extends IntervalImpl {
	
	public static final String PROP_MUTE = "mute";
	public static final JaretDate MEDIA_START_TIME= new JaretDate().setTime(0, 0, 0, 0);
	protected CollectionMedia media;
	protected AbstractMedia abstractMedia;
	private DefaultTimeBarRowModel _owner; 
	protected String label;
	protected String info;
	protected TimeBarViewer timeBarViewer;
	
    public MediaIntervalImpl(TimeBarViewer timeBarViewer, CollectionMedia media, DefaultTimeBarRowModel ownerValue, AbstractMedia abstractMedia) {
        super();
        this.media = media;
        this.abstractMedia = abstractMedia;
        this.timeBarViewer = timeBarViewer; 
        _owner = ownerValue;
        this.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName() == MediaIntervalImpl.PROP_BEGIN) {
					MediaIntervalImpl.this.media.setOffSet(MediaIntervalImpl.this.getBegin().getDate().getTime() - MEDIA_START_TIME.copy().getDate().getTime());
				}
			}
        	
        });
    }
    
    public MediaIntervalImpl(JaretDate begin, JaretDate end) {
        super(begin, end);
    }
    
	public CollectionMedia getCollectionMedia() {
		return media;
	}
	
	public void setCollectionMedia(CollectionMedia media) {
		this.media = media;
	}
	
	
	 public void setLabel(String label) {
	        this.label = label;
	 }
	    
	@Override
	public String toString() {
	    return label!=null?label:super.toString();
	}
		
	public String getInfo() {
		return info;
	}
	
	public void setInfo(String info) {
		this.info = info;
	}
	
	
	public DefaultTimeBarRowModel getOwner() {
		return _owner;
	}	
	
	public boolean isMediaMute(){
		return abstractMedia.isMute();
	}
	
	public void setMute(boolean b){
		this.media.setMute(b);
		boolean mute = this.abstractMedia.isMute();
		this.abstractMedia.setMute(b);
		this.firePropertyChange(PROP_MUTE, mute, this.abstractMedia.isMute());
	}

}
