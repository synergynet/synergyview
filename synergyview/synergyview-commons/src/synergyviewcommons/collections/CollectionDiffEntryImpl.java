package synergyviewcommons.collections;

/**
 * The Class CollectionDiffEntryImpl.
 * 
 * @param <R>
 *            the generic type
 */
public class CollectionDiffEntryImpl<R> extends CollectionDiffEntry<R> {
	
	/** The entry. */
	private R entry;
	
	/** The is addition. */
	private boolean isAddition;
	
	/** The position. */
	private int position;
	
	/**
	 * Instantiates a new collection diff entry impl.
	 * 
	 * @param entry
	 *            the entry
	 * @param isAddition
	 *            the is addition
	 * @param position
	 *            the position
	 */
	public CollectionDiffEntryImpl(R entry, boolean isAddition, int position) {
		super();
		this.entry = entry;
		this.isAddition = isAddition;
		this.position = position;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcommons.collections.CollectionDiffEntry#getElement()
	 */
	@Override
	public R getElement() {
		return entry;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcommons.collections.CollectionDiffEntry#getPosition()
	 */
	@Override
	public int getPosition() {
		return position;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcommons.collections.CollectionDiffEntry#isAddition()
	 */
	@Override
	public boolean isAddition() {
		return isAddition;
	}
}
