package synergyviewcore.media;

import synergyviewcore.controller.ModelPersistenceException;

public class MediaException extends Exception {

	private static final long serialVersionUID = 1L;
	public MediaException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public MediaException(String message) {
		super(message);
	}

	public MediaException(ModelPersistenceException e) {
		super(e);
	}
}
