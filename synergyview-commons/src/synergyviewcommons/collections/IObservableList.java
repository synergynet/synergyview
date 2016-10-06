package synergyviewcommons.collections;

import java.util.List;

public interface IObservableList<R extends List<E>, E> extends List<E> {
	public void addChangeListener(ListChangeListener listener);
	public void removeChangeListener(ListChangeListener listener);
	public List<E> getReadOnlyList();
}
