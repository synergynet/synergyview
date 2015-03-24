package synergyviewcore.media.model;

import org.eclipse.core.resources.IFile;

import synergyviewcore.navigation.model.AbstractNode;
import synergyviewcore.navigation.model.IParentNode;


/**
 * The Class ImageNode.
 */
public class ImageNode extends AbstractNode<IFile> {

	/**
	 * Checks if is valid extension.
	 *
	 * @param extValue the ext value
	 * @return true, if is valid extension
	 */
	public static boolean isValidExtension(String extValue) {
		return (extValue.equalsIgnoreCase("png") || extValue.equalsIgnoreCase("jpg"));
	}
	
	/**
	 * Instantiates a new image node.
	 *
	 * @param fileValue the file value
	 * @param parentValue the parent value
	 */
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
