package synergyviewcore.navigation;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;

import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;

/**
 * The Class NavigatorContentProvider.
 */
public class NavigatorContentProvider extends ObservableListTreeContentProvider {
	
	// This factory returns an observable list of children for the given parent.
	/**
	 * Gets the observable list factory.
	 * 
	 * @return the observable list factory
	 */
	private static IObservableFactory getObservableListFactory() {
		return new IObservableFactory() {
			public IObservable createObservable(Object parent) {
				if (parent instanceof IParentNode) {
					return ((IParentNode) parent).getObservableChildren();
				}
				return null;
			}
		};
	}
	
	// The following is optional, you can pass null as the advisor, but then
	// setSelection() will not find elements that have not been expanded.
	/**
	 * Gets the tree structure advisor.
	 * 
	 * @return the tree structure advisor
	 */
	private static TreeStructureAdvisor getTreeStructureAdvisor() {
		return new TreeStructureAdvisor() {
			public Object getParent(Object element) {
				if (element instanceof INode) {
					return ((INode) element).getParent();
				}
				return super.getParent(element);
			}
		};
	}
	
	/**
	 * Instantiates a new navigator content provider.
	 */
	public NavigatorContentProvider() {
		super(getObservableListFactory(), getTreeStructureAdvisor());
	}
}
