package synergyviewcommons.collections;

public abstract class ListDiffEntry<R> {
	
	public abstract R getElement();
	public abstract boolean isAddition();
	public abstract int getPosition();
}
