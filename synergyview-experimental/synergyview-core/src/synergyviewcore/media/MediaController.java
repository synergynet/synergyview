package synergyviewcore.media;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.controller.AbstractController;
import synergyviewcore.controller.ModelPersistenceException;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.model.Media;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.project.OpenedProjectController;
import synergyviewcore.runtime.RunnableUtil;
import synergyviewcore.runtime.WorkspaceModifyOperation;
import synergyviewcore.workspace.resource.DbUtil;

public class MediaController extends AbstractController<Media> {
	private static final String MEDIA_DATA_FILE_NAME = "media.dat";
	private static Logger logger = Logger.getLogger(MediaController.class);
	private IFile mediaDataFile;
	private IFolder mediaFolder;
	
	private IResourceChangeListener fileResourceChangeListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) throws CoreException {
						IResource changedResource = delta.getResource();
						if (mediaFolder == null || !mediaFolder.isAccessible())
							return true;
						if (!(changedResource instanceof IFile))
							return true;
						if (!(changedResource.getParent().getFullPath().equals(mediaFolder.getFullPath())))
							return true;
						updateMediaProperty((IFile) changedResource);
						return true;
					}
				});
				
			} catch (CoreException ex) {
				logger.error("Error while processing resource Changed for media.", ex);
			}
		}
	};
	
	public MediaController(IFolder mediaFolder, IFolder mediaDataFolder) {
		super(OpenedProjectController.getInstance().getEntityManagerFactory(), Media.class);
		this.mediaFolder = mediaFolder;
		this.mediaDataFile = mediaDataFolder.getFile(MEDIA_DATA_FILE_NAME);
		addMediaFileResourceChangeListener();
	}
	
	
	public void dispose() {
		removeMediaFileResourceChangeListener();
		clearMediaData();
	}
	
	public IFolder getMediaFolder() {
		return mediaFolder;
	}
	

	public void deleteMedia(final List<Media> mediaDataItemListToDelete) throws MediaException {
		WorkspaceModifyOperation deleteMediaItemsOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				monitor.beginTask("Deleting Media data items", mediaDataItemListToDelete.size());
				try {
					MediaController.this.delete(mediaDataItemListToDelete);
					for (Media projectResoruceToBeRemoved : mediaDataItemListToDelete) {
						if (projectResoruceToBeRemoved.getMediaFileResource()!=null) 
							projectResoruceToBeRemoved.getMediaFileResource().delete(true, false, monitor);
						monitor.worked(1);
					}
				} catch (Exception e) {
					this.error = e;
				} finally {
					monitor.done();
				}
				
			}
		};
		RunnableUtil.runWithProgress(deleteMediaItemsOperation);
		if (deleteMediaItemsOperation.getError()!=null)
			throw new MediaException("Unable to delete the Media data item " + deleteMediaItemsOperation.getError().getMessage());
	}
	
	public void deleteMediaResource(final List<IFile> mediaFilesToBeDeleted) throws MediaException {
		WorkspaceModifyOperation deleteMediaFileOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				monitor.beginTask("Deleting Media files", mediaFilesToBeDeleted.size());
				try {
					for (IFile projectResoruceToBeRemoved : mediaFilesToBeDeleted) {
						if(monitor.isCanceled())
							throw new OperationCanceledException("Deleting Media files is cancelled.");
						projectResoruceToBeRemoved.delete(true, false, monitor);
						monitor.worked(1);
					}
				} catch (CoreException e) {
					error = e;
				}
			}
		};
		RunnableUtil.runWithProgress(deleteMediaFileOperation);
		if (deleteMediaFileOperation.getError()!=null)
			throw new MediaException("Unable to delete the Media files. " + deleteMediaFileOperation.getError().getMessage());
	}
	
	
	public void importDBMediaData() throws Exception {
		if (!mediaDataFile.exists())
			return;		
		DbUtil.importDataToDB("MEDIA", mediaDataFile);	
	}
	
	public void exportDBMediaData() {
		try {
			DbUtil.exportDataFromDB("MEDIA", mediaDataFile);
		} catch (Exception e) {
			MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
		}
	}

	public void importMediaResourceFiles(String[] fileNames) throws MediaException {
		try {
			createMedia(fileNames);
			MediaDataFactory.importExternalMediaFiles(fileNames, mediaFolder);
		} catch (Exception e) {
			logger.error("Unable to import media files.", e);
			throw new MediaException("Unable to import media files.", e);
		}
	}

	private void createMedia(String[] filePaths) throws MediaException {
		for (String filePath : filePaths) {
			File file = new File(filePath);
			try {
				Media existingMedia = this.find(file.getName());
				if (existingMedia.getFileSize() != file.length()) {
					throw new MediaException("File entry with the same name but different file size already exist.");
				}
			} catch(ObjectNotfoundException ex) {
				Media media = MediaDataFactory.getMedia(file, PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				try {
					this.create(media);
				} catch (ModelPersistenceException e) {
					throw new MediaException(e);
				}
			}
		}		
	}

	private void addMediaFileResourceChangeListener() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(fileResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}

	private void removeMediaFileResourceChangeListener() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(fileResourceChangeListener);
	}

	
	public List<Media> getMediaList() throws Exception {
		List<Media> mediaList =  this.getAll("getAllMedia", Media.class);
		return mediaList;
	}
	
	public List<PlayableMedia> getPlayableMediaList() {
		List<PlayableMedia> mediaList =  this.getAll("getAllPlayableMedia", PlayableMedia.class);
		return mediaList;
	}
	
	@Override
	protected <T extends Media> List<T> getAll(String queryString,
			Class<T> className) {
		return super.getAll(queryString, className);
	}

	private void clearMediaData() {
		mediaDataFile = null;
		mediaFolder = null;
	}
	
	private void updateMediaProperty(IFile addedResource) {
		
		try {
			Media mediaDataItem = this.find(addedResource.getName());
			PropertyChangeEvent propertyChangeEvent = new PropertyChangeEvent(mediaDataItem.getId(), Media.PROP_MEDIA_FILE_RESOURCE, null, mediaDataItem.getMediaFileResource());
			this.notifyPropertyListeners(mediaDataItem.getId(), propertyChangeEvent);
		} catch (ObjectNotfoundException e) {
			//
		} 
	}

	public List<String> getPlayableMediaIdList() throws Exception {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<String> query = entityManager.createQuery("SELECT M.id FROM PlayableMedia M", String.class);
			return query.getResultList();
		} catch (Exception e) {
			logger.error("Error while geting Ids.", e);
			throw new Exception("Error while getting Ids.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}
	
	@Override
	public Media find(String mediaId) throws ObjectNotfoundException {
		Media media = super.find(mediaId);
		//setMediaFileResource(media);
		return media;
	}

	public List<String> getMediaIdList() throws Exception {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<String> query = entityManager.createQuery("SELECT M.id FROM Media M", String.class);
			return query.getResultList();
		} catch (Exception e) {
			logger.error("Error while geting Ids.", e);
			throw new Exception("Error while getting Ids.", e);
		} finally {
			if (entityManager!=null && entityManager.isOpen())
				entityManager.close();
		}
	}

	public void notifyListChangeListeners(Media media, boolean isAddition) {
		List<Media> changedMediaList = new ArrayList<Media>();
		changedMediaList.add(media);
		this.notifyCollectionChangeListeners(changedMediaList, isAddition);
	}

}
