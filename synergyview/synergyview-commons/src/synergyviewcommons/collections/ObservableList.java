package synergyviewcommons.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Own implmentation of Observable list
 * 
 * @author phyo
 *
 * @param <R> R is the type of List<E>
 * @param <E> E is the type of object 
 */
public class ObservableList<R extends List<E>, E> implements IObservableList<R,E>  {
	//Thread-safe list with a cost!
	private List<CollectionChangeListener> listeners = new CopyOnWriteArrayList<CollectionChangeListener>();
	private R list = null;
	private List<E> readOnlyList = null;
	
	@Override
	public synchronized void addChangeListener(CollectionChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public synchronized void removeChangeListener(CollectionChangeListener listener) {
		listeners.remove(listener);
	}
	
	public ObservableList(R list) {
		this.list = list;
	}

	@Override
	public void add(int index, E element) {
		list.add(index, element);
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(element, true);
		eventNotify(entries);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> collection) {
		if (!list.addAll(index, collection)) 
			return false;
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, true);
		eventNotify(entries);
		return true;
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return list.containsAll(collection);
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object object) {
		return list.indexOf(object);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator(index);
	}

	@Override
	public E remove(int index) {
		E element = list.remove(index);
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(element, false);
		eventNotify(entries);
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, false);
		if (!list.removeAll(collection)) 
			return false;
		eventNotify(entries);
		return true;
		
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		if (!list.retainAll(collection))
			return false;
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, false);
		eventNotify(entries);
		return true;
	}

	@Override
	public E set(int index, E element) {
		return list.set(index, element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public boolean add(E element) {
		if (!list.add(element))
			return false;
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(element, true);
		eventNotify(entries);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> collection) {
		if (!list.addAll(collection)) 
			return false;
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, true);
		eventNotify(entries);
		return true;
	}

	@Override
	public void clear() {
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(list, false);
		list.clear();
		eventNotify(entries);
	}

	@Override
	public boolean remove(Object object) {
		if (!list.contains(object))
			return false;
		CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(object, false);
		if (!list.remove(object)) 
			return false;
		eventNotify(entries);
		return true;
	}
	
	@Override
	public List<E> getReadOnlyList() {
		if (readOnlyList==null)
			readOnlyList = Collections.unmodifiableList(list);
		return readOnlyList;
	}
	
	private void eventNotify(CollectionDiffEntry<E>[] changedItems) {
		CollectionDiff<E> diff = new CollectionDiffImpl<E>(changedItems);
		CollectionChangeEvent event = new CollectionChangeEvent(getReadOnlyList(), diff);
		for (CollectionChangeListener listener : listeners) {
			listener.listChanged(event);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private CollectionDiffEntry<E>[] createCollectionDiffEntryArray(Collection<?> collection, boolean isAddition) {
		CollectionDiffEntry<E>[] entries = (CollectionDiffEntry<E>[]) Array.newInstance(CollectionDiffEntry.class, collection.size());
		Iterator<?> iterator = collection.iterator();
		for(int i = 0 ; i < collection.size(); i++) {
			E item = (E) iterator.next();
			entries[i] = new CollectionDiffEntryImpl<E>(item, isAddition, list.indexOf(item));
		}
		return entries;
	}
	
	@SuppressWarnings("unchecked")
	private CollectionDiffEntry<E>[] createCollectionDiffEntryArray(Object element, boolean isAddition) {
		CollectionDiffEntry<E>[] entries = (CollectionDiffEntry<E>[]) Array.newInstance(CollectionDiffEntry.class, 1);
		entries[0] = new CollectionDiffEntryImpl<E>((E) element, isAddition, list.indexOf(element));
		return entries;
	}
}
