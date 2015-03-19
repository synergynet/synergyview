package synergyviewcommons.collections;

import java.util.EventObject;

public class CollectionChangeEvent extends EventObject {
	private CollectionDiff<?> diff;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5462704454585023407L;

	public CollectionChangeEvent(Object source, CollectionDiff<?> diff) {
		super(source);
		this.diff = diff;
	}
	
	public CollectionDiff<?> getListDiff() {
		return this.diff;
	}

}
