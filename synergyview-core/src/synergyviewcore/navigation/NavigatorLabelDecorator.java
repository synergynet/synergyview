package synergyviewcore.navigation;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import synergyviewcore.project.ProjectResourceHelper;
import synergyviewcore.project.ui.model.StudyNode;

public class NavigatorLabelDecorator implements ILabelDecorator {

	@Override
	public Image decorateImage(Image image, Object element) {
		if (element instanceof StudyNode) {
			return ProjectResourceHelper.getProjectNodeDecorator((StudyNode) element, image);
		}
		return null;
	}

	@Override
	public void dispose() {
		//
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		//
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		//
	}

	@Override
	public String decorateText(String text, Object element) {
		return null;
	}




}
