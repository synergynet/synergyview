package synergyviewcore.navigation.model;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcore.navigation.projects.model.IEMFactoryProvider;

public interface INode {
	String getLabel();
	void setLabel(String labelValue);
	IParentNode getParent();
	IParentNode getRoot();
	IParentNode getLastParent();
	IViewerProvider getViewerProvider();
	IEMFactoryProvider getEMFactoryProvider();
	void setParent(IParentNode parentValue);
	ImageDescriptor getIcon();
	Object getResource();
	void dispose() throws DisposeException;
}
