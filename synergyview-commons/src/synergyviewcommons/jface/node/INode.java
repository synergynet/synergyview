package synergyviewcommons.jface.node;

import org.eclipse.jface.resource.ImageDescriptor;

public interface INode {
	String getLabel();
	void setLabel(String labelValue);
	IParentNode getParent();
	IParentNode getRoot();
	void setParent(IParentNode parentValue);
	ImageDescriptor getIcon();
	Object getResource();
	void dispose() throws DisposeException;
}
