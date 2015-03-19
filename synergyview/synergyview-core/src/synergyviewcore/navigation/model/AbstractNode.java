package synergyviewcore.navigation.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.ILog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import synergyviewcore.Activator;
import synergyviewcore.model.ModelObject;
import synergyviewcore.navigation.projects.model.IEMFactoryProvider;
import synergyviewcore.resource.ResourceLoader;

public abstract class AbstractNode<R> extends ModelObject implements INode {

	private String label;
	protected R resource;
	protected final ILog logger;
	private IParentNode parent;
	public AbstractNode(R resourceValue, IParentNode parentValue) {
		this.resource = resourceValue;
		logger = Activator.getDefault().getLog();
		setParent(parentValue);
	}

	public IParentNode getParent() {
		return parent;
	}

	protected AbstractNode() {
		logger = Activator.getDefault().getLog();
	}
	
	public IEMFactoryProvider getEMFactoryProvider() {
		INode node = this;
		while (!(node instanceof IEMFactoryProvider)) {
			node = node.getParent();
		}
		return (IEMFactoryProvider) node;
	}

	public IParentNode getRoot() {
		INode node = this;
		while (!(node.getParent() == null)) {
			node = node.getParent();
		}
		return (IParentNode) node;
	}

	
	public IViewerProvider getViewerProvider() {
		INode node = this;
		while (!(node instanceof IViewerProvider)) {
			node = node.getParent();
		}
		return (IViewerProvider) node;
	}

	public IParentNode getLastParent() {
		INode node = this;
		if (node.getParent().getParent() != null) { //If not last parent
			while (!(node.getParent().getParent() == null)) {
				node = node.getParent();
			}
			return (IParentNode) node;
		} else return null;
	}

	public void setParent(IParentNode parentValue) {
		this.firePropertyChange("parent", parent, this.parent = parentValue);
	}

	public ImageDescriptor getIcon() {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			return ResourceLoader.getIconFromProgram(Program.findProgram(file.getFileExtension()));
		}
		if (resource instanceof IProject) {
			ImageDescriptor i = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
			return i;
		}
		if (resource instanceof IFolder) {
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		}
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
	}

	public R getResource() {
		return resource;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String labelValue) {
		firePropertyChange("label", this.label, this.label = labelValue);
	}

}
