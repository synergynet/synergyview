package synergyviewcore.media.model;

import org.eclipse.core.resources.IFile;

import synergyviewcore.navigation.model.AbstractNode;
import synergyviewcore.navigation.model.IParentNode;

public class ImageNode extends AbstractNode<IFile> {

	public static boolean isValidExtension(String extValue) {
		return (extValue.equalsIgnoreCase("png") || extValue.equalsIgnoreCase("jpg"));
	}
	
	public ImageNode(IFile fileValue, IParentNode parentValue) {
		super(fileValue, parentValue);
		setLabel(fileValue.getName());
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() {
		//
		
	}



}
