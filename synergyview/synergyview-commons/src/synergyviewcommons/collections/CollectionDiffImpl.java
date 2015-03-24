package synergyviewcommons.collections;

/**
 * The Class CollectionDiffImpl.
 *
 * @param <R> the generic type
 */
public class CollectionDiffImpl<R> extends CollectionDiff<R> {
	
	/** The entries. */
	private CollectionDiffEntry<R>[] entries;
	
	/**
	 * Instantiates a new collection diff impl.
	 *
	 * @param entries the entries
	 */
	public CollectionDiffImpl(CollectionDiffEntry<R>[] entries) {
		this.entries = entries;
	}
	
	/* (non-Javadoc)
	 * @see synergyviewcommons.collections.CollectionDiff#getDifferences()
	 */
	@Override
	public CollectionDiffEntry<R>[] getDifferences() {
		return this.entries;
	}
}
