package synergyviewcommons.collections;

import java.util.EventObject;

/**
 * The Class CollectionChangeEvent.
 */
public class CollectionChangeEvent extends EventObject {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5462704454585023407L;

    /** The diff. */
    private CollectionDiff<?> diff;

    /**
     * Instantiates a new collection change event.
     * 
     * @param source
     *            the source
     * @param diff
     *            the diff
     */
    public CollectionChangeEvent(Object source, CollectionDiff<?> diff) {
	super(source);
	this.diff = diff;
    }

    /**
     * Gets the list diff.
     * 
     * @return the list diff
     */
    public CollectionDiff<?> getListDiff() {
	return this.diff;
    }

}
