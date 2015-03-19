package synergyviewcommons.collections;

public class CollectionDiffEntryImpl<R> extends CollectionDiffEntry<R> {
	private R entry;
	private boolean isAddition;
	private int position;
	
	public CollectionDiffEntryImpl(R entry, boolean isAddition, int position) {
		super();
		this.entry = entry;
		this.isAddition = isAddition;
		this.position = position;
	}

	@Override
	public R getElement() {
		return entry;
	}

	@Override
	public boolean isAddition() {
		return isAddition;
	}

	@Override
	public int getPosition() {
		return position;
	}
}
