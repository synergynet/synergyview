package synergyviewcommons.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * The Class PropertySupportObject.
 */
public abstract class PropertySupportObject {
	
	/** The property change support. */
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/**
	 * Adds the property change listener.
	 *
	 * @param listener the listener
	 */
	public final void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Adds the property change listener.
	 *
	 * @param propertyName the property name
	 * @param listener the listener
	 */
	public final void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param listener the listener
	 */
	public final void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param propertyName the property name
	 * @param listener the listener
	 */
	public final void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}

	/**
	 * Fire property change.
	 *
	 * @param propertyName the property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}
}
