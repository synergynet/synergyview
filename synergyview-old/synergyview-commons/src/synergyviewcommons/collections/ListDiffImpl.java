package synergyviewcommons.collections;

public class ListDiffImpl<R> extends ListDiff<R> {
	private ListDiffEntry<R>[] entries;
	public ListDiffImpl(ListDiffEntry<R>[] entries) {
		this.entries = entries;
	}
	
	@Override
	public ListDiffEntry<R>[] getDifferences() {
		return this.entries;
	}
}
