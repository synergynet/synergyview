package synergyviewcore.media.model;

import org.eclipse.core.resources.IFile;

import synergyviewcore.navigation.model.AbstractNode;
import synergyviewcore.navigation.model.IParentNode;

/**
 * The Class MiscNode.
 */
public class MiscNode extends AbstractNode<IFile> {

    /**
     * Instantiates a new misc node.
     * 
     * @param fileValue
     *            the file value
     * @param parentValue
     *            the parent value
     */
    public MiscNode(IFile fileValue, IParentNode parentValue) {
	super(fileValue, parentValue);
	setLabel(fileValue.getName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.navigation.model.INode#dispose()
     */
    public void dispose() {
	// TODO Auto-generated method stub

    }

}
