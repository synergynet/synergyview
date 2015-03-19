package synergyviewcommons.collections;

import java.util.List;

public interface IObservableList<R extends List<E>, E> extends List<E>, ICollectionObservable {
	public List<E> getReadOnlyList();
}
