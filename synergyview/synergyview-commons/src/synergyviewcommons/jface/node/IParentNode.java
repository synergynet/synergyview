package synergyviewcommons.jface.node;

import java.util.List;

import org.eclipse.core.databinding.observable.IObservable;

public interface IParentNode extends INode {
	List<INode> getChildren();
	IObservable getObservableChildren();
	void deleteChildren(INode[] nodeValue) throws NodeRemoveException;
	List<String> getChildrenNames();
}
