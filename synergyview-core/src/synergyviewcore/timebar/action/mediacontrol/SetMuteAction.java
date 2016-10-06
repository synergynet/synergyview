package synergyviewcore.timebar.action.mediacontrol;

import synergyviewcore.collections.ui.AbstractMediaCollectionControl;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.timebar.action.BaseTimeBarAction;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * The Class SetMuteAction.
 */
public class SetMuteAction extends BaseTimeBarAction {
	
	/** The Constant MUTE_OFF. */
	protected final static String MUTE_OFF = "Mute Off";
	
	/** The Constant MUTE_ON. */
	protected final static String MUTE_ON = "Mute On";
	
	/** The media collection. */
	AbstractMediaCollectionControl mediaCollection;
	
	/** The mute state. */
	protected String muteState = MUTE_OFF;
	
	/**
	 * Instantiates a new sets the mute action.
	 * 
	 * @param tbv
	 *            the tbv
	 * @param medias
	 *            the medias
	 */
	public SetMuteAction(TimeBarViewer tbv,
			AbstractMediaCollectionControl medias) {
		super(tbv);
		mediaCollection = medias;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		return ResourceLoader.getString("TIMEBAR_MEDIA_CONTEXTMENU_MUTE");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
	 */
	@Override
	protected void init() {
		this.setEnabled(true);
		this.setToolTipText(ResourceLoader
				.getString("TIMEBAR_MEDIA_CONTEXTMENU_DELETE"));
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void run() {
		
		// List<Interval> intervals =
		// _tbv.getSelectionModel().getSelectedIntervals();
		// for (Interval interval:intervals){
		// CollectionMedia media =
		// ((MediaIntervalImpl)interval).getCollectionMedia();
		// if (muteState.equals(MUTE_ON)){
		// media.getMedia().setMute(false);
		// muteState=MUTE_OFF;
		// }
		// else{
		// media.getMedia().setMute(true);
		// muteState=MUTE_ON;
		// }
		// }
		
	}
	
}
