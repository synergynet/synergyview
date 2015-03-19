package synergyviewmvc.media.model;

import java.net.URI;

import uk.ac.durham.tel.commons.model.PropertySupportObject;

public abstract class AbstractMedia extends PropertySupportObject implements IMedia {
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
