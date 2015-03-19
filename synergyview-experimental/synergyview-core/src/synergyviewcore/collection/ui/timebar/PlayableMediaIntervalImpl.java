package synergyviewcore.collection.ui.timebar;

import synergyviewcore.media.model.PlayableMedia;
import de.jaret.util.date.IntervalImpl;

public abstract class PlayableMediaIntervalImpl extends IntervalImpl {
	public abstract void setupModel();
	public abstract PlayableMedia getMedia();
}
