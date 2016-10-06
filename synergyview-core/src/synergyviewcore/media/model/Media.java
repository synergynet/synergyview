package synergyviewcore.media.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Transient;

import org.eclipse.core.resources.IFile;

import synergyviewcore.media.model.listeners.MediaChangeListener;
import uk.ac.durham.tel.commons.persistence.IdBasedObject;


@Entity
@NamedQuery(name="getAllMedia", query="select M from Media M")
@EntityListeners({MediaChangeListener.class})
public class Media extends IdBasedObject {
	
	public static final String PROP_MEDIA_FILE_RESOURCE = "mediaFileResource";
	private IFile mediaFileResource;
	private long fileSize;

	public void setMediaFileResource(IFile mediaFileResource) {
		this.firePropertyChange(PROP_MEDIA_FILE_RESOURCE, this.mediaFileResource, this.mediaFileResource = mediaFileResource);
	}
	
	@Transient
	public IFile getMediaFileResource() {
		return this.mediaFileResource;
	}
	
	@Id
	@Override
	public String getId() {
		return super.getId();
	}
	
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public long getFileSize() {
		return fileSize;
	}
}
