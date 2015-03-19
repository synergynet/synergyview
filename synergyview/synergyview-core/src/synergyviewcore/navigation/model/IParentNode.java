package synergyviewcore.navigation.model;

import java.util.List;

import org.eclipse.core.databinding.observable.IObservable;

public interface IParentNode extends INode {
	List<INode> getChildren();
	IObservable getObservableChildren();
	void deleteChildren(INode[] nodeValue);
	List<String> getChildrenNames();
}
