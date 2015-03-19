package synergyviewcore.media.model;

import org.eclipse.core.resources.IFile;

import synergyviewcore.navigation.model.AbstractNode;
import synergyviewcore.navigation.model.IParentNode;

public class MiscNode extends AbstractNode<IFile> {

	public MiscNode(IFile fileValue, IParentNode parentValue) {
		super(fileValue, parentValue);
		setLabel(fileValue.getName());
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


}
