package synergyviewcore.media.model;

import java.net.URI;

import synergyviewcore.model.ModelObject;

/**
 * The Class AbstractMedia.
 */
public abstract class AbstractMedia extends ModelObject implements IMedia {
	
	/** The media url. */
	protected URI mediaUrl;
	
	/** The name. */
	protected String name;
	
	/**
	 * Instantiates a new abstract media.
	 */
	@SuppressWarnings("unused")
	private AbstractMedia() {
		//
	}
	
	/**
	 * Instantiates a new abstract media.
	 * 
	 * @param mediaUrl
	 *            the media url
	 * @param name
	 *            the name
	 */
	public AbstractMedia(URI mediaUrl, String name) {
		this.mediaUrl = mediaUrl;
		this.name = name;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.media.model.IMedia#getName()
	 */
	public String getName() {
		return name;
	}
}
