package synergyviewcommons.collections;

public class CollectionDiffImpl<R> extends CollectionDiff<R> {
	private CollectionDiffEntry<R>[] entries;
	public CollectionDiffImpl(CollectionDiffEntry<R>[] entries) {
		this.entries = entries;
	}
	
	@Override
	public CollectionDiffEntry<R>[] getDifferences() {
		return this.entries;
	}
}
