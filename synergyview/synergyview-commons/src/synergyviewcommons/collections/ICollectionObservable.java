package synergyviewcommons.collections;

public interface ICollectionObservable {
	public void addChangeListener(CollectionChangeListener listener);
	public void removeChangeListener(CollectionChangeListener listener);
}
