package synergyviewcommons.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class ObservableList<R extends List<E>, E> implements IObservableList<R,E>  {
	private List<ListChangeListener> listeners = new CopyOnWriteArrayList<ListChangeListener>();
	private R list = null;
	private List<E> readOnlyList = null;
	@Override
	public void addChangeListener(ListChangeListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeChangeListener(ListChangeListener listener) {
		listeners.remove(listener);
	}
	
	public ObservableList(R list) {
		this.list = list;
	}

	@Override
	public void add(int index, E element) {
		list.add(index, element);
		@SuppressWarnings("unchecked")
		ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, 1);
		entries[0] = new ListDiffEntryImpl<E>(element, true, index);
		eventNotify(entries);
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		@SuppressWarnings("unchecked")
		ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, c.size());
		if (list.addAll(index, c)) {
			Iterator<? extends E> iterator = c.iterator();
			for(int i = 0 ; i < c.size(); i++) {
				E item = iterator.next();
				entries[i] = new ListDiffEntryImpl<E>(item, true, list.indexOf(item));
			}
			eventNotify(entries);
			return true;
		} else return false;
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
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
		@SuppressWarnings("unchecked")
		ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, 1);
		entries[0] = new ListDiffEntryImpl<E>(element, false, index);
		eventNotify(entries);
		return element;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(Collection<?> c) {
		ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, c.size());
		Iterator<?> iterator = c.iterator();
		for(int i = 0 ; i < c.size(); i++) {
			Object item = iterator.next();
			if (list.contains(item))
				entries[i] = new ListDiffEntryImpl<E>((E) item, false, list.indexOf(item));
		}
		if (list.removeAll(c)) {
			eventNotify(entries);
			return true;
		} else return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(Collection<?> c) {
		ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, c.size());
		if (list.retainAll(c)) {
			Iterator<?> iterator = c.iterator();
			for(int i = 0 ; i < c.size(); i++) {
				E item = (E) iterator.next();
				entries[i] = new ListDiffEntryImpl<E>(item, true, list.indexOf(item));
			}
			eventNotify(entries);
			return true;
		} else return false;
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
	public boolean add(E e) {
		if (list.add(e)) {
			@SuppressWarnings("unchecked")
			ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, 1);
			entries[0] = new ListDiffEntryImpl<E>(e, true, list.indexOf(e));
			eventNotify(entries);
			return true;
		} else return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		@SuppressWarnings("unchecked")
		ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, c.size());
		if (list.addAll(c)) {
			Iterator<? extends E> iterator = c.iterator();
			for(int i = 0 ; i < c.size(); i++) {
				E item = iterator.next();
				entries[i] = new ListDiffEntryImpl<E>(item, true, list.indexOf(item));
			}
			eventNotify(entries);
			return true;
		} else return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) list.toArray();
		list.clear();
		eventNotify(entries);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		if (list.contains(o)) {
			int index = list.indexOf(o);
			if (list.remove(o)) {
				ListDiffEntry<E>[] entries = (ListDiffEntry<E>[]) Array.newInstance(ListDiffEntry.class, 1);
				entries[0] = new ListDiffEntryImpl<E>((E) o, false, index);
				eventNotify(entries);
				return true;
			} else return false;
		} else return false;
	}
	

	private void eventNotify(ListDiffEntry<E>[] changedItems) {
		ListDiff<E> diff = new ListDiffImpl<E>(changedItems);
		ListChangeEvent event = new ListChangeEvent(getReadOnlyList(), diff);
		
		for (ListChangeListener listener : listeners) {
			listener.listChanged(event);
		}
	}	

	@Override
	public List<E> getReadOnlyList() {
		if (readOnlyList==null)
			readOnlyList = Collections.unmodifiableList(list);
		return readOnlyList;
	}
}
