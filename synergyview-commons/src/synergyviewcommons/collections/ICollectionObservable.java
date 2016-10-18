package synergyviewcommons.collections;

/**
 * The Interface ICollectionObservable.
 */
public interface ICollectionObservable {

    /**
     * Adds the change listener.
     * 
     * @param listener
     *            the listener
     */
    public void addChangeListener(CollectionChangeListener listener);

    /**
     * Removes the change listener.
     * 
     * @param listener
     *            the listener
     */
    public void removeChangeListener(CollectionChangeListener listener);
}
