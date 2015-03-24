package synergyviewcommons.collections;

/**
 * The Class CollectionDiff.
 *
 * @param <R> the generic type
 */
public abstract class CollectionDiff<R> {
	
	/**
	 * Gets the differences.
	 *
	 * @return the differences
	 */
	public abstract CollectionDiffEntry<R>[] getDifferences(); 
}
