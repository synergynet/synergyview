package synergyviewcore.media.model.listeners;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;

import org.eclipse.core.resources.IFile;

import synergyviewcore.media.model.Media;
import synergyviewcore.project.OpenedProjectController;

public class MediaChangeListener {
	@PostPersist
	public void inserted(Media media) {
		if (!OpenedProjectController.getInstance().isMediaControllerAvailable())
			return;
		OpenedProjectController.getInstance().getMediaController().notifyListChangeListeners(media, true);
	}
	
	@PostRemove
	public void removed(Media media) {
		if (!OpenedProjectController.getInstance().isMediaControllerAvailable())
			return;
		OpenedProjectController.getInstance().getMediaController().notifyListChangeListeners(media, false);
	}
	
	@PostLoad
	public void loaded(Media media) {
		if (!OpenedProjectController.getInstance().isMediaControllerAvailable())
			return;
		IFile file = OpenedProjectController.getInstance().getMediaController().getMediaFolder().getFile(media.getId());
		if (file.isAccessible())
			media.setMediaFileResource(file);
		else media.setMediaFileResource(null);
	}	
}
