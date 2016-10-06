package synergyviewcommons.collections;

import java.util.EventObject;
import java.util.List;

public class ListChangeEvent extends EventObject {
	private ListDiff<?> diff;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5462704454585023407L;

	public ListChangeEvent(List<?> source, ListDiff<?> diff) {
		super(source);
		this.diff = diff;
	}
	
	public ListDiff<?> getListDiff() {
		return this.diff;
	}

}
