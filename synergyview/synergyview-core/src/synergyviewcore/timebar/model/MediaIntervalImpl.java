package synergyviewcore.timebar.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import synergyviewcore.collections.model.CollectionMedia;
import synergyviewcore.media.model.AbstractMedia;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * The Class MediaIntervalImpl.
 */
public class MediaIntervalImpl extends IntervalImpl {
	
	/** The Constant MEDIA_START_TIME. */
	public static final JaretDate MEDIA_START_TIME = new JaretDate().setTime(0,
			0, 0, 0);
	
	/** The Constant PROP_MUTE. */
	public static final String PROP_MUTE = "mute";
	
	/** The _owner. */
	private DefaultTimeBarRowModel _owner;
	
	/** The abstract media. */
	protected AbstractMedia abstractMedia;
	
	/** The info. */
	protected String info;
	
	/** The label. */
	protected String label;
	
	/** The media. */
	protected CollectionMedia media;
	
	/** The time bar viewer. */
	protected TimeBarViewer timeBarViewer;
	
	/**
	 * Instantiates a new media interval impl.
	 * 
	 * @param begin
	 *            the begin
	 * @param end
	 *            the end
	 */
	public MediaIntervalImpl(JaretDate begin, JaretDate end) {
		super(begin, end);
	}
	
	/**
	 * Instantiates a new media interval impl.
	 * 
	 * @param timeBarViewer
	 *            the time bar viewer
	 * @param media
	 *            the media
	 * @param ownerValue
	 *            the owner value
	 * @param abstractMedia
	 *            the abstract media
	 */
	public MediaIntervalImpl(TimeBarViewer timeBarViewer,
			CollectionMedia media, DefaultTimeBarRowModel ownerValue,
			AbstractMedia abstractMedia) {
		super();
		this.media = media;
		this.abstractMedia = abstractMedia;
		this.timeBarViewer = timeBarViewer;
		_owner = ownerValue;
		this.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName() == MediaIntervalImpl.PROP_BEGIN) {
					MediaIntervalImpl.this.media
							.setOffSet(MediaIntervalImpl.this.getBegin()
									.getDate().getTime()
									- MEDIA_START_TIME.copy().getDate()
											.getTime());
				}
			}
			
		});
	}
	
	/**
	 * Gets the collection media.
	 * 
	 * @return the collection media
	 */
	public CollectionMedia getCollectionMedia() {
		return media;
	}
	
	/**
	 * Gets the info.
	 * 
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}
	
	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public DefaultTimeBarRowModel getOwner() {
		return _owner;
	}
	
	/**
	 * Checks if is media mute.
	 * 
	 * @return true, if is media mute
	 */
	public boolean isMediaMute() {
		return abstractMedia.isMute();
	}
	
	/**
	 * Sets the collection media.
	 * 
	 * @param media
	 *            the new collection media
	 */
	public void setCollectionMedia(CollectionMedia media) {
		this.media = media;
	}
	
	/**
	 * Sets the info.
	 * 
	 * @param info
	 *            the new info
	 */
	public void setInfo(String info) {
		this.info = info;
	}
	
	/**
	 * Sets the label.
	 * 
	 * @param label
	 *            the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	
	/**
	 * Sets the mute.
	 * 
	 * @param b
	 *            the new mute
	 */
	public void setMute(boolean b) {
		this.media.setMute(b);
		boolean mute = this.abstractMedia.isMute();
		this.abstractMedia.setMute(b);
		this.firePropertyChange(PROP_MUTE, mute, this.abstractMedia.isMute());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.jaret.util.date.IntervalImpl#toString()
	 */
	@Override
	public String toString() {
		return label != null ? label : super.toString();
	}
	
}
