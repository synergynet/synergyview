package synergyviewcore.navigation;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import uk.ac.durham.tel.commons.jface.node.INode;

public class NavigatorLabelProvider extends LabelProvider {
	private LocalResourceManager resourceManager = new LocalResourceManager(
			JFaceResources.getResources());
	
	@Override
	public void dispose() {
		resourceManager.dispose();
		super.dispose();
	}
	
	@Override
	public Image getImage(Object element) {
		if (element instanceof INode) {
			return (Image) resourceManager.get(((INode) element).getIcon());
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof INode) {
			return ((INode) element).getLabel();
		}
		return "Unknown";
	}

}
