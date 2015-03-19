package synergyviewcore.project;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.osgi.PersistenceProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.collection.MediaCollectionController;
import synergyviewcore.media.MediaController;
import synergyviewcore.plugin.Activator;
import synergyviewcore.runtime.RunnableUtil;
import synergyviewcore.runtime.WorkspaceModifyOperation;
import synergyviewcore.workspace.WorkspaceController;
import synergyviewcore.workspace.resource.WorkspaceResourceUtil;
import uk.ac.durham.tel.commons.model.PropertySupportObject;

public class OpenedProjectController extends PropertySupportObject {
	public static final String PROJECT_DB = "DB";
	private static Logger logger = Logger.getLogger(OpenedProjectController.class);
	private static OpenedProjectController instance;
	private IProject openedStudy;
	private IProject studyToBeOpened;
	public static final String PROP_IS_MEDIA_CONTROLLER_AVAILABLE = "isMediaControllerAvailable";
	private boolean isMediaControllerAvailable;
	public static final String PROP_IS_SUBJECT_FOLDER_AVAILABLE = "isSubjectFolderAvailable";
	private boolean isSubjectFolderAvailable;
	public static final String PROP_IS_MEDIA_COLLECTION_CONTROLLER_AVAILABLE = "isMediaCollectionControllerAvailable";
	private boolean isMediaCollectionControllerAvailable;
	private MediaController mediaController;
	private MediaCollectionController collectionController;
	private IResourceChangeListener projectResourceChangeListener = new IResourceChangeListener() {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					public boolean visit(final IResourceDelta delta) throws CoreException {
						if (!(delta.getResource() instanceof IProject))
							return true;
						final IProject study = (IProject) delta.getResource();
						if (openedStudy!=null && study.getFullPath().equals(openedStudy.getFullPath())) {
							if (delta.getKind() == IResourceDelta.REMOVED || !study.isAccessible()) {								
								Display.getDefault().asyncExec(new Runnable() {
									@Override
									public void run() {
										checkProjectState();
									}
								});
							}
							return true;
						} 
						if (openedStudy!=null && study.isAccessible() && !study.getFullPath().equals(openedStudy.getFullPath())) {
							studyToBeOpened = study;
							return true;
						}
						if (openedStudy==null && study.isAccessible()) {
							Display.getDefault().asyncExec(new Runnable() {
								@Override
								public void run() {
									updateOpenStudyState(study);
								}
							});
							return true;
						}
						return true;
					}
				});
			} catch (CoreException ex) {
				//
			}
		}
	};
	private EntityManagerFactory eManagerFactory;
	

	
	public static synchronized OpenedProjectController getInstance() {
		if (instance==null) {
			instance = new OpenedProjectController();
		}
		return instance;
	}
	
	private OpenedProjectController() {
		//
	}


	
	public IProject getOpenedProject() {
		return openedStudy;
	}
	
	public void setMediaAvailable(boolean isMediaAvailable) {
		this.isMediaControllerAvailable = isMediaAvailable;
	}
	
	
	public void dispose() {
		setClosedStudyState();
		removeResourceChangeListener();
	}



	public boolean isMediaControllerAvailable() {
		return isMediaControllerAvailable;
	}

	public boolean isSubjectFolderAvailable() {
		return isSubjectFolderAvailable;
	}

	public boolean isMediaCollectionControllerAvailable() {
		return isMediaCollectionControllerAvailable;
	}

	public void initialise() {
		addResourceChangeListener();
		lookForInitialOpenedProject();
	}
	
	private void updateOpenStudyState(IProject studyOpening) {
		this.studyToBeOpened = null;
		setOpenedStudyState(studyOpening);
	}
	
	private void checkProjectState() {
		setClosedStudyState();
		if (studyToBeOpened!=null) {
			setOpenedStudyState(studyToBeOpened);
			studyToBeOpened = null;
		}
	}
	
	private void setOpenedStudyState(IProject openedStudy) {
		logger.debug("Trying to configure opened study");
		this.openedStudy = openedStudy;
		try {
			createLocalDB();
			WorkspaceResourceUtil.refreshResource(openedStudy);
			mediaController = new MediaController(openedStudy.getFolder(WorkspaceController.MEDIA_DIR_NAME), openedStudy.getFolder(WorkspaceController.DATA_DIR_NAME));
			firePropertyChange(PROP_IS_MEDIA_CONTROLLER_AVAILABLE, isMediaControllerAvailable, isMediaControllerAvailable = true);
			collectionController = new MediaCollectionController(); 
			firePropertyChange(PROP_IS_MEDIA_COLLECTION_CONTROLLER_AVAILABLE, isMediaCollectionControllerAvailable, isMediaCollectionControllerAvailable = true);
			logger.debug("Done");
		} catch (Exception e) {
			logger.error("Unable to configure the opened study", e);
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error", e.getMessage());
			openedStudy = null;
		}
	}
	
	private void createLocalDB() throws Exception {
		WorkspaceModifyOperation createLocalDBOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				Map<String, Object> map = new HashMap<String, Object>();
				try {
					String dbUri = String.format(
							"jdbc:derby:%s/%s/%s;create=true", openedStudy.getLocation().toString(), WorkspaceController.LOCAL_DIR_NAME, PROJECT_DB);
					map.put(PersistenceUnitProperties.JDBC_URL, dbUri);
					map.put(PersistenceUnitProperties.CLASSLOADER, Activator.class.getClassLoader());
					logger.debug("Trying to create DB with URL " + dbUri);
					eManagerFactory = new PersistenceProvider().createEntityManagerFactory(PROJECT_DB, map);
					//Trigger database creation
					eManagerFactory.createEntityManager().close();
					logger.debug("DB created..");
				} catch (Exception ex) {
					logger.error("Unable to initialise Local database", ex);
					error = ex;
				}
				
			}
		};
		RunnableUtil.runWithProgress(createLocalDBOperation);
		if (createLocalDBOperation.getError()!=null)
			throw new Exception("Unable to initialise Local database.", createLocalDBOperation.getError());
	}

	public EntityManagerFactory getEntityManagerFactory() {
		return eManagerFactory;
	}

	private void setClosedStudyState() {		
		logger.debug("Trying to close the study and controllers");
		firePropertyChange(PROP_IS_MEDIA_COLLECTION_CONTROLLER_AVAILABLE, isMediaCollectionControllerAvailable, isMediaCollectionControllerAvailable = false);
		firePropertyChange(PROP_IS_MEDIA_CONTROLLER_AVAILABLE, isMediaControllerAvailable, isMediaControllerAvailable = false);
		if (collectionController!=null) {
			collectionController.dispose();
			collectionController = null;
		}
		if (mediaController!=null) {
			mediaController.dispose();
			mediaController = null;
		}		
		openedStudy = null;
		if (eManagerFactory!=null && eManagerFactory.isOpen()) {
			eManagerFactory.close();
			logger.debug("DB entity factory closed");
		}
	}

	
	private void lookForInitialOpenedProject() {
		for (IProject study : WorkspaceController.getInstance().getWorkspaceRoot().getProjectResourceList()) {
			if (study.isOpen()) {
				updateOpenStudyState(study);
				break;
			}
		}
	}

	private void addResourceChangeListener() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(projectResourceChangeListener, IResourceChangeEvent.POST_CHANGE);
	}
	
	private void removeResourceChangeListener() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(projectResourceChangeListener);
	}

	public MediaController getMediaController() {
		return mediaController;
	}

	public MediaCollectionController getMediaCollectionController() {
		return collectionController;
	}


	
}
