package synergyviewcore.sharing;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;

import synergyviewcore.runtime.RunnableUtil;
import synergyviewcore.runtime.WorkspaceModifyOperation;
import synergyviewcore.sharing.model.NetworkSharingInfo;
import synergyviewcore.sharing.model.OwnSharingInfo;
import synergyviewcore.sharing.model.SharingInfo;
import synergyviewcore.sharing.model.SvnServerInfo;
import synergyviewcore.sharing.preference.SvnServerInfoPreferenceHelper;
import synergyviewcore.workspace.WorkspaceController;

//TODO This class needs refactoring

public class SharingController {
	
	private static Logger logger = Logger.getLogger(SharingController.class);
	public static SharingController instance;
	public final static String SVN_COVANTO_PATH = "covanto-projects";
	
	private SvnServerInfoPreferenceHelper svnServerInfoPreferenceHelper;
	
	private SharingController() {
		//
	}

	public void initialise() {
		svnServerInfoPreferenceHelper = new SvnServerInfoPreferenceHelper();
	}
	
	public static synchronized SharingController getInstance() {
		if (instance == null)
			instance = new SharingController();
		return instance;
	}

	public void shareProject(final IProject projectResource) throws SharingException {
		WorkspaceModifyOperation shareProjectOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor progressMonitor) {
				try {
					progressMonitor.beginTask("Sharing Study", 3);
					SVNClientManager svnClientManager = svnServerInfoPreferenceHelper.getSVNClientManager();
					File projectFile = projectResource.getLocation().toFile();
					System.out.println(projectFile.getAbsolutePath());
					String projectURL = getUserProjectsRootUrl() + projectResource.getName();
					progressMonitor.subTask("Importing Study directory...");
					svnClientManager.getCommitClient().doImport(projectFile, SVNURL.parseURIDecoded(projectURL), "Importing a study", new SVNProperties(), false, false, SVNDepth.IMMEDIATES);
					progressMonitor.worked(1);
					File projectDataFile = new File(projectFile, WorkspaceController.DATA_DIR_NAME);
					String projectDataURL = String.format("%s/%s", projectURL,WorkspaceController.DATA_DIR_NAME); //TODO Refactor
					progressMonitor.subTask("Importing Study data files...");
					svnClientManager.getCommitClient().doImport(projectDataFile, SVNURL.parseURIDecoded(projectDataURL), "Importing study data files", new SVNProperties(), false, false, SVNDepth.INFINITY);
					progressMonitor.worked(1);
					progressMonitor.subTask("Configuing local workspace for sharing...");
					svnClientManager.getUpdateClient().doCheckout(SVNURL.parseURIDecoded(projectURL), projectFile, SVNRevision.UNDEFINED, SVNRevision.HEAD, SVNDepth.INFINITY, true);
					projectResource.refreshLocal(IResource.DEPTH_ONE, progressMonitor);
					progressMonitor.worked(1);
				} catch (SVNException e) {
					logger.error("Unable to share Study.", e);
					error = e;
				} catch (CoreException e) {
					logger.error("Unable to share Study and record changes.", e);
					error = e;
				}
			}
		};
		RunnableUtil.runWithProgress(shareProjectOperation);
		if (shareProjectOperation.getError()!=null)
			throw new SharingException("Unable to share Study.", shareProjectOperation.getError());
	}

	public SharingInfo getProjectSharingInfo(IProject resource) throws SharingException {
		final SvnServerInfo svnServerInfo = svnServerInfoPreferenceHelper.getSvnServerInfo();
		SVNClientManager svnClientManager = svnServerInfoPreferenceHelper.getSVNClientManager();
		try {
			SharingInfo sharingInfo;
			SVNStatus status = svnClientManager.getStatusClient().doStatus(new File(resource.getLocationURI()), false);
			if (status.getAuthor().compareTo(svnServerInfo.getUserName())==0) {
				sharingInfo = new OwnSharingInfo();
			} else {
				sharingInfo = new NetworkSharingInfo();
			}
			sharingInfo.setOwnerName(status.getAuthor());
			sharingInfo.setProjectName(resource.getName());
			sharingInfo.setCommitDate(status.getCommittedDate());
			return sharingInfo;
		} catch (SVNException e) {
			throw new SharingException("Unable to get sharing infomation.", e);
		}
	}
	
	public boolean isProjectShared(IProject resource) {
		SVNClientManager svnClientManager = svnServerInfoPreferenceHelper.getSVNClientManager();
		try {
			svnClientManager.getStatusClient().doStatus(new File(resource.getLocationURI()), false);
				return true;
		} catch (SVNException e) {
			if (e.getErrorMessage().getErrorCode() != SVNErrorCode.WC_NOT_DIRECTORY)
				logger.error("Unable to check if Project is shared", e);
			return false;
		}
	}
	
	public List<SharingInfo> browseRemoteStudies() throws SharingException {
		BrowseRemoteStudiesOperation browseRemoteStudiesOperation = new BrowseRemoteStudiesOperation(svnServerInfoPreferenceHelper, projectsRootUrl());
		RunnableUtil.runWithProgress(browseRemoteStudiesOperation);
		if (browseRemoteStudiesOperation.getError()!=null)
			throw new SharingException("Unable to browse remote studies", browseRemoteStudiesOperation.getError());
		return browseRemoteStudiesOperation.getRemoteSharingInfoList();
	}

	public void downloadStudies(final List<SharingInfo> remoteSharingInfoList) throws SharingException {
		WorkspaceModifyOperation checkoutStudiesOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor) {
				try {
					SVNClientManager svnClientManager = svnServerInfoPreferenceHelper.getSVNClientManager();
					File workspaceRootDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
					monitor.beginTask("Downloading remote Studies.", remoteSharingInfoList.size());
					StringBuilder errorMessges = new StringBuilder();
					try {
						for (SharingInfo remoteSharingInfo : remoteSharingInfoList) {
							File projectDir = new File(workspaceRootDir, remoteSharingInfo.getProjectName());
							IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(remoteSharingInfo.getProjectName());
							IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
							if (project.exists()) {
								errorMessges.append(String.format("Study %s is already exist. ", project.getName()));
								monitor.worked(1);
								continue;
							}
							if(monitor.isCanceled())
								throw new OperationCanceledException("Download remote Studies was cancelled.");
							svnClientManager.getUpdateClient().doCheckout(remoteSharingInfo.getUrl(), projectDir, SVNRevision.HEAD, SVNRevision.HEAD,SVNDepth.INFINITY, false);
							project.create(description, monitor);
							monitor.worked(1);
						}
						if (!errorMessges.toString().isEmpty())
							error = new Exception(errorMessges.toString());
					} catch (SVNException e) {
						logger.info("Unable to checkout remote Studies.", e);
						error = e;
					} catch (CoreException e) {
						logger.error("Unable to process local workspace to download remote Studies.", e);
						error = e;
					}
				} finally {
					monitor.done();
				}
			}
		};
		RunnableUtil.runWithProgress(checkoutStudiesOperation);
		if (checkoutStudiesOperation.getError()!=null)
			throw new SharingException("Unable to checkout remote Studies.", checkoutStudiesOperation.getError());
	}

	public void dispose() {
		if (svnServerInfoPreferenceHelper!=null) {
			svnServerInfoPreferenceHelper.dispose();
			svnServerInfoPreferenceHelper = null;
		}
	}
	

	
	private String getProjectsRootPath() {
		return String.format("/%s/", SVN_COVANTO_PATH);
	}
	
	private String projectsRootUrl() {
		SvnServerInfo svnServerInfo = svnServerInfoPreferenceHelper.getSvnServerInfo();
		StringBuilder userProjectsRootUrl = new StringBuilder(svnServerInfo.getServerUrl());
		userProjectsRootUrl.append(getProjectsRootPath());
		return userProjectsRootUrl.toString();
	}
	
	private String getUserProjectsRootUrl() {
		SvnServerInfo svnServerInfo = svnServerInfoPreferenceHelper.getSvnServerInfo();
		StringBuilder userProjectsRootUrl = new StringBuilder(svnServerInfo.getServerUrl());
		userProjectsRootUrl.append(getUserProjectsRootPath());
		return userProjectsRootUrl.toString();
	}
	
	private String getUserProjectsRootPath() {
		SvnServerInfo svnServerInfo = svnServerInfoPreferenceHelper.getSvnServerInfo();
		return String.format("%s%s/", getProjectsRootPath(), svnServerInfo.getUserName());
	}
}
