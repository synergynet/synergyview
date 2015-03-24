package synergyviewcore.projects.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import synergyviewcore.Activator;
import synergyviewcore.media.model.MediaRootNode;
import synergyviewcore.resource.ResourceLoader;


/**
 * The Class NewProjectWizard.
 */
public class NewProjectWizard extends Wizard implements INewWizard {
	
	/** The _main page. */
	private WizardNewProjectCreationPage _mainPage;
	
	/** The logger. */
	private final ILog logger;
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		super.addPages();
		
		_mainPage = new WizardNewProjectCreationPage(ResourceLoader.getString("DIALOG_TITLE_NEW_PROJECT"));
		_mainPage.setTitle(ResourceLoader.getString("DIALOG_TITLE_NEW_PROJECT"));
		_mainPage.setDescription(ResourceLoader.getString("DIALOG_MESSAGE_NEW_PROJECT"));

		addPage(_mainPage);
	}

	/**
	 * Instantiates a new new project wizard.
	 */
	public NewProjectWizard() {
		logger = Activator.getDefault().getLog();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		createNewProject();
		return true;
	}

	/**
	 * Creates the new project.
	 *
	 * @return the i project
	 */
	public IProject createNewProject() {

		final IProject newProjectHandle = _mainPage.getProjectHandle();

		// get a project descriptor
		IPath defaultPath = Platform.getLocation();
		IPath newPath = _mainPage.getLocationPath();
		if (defaultPath.equals(newPath)) {
			newPath = null;
		}
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace
				.newProjectDescription(newProjectHandle.getName());
		

		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				createProject(description, newProjectHandle, monitor);
			}
		};

		try {
			getContainer().run(false, true, op);
		} catch (InterruptedException e) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,e.getMessage(), e);
			logger.log(status);
			return null;
		} catch (InvocationTargetException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return null;
		}
		return newProjectHandle;

	}

	/**
	 * Creates the project.
	 *
	 * @param description the description
	 * @param projectHandle the project handle
	 * @param monitor the monitor
	 * @throws CoreException the core exception
	 */
	public void createProject(IProjectDescription description,
			IProject projectHandle, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask(ResourceLoader.getString("DIALOG_PROGRESS_MESSAGE_NEW_PROJECT"), 5);

			projectHandle.create(description, new SubProgressMonitor(monitor,
					1));
			
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
			projectHandle.open(new SubProgressMonitor(monitor, 2));
			IFolder mediaFolder = projectHandle.getFolder(MediaRootNode.getMediaFolderName());
			mediaFolder.create(false, true, new SubProgressMonitor(monitor, 3));
		} finally {
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

}
