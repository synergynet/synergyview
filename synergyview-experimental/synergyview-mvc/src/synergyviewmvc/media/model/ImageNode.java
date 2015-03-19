package synergyviewmvc.media.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import uk.ac.durham.tel.commons.jface.node.AbstractBaseNode;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

public class ImageNode extends AbstractBaseNode<IFile> {

	public static boolean isValidExtension(String extValue) {
		return (extValue.equalsIgnoreCase("png") || extValue.equalsIgnoreCase("jpg"));
	}
	
	public ImageNode(IFile fileValue, IParentNode parentValue) {
		super(fileValue, parentValue);
		setLabel(fileValue.getName());
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.AbstractBaseNode#getIcon()
	 */
	@Override
	public ImageDescriptor getIcon() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT);
	}
}
