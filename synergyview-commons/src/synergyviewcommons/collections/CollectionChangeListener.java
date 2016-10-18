package synergyviewcommons.collections;

/**
 * The listener interface for receiving collectionChange events. The class that is interested in processing a collectionChange event implements this interface, and the object created with that class is registered with a component using the component's <code>addCollectionChangeListener<code> method. When
 * the collectionChange event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see CollectionChangeEvent
 */
public interface CollectionChangeListener {

    /**
     * List changed.
     * 
     * @param event
     *            the event
     */
    public void listChanged(CollectionChangeEvent event);
}
