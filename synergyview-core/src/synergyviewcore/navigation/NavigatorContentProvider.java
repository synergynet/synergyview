package synergyviewcore.navigation;

import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;

import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;


public class NavigatorContentProvider extends ObservableListTreeContentProvider {

	public NavigatorContentProvider() {
		super(getObservableListFactory(), getTreeStructureAdvisor());
	}

	// This factory returns an observable list of children for the given parent.
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
}
