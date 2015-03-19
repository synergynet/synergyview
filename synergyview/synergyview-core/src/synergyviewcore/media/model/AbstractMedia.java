package synergyviewcore.media.model;

import java.net.URI;

import synergyviewcore.model.ModelObject;

public abstract class AbstractMedia extends ModelObject implements IMedia {
	protected URI mediaUrl;
	protected String name;
	
	public AbstractMedia(URI mediaUrl, String name) {
		this.mediaUrl = mediaUrl;
		this.name = name;
	}
	
	@SuppressWarnings("unused")
	private AbstractMedia() {
		//
	}

	public String getName() {
		return name;
	}
}
