package synergyviewcore.controller;


public class ModelPersistenceException extends Exception {
	private static final long serialVersionUID = 6375592867490400957L;
	
	public ModelPersistenceException(String string, Exception e) {
		super(string, e);
	}

}
