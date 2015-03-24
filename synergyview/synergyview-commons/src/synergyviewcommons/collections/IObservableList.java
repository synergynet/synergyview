package synergyviewcommons.collections;

import java.util.List;

/**
 * The Interface IObservableList.
 *
 * @param <R> the generic type
 * @param <E> the element type
 */
public interface IObservableList<R extends List<E>, E> extends List<E>, ICollectionObservable {
	
	/**
	 * Gets the read only list.
	 *
	 * @return the read only list
	 */
	public List<E> getReadOnlyList();
}
