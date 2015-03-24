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


/**
 * The Class AbstractNode.
 *
 * @param <R> the generic type
 */
public abstract class AbstractNode<R> extends ModelObject implements INode {

	/** The label. */
	private String label;
	
	/** The resource. */
	protected R resource;
	
	/** The logger. */
	protected final ILog logger;
	
	/** The parent. */
	private IParentNode parent;
	
	/**
	 * Instantiates a new abstract node.
	 *
	 * @param resourceValue the resource value
	 * @param parentValue the parent value
	 */
	public AbstractNode(R resourceValue, IParentNode parentValue) {
		this.resource = resourceValue;
		logger = Activator.getDefault().getLog();
		setParent(parentValue);
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getParent()
	 */
	public IParentNode getParent() {
		return parent;
	}

	/**
	 * Instantiates a new abstract node.
	 */
	protected AbstractNode() {
		logger = Activator.getDefault().getLog();
	}
	
	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getEMFactoryProvider()
	 */
	public IEMFactoryProvider getEMFactoryProvider() {
		INode node = this;
		while (!(node instanceof IEMFactoryProvider)) {
			node = node.getParent();
		}
		return (IEMFactoryProvider) node;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getRoot()
	 */
	public IParentNode getRoot() {
		INode node = this;
		while (!(node.getParent() == null)) {
			node = node.getParent();
		}
		return (IParentNode) node;
	}

	
	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getViewerProvider()
	 */
	public IViewerProvider getViewerProvider() {
		INode node = this;
		while (!(node instanceof IViewerProvider)) {
			node = node.getParent();
		}
		return (IViewerProvider) node;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getLastParent()
	 */
	public IParentNode getLastParent() {
		INode node = this;
		if (node.getParent().getParent() != null) { //If not last parent
			while (!(node.getParent().getParent() == null)) {
				node = node.getParent();
			}
			return (IParentNode) node;
		} else return null;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#setParent(synergyviewcore.navigation.model.IParentNode)
	 */
	public void setParent(IParentNode parentValue) {
		this.firePropertyChange("parent", parent, this.parent = parentValue);
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getIcon()
	 */
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

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getResource()
	 */
	public R getResource() {
		return resource;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#getLabel()
	 */
	public String getLabel() {
		return label;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#setLabel(java.lang.String)
	 */
	public void setLabel(String labelValue) {
		firePropertyChange("label", this.label, this.label = labelValue);
	}

}
