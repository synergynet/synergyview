package synergyviewcore.media.ui;

import org.eclipse.core.expressions.PropertyTester;

import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.model.Media;
import synergyviewcore.media.ui.model.MediaNode;
import synergyviewcore.project.OpenedProjectController;

public class MediaResourceFileAvailablePropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof MediaNode) {
			boolean expectedBooleanValue = ((Boolean) expectedValue).booleanValue();
			MediaNode mediaNode = (MediaNode) receiver;
			if (property.compareTo("isMediaResourceFileAvailable") == 0) {
				Media media;
				try {
					media = OpenedProjectController.getInstance().getMediaController().find(mediaNode.getResource());
					boolean isMediaResourceFileAvailable = (media.getMediaFileResource() != null);
					return (expectedBooleanValue == isMediaResourceFileAvailable) ? true : false;
				} catch (ObjectNotfoundException e) {
					return false;
				}
			} 
		}
		return false;
	}

	protected boolean toBoolean(Object expectedValue) {
		if (expectedValue instanceof Boolean) {
			return ((Boolean) expectedValue).booleanValue();
		}
		return true;
	}

}
