package synergyviewmvc.media.model;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import uk.ac.durham.tel.commons.jface.node.AbstractBaseNode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;

public class MiscNode extends AbstractBaseNode<IFile> {

	public MiscNode(IFile fileValue, IParentNode parentValue) {
		super(fileValue, parentValue);
		setLabel(fileValue.getName());
	}
	
	

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() {
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
