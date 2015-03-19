package synergyviewcommons.jface.node;

import org.eclipse.jface.resource.ImageDescriptor;

public interface INode {
	String getLabel();
	String getLabelDecorator();
	String getIconDecorator();
	IParentNode getParent();
	IParentNode getRoot();
	ImageDescriptor getIcon();
	Object getResource();
	void dispose() throws DisposeException;
}
