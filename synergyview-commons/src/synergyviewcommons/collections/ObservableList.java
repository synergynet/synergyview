package synergyviewcommons.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Own implmentation of Observable list.
 * 
 * @author phyo
 * @param <R>
 *            R is the type of List<E>
 * @param <E>
 *            E is the type of object
 */
public class ObservableList<R extends List<E>, E> implements IObservableList<R, E> {
    /** The list. */
    private R list = null;

    // Thread-safe list with a cost!
    /** The listeners. */
    private List<CollectionChangeListener> listeners = new CopyOnWriteArrayList<CollectionChangeListener>();

    /** The read only list. */
    private List<E> readOnlyList = null;

    /**
     * Instantiates a new observable list.
     * 
     * @param list
     *            the list
     */
    public ObservableList(R list) {
	this.list = list;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#add(java.lang.Object)
     */
    @Override
    public boolean add(E element) {
	if (!list.add(element)) {
	    return false;
	}
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(element, true);
	eventNotify(entries);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#add(int, java.lang.Object)
     */
    @Override
    public void add(int index, E element) {
	list.add(index, element);
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(element, true);
	eventNotify(entries);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#addAll(java.util.Collection)
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
	if (!list.addAll(collection)) {
	    return false;
	}
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, true);
	eventNotify(entries);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#addAll(int, java.util.Collection)
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
	if (!list.addAll(index, collection)) {
	    return false;
	}
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, true);
	eventNotify(entries);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.collections.ICollectionObservable#addChangeListener (synergyviewcommons.collections.CollectionChangeListener)
     */
    @Override
    public synchronized void addChangeListener(CollectionChangeListener listener) {
	listeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#clear()
     */
    @Override
    public void clear() {
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(list, false);
	list.clear();
	eventNotify(entries);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o) {
	return list.contains(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> collection) {
	return list.containsAll(collection);
    }

    /**
     * Creates the collection diff entry array.
     * 
     * @param collection
     *            the collection
     * @param isAddition
     *            the is addition
     * @return the collection diff entry[]
     */
    @SuppressWarnings("unchecked")
    private CollectionDiffEntry<E>[] createCollectionDiffEntryArray(Collection<?> collection, boolean isAddition) {
	CollectionDiffEntry<E>[] entries = (CollectionDiffEntry<E>[]) Array.newInstance(CollectionDiffEntry.class, collection.size());
	Iterator<?> iterator = collection.iterator();
	for (int i = 0; i < collection.size(); i++) {
	    E item = (E) iterator.next();
	    entries[i] = new CollectionDiffEntryImpl<E>(item, isAddition, list.indexOf(item));
	}
	return entries;
    }

    /**
     * Creates the collection diff entry array.
     * 
     * @param element
     *            the element
     * @param isAddition
     *            the is addition
     * @return the collection diff entry[]
     */
    @SuppressWarnings("unchecked")
    private CollectionDiffEntry<E>[] createCollectionDiffEntryArray(Object element, boolean isAddition) {
	CollectionDiffEntry<E>[] entries = (CollectionDiffEntry<E>[]) Array.newInstance(CollectionDiffEntry.class, 1);
	entries[0] = new CollectionDiffEntryImpl<E>((E) element, isAddition, list.indexOf(element));
	return entries;
    }

    /**
     * Event notify.
     * 
     * @param changedItems
     *            the changed items
     */
    private void eventNotify(CollectionDiffEntry<E>[] changedItems) {
	CollectionDiff<E> diff = new CollectionDiffImpl<E>(changedItems);
	CollectionChangeEvent event = new CollectionChangeEvent(getReadOnlyList(), diff);
	for (CollectionChangeListener listener : listeners) {
	    listener.listChanged(event);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#get(int)
     */
    @Override
    public E get(int index) {
	return list.get(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.collections.IObservableList#getReadOnlyList()
     */
    @Override
    public List<E> getReadOnlyList() {
	if (readOnlyList == null) {
	    readOnlyList = Collections.unmodifiableList(list);
	}
	return readOnlyList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#indexOf(java.lang.Object)
     */
    @Override
    public int indexOf(Object object) {
	return list.indexOf(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#isEmpty()
     */
    @Override
    public boolean isEmpty() {
	return list.isEmpty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#iterator()
     */
    @Override
    public Iterator<E> iterator() {
	return list.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#lastIndexOf(java.lang.Object)
     */
    @Override
    public int lastIndexOf(Object o) {
	return list.lastIndexOf(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator()
     */
    @Override
    public ListIterator<E> listIterator() {
	return list.listIterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#listIterator(int)
     */
    @Override
    public ListIterator<E> listIterator(int index) {
	return list.listIterator(index);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(int)
     */
    @Override
    public E remove(int index) {
	E element = list.remove(index);
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(element, false);
	eventNotify(entries);
	return element;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#remove(java.lang.Object)
     */
    @Override
    public boolean remove(Object object) {
	if (!list.contains(object)) {
	    return false;
	}
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(object, false);
	if (!list.remove(object)) {
	    return false;
	}
	eventNotify(entries);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, false);
	if (!list.removeAll(collection)) {
	    return false;
	}
	eventNotify(entries);
	return true;

    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcommons.collections.ICollectionObservable#removeChangeListener (synergyviewcommons.collections.CollectionChangeListener)
     */
    @Override
    public synchronized void removeChangeListener(CollectionChangeListener listener) {
	listeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#retainAll(java.util.Collection)
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
	if (!list.retainAll(collection)) {
	    return false;
	}
	CollectionDiffEntry<E>[] entries = createCollectionDiffEntryArray(collection, false);
	eventNotify(entries);
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#set(int, java.lang.Object)
     */
    @Override
    public E set(int index, E element) {
	return list.set(index, element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#size()
     */
    @Override
    public int size() {
	return list.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#subList(int, int)
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
	return list.subList(fromIndex, toIndex);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#toArray()
     */
    @Override
    public Object[] toArray() {
	return list.toArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.List#toArray(T[])
     */
    @Override
    public <T> T[] toArray(T[] a) {
	return list.toArray(a);
    }
}
