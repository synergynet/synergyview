package synergyviewcore.workspace;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

import synergyviewcore.LogUtil;
import synergyviewcore.LogUtil.LogStatus;
import synergyviewcore.runtime.RunnableUtil;
import synergyviewcore.runtime.WorkspaceModifyOperation;
import synergyviewcore.workspace.model.WorkspaceRoot;


public class WorkspaceController {
	
	private static WorkspaceController instance;
	private WorkspaceRoot workspaceRoot;
    public static final String MEDIA_DIR_NAME = "media";
    public static final String DATA_DIR_NAME = "data";
    public static final String LOCAL_DIR_NAME = "local";
	private WorkspaceController() {
		//
	}

	public void initialise() {
		createWorkspaceRoot();
	}

	private void createWorkspaceRoot() {
		workspaceRoot = new WorkspaceRoot(ResourcesPlugin.getWorkspace().getRoot());
	}

	public void dispose() {
		workspaceRoot.dispose();
	}

	public void createStudy(final String studyName) throws WorkspaceException {
		WorkspaceModifyOperation createStudyOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor progressMonitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				try {
					progressMonitor.beginTask("Creating Study...", 3);
					IProject projectResource = ResourcesPlugin.getWorkspace().getRoot().getProject(studyName);
					projectResource.create(progressMonitor);
					progressMonitor.worked(1);
					openStudyOperation(projectResource, new SubProgressMonitor(progressMonitor, 1));
					progressMonitor.worked(1);
					createStudyDirectoryStructure(progressMonitor, projectResource);
					progressMonitor.worked(1);
				} catch (CoreException e) {
					LogUtil.log(LogStatus.ERROR, "Unable to create the Study.", e);
					error = e;
				} finally {
					progressMonitor.done();
				}
			}
		};
		RunnableUtil.runWithProgress(createStudyOperation);
		if (createStudyOperation.getError()!=null)
			throw new WorkspaceException("Unable to create the Study.", createStudyOperation.getError());
	}
	
	private void openStudyOperation(IProject studyToBeOpened, IProgressMonitor progressMonitor) throws CoreException {
		progressMonitor.beginTask("Opening Study", 2);
		for (IProject projectResourceEntry : ResourcesPlugin.getWorkspace().getRoot().getProjects()) { //Closing other opened Study
			if (projectResourceEntry.isOpen() && projectResourceEntry != studyToBeOpened)
				projectResourceEntry.close(progressMonitor);
		}
		progressMonitor.worked(1);
		studyToBeOpened.open(progressMonitor);
		progressMonitor.worked(1);
	}

	private void createStudyDirectoryStructure (
			IProgressMonitor progressMonitor, IProject projectResource)
			throws CoreException {
		IFolder mediaFolder = projectResource.getFolder(MEDIA_DIR_NAME);
		mediaFolder.create(false, true, progressMonitor);
		IFolder dataFolder = projectResource.getFolder(DATA_DIR_NAME);
		dataFolder.create(false, true, progressMonitor);
		IFolder localDBFolder = projectResource.getFolder(LOCAL_DIR_NAME);
		localDBFolder.create(false, true, progressMonitor);
	}
	
	public static synchronized WorkspaceController getInstance() {
		if (instance==null) {
			instance = new WorkspaceController();
		}
		return instance;
	}
	
	public void removeStudies(final List<IProject> studiesToBeRemoved) throws WorkspaceException {
		WorkspaceModifyOperation remoteStudiesOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor progressMonitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				progressMonitor.beginTask("Deleting Studies", studiesToBeRemoved.size());
				try {
					for (IProject studyToBeRemoved : studiesToBeRemoved) {
						if(progressMonitor.isCanceled())
							return;
						if (studyToBeRemoved.isOpen()) {
							studyToBeRemoved.close(progressMonitor);
						}
						studyToBeRemoved.delete(true, false, progressMonitor);
						progressMonitor.worked(1);
					}
				} catch (CoreException e) {
					LogUtil.log(LogStatus.ERROR, "Unable to delete the Studies.", e);
					error = e;
				} finally {
					progressMonitor.done();
				}
			}
		};
		RunnableUtil.runWithProgress(remoteStudiesOperation);
		if (remoteStudiesOperation.getError()!=null)
			throw new WorkspaceException("Unable to delete the Studies.", remoteStudiesOperation.getError());
	}
	
	public void openStudy(final IProject studyToBeOpened) throws WorkspaceException {
		WorkspaceModifyOperation openStudyOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor progressMonitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				try {
					openStudyOperation(studyToBeOpened, progressMonitor);
				} catch (CoreException e) {
					LogUtil.log(LogStatus.ERROR, "Unable to open the Study.", e);
					error = e;
				} finally {
					progressMonitor.done();
				}
			}
		};
		RunnableUtil.runWithProgress(openStudyOperation);
		if (openStudyOperation.getError()!=null)
			throw new WorkspaceException("Unable to open the Study.", openStudyOperation.getError());
	}
	
	public void closeStudy(final IProject studyToBeClosed) throws WorkspaceException {
		WorkspaceModifyOperation closeStudyOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor progressMonitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				progressMonitor.beginTask("Closing Study", 1);
				try {
					studyToBeClosed.close(progressMonitor);
					progressMonitor.worked(1);
				} catch (CoreException e) {
					LogUtil.log(LogStatus.ERROR, "Unable to close the Study.", e);
					error = e;
				} finally {
					progressMonitor.done();
				}
			}
		};
		RunnableUtil.runWithProgress(closeStudyOperation);
		if (closeStudyOperation.getError()!=null)
			throw new WorkspaceException("Unable to close the Study.", closeStudyOperation.getError());
	}
	

	public void renameStudy(final IProject studyToBeRenamed, final String newName) throws WorkspaceException {
		WorkspaceModifyOperation renameStudyOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor progressMonitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				try {
					progressMonitor.beginTask("Renaming Study", 1);
					IProjectDescription projectDescription = studyToBeRenamed.getDescription();
					projectDescription.setName(newName);
					studyToBeRenamed.move(projectDescription, false, progressMonitor);
					progressMonitor.worked(1);
				} catch (CoreException e) {
					LogUtil.log(LogStatus.ERROR, "Unable to rename the Study.", e);
					error = e;
				} finally {
					progressMonitor.done();
				}
			}
		};
		RunnableUtil.runWithProgress(renameStudyOperation);
		if (renameStudyOperation.getError()!=null)
			throw new WorkspaceException("Unable to rename the Study.", renameStudyOperation.getError());
	}

	public WorkspaceRoot getWorkspaceRoot() {
		return workspaceRoot;
	}
	
	
}
