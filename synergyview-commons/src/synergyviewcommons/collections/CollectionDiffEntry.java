package synergyviewcommons.collections;

/**
 * The Class CollectionDiffEntry.
 * 
 * @param <R>
 *            the generic type
 */
public abstract class CollectionDiffEntry<R> {

    /**
     * Gets the element.
     * 
     * @return the element
     */
    public abstract R getElement();

    /**
     * Gets the position.
     * 
     * @return the position
     */
    public abstract int getPosition();

    /**
     * Checks if is addition.
     * 
     * @return true, if is addition
     */
    public abstract boolean isAddition();
}
