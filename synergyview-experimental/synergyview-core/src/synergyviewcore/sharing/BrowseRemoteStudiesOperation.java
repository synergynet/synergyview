package synergyviewcore.sharing;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import synergyviewcore.sharing.model.SharingInfo;
import synergyviewcore.sharing.model.SvnServerInfo;
import synergyviewcore.sharing.preference.SvnServerInfoPreferenceHelper;

public class BrowseRemoteStudiesOperation implements IRunnableWithProgress {
	
	private static Logger logger = Logger.getLogger(BrowseRemoteStudiesOperation.class);
	private List<SharingInfo> remoteSharingInfoList = new ArrayList<SharingInfo>();
	private SvnServerInfoPreferenceHelper svnServerInfoPreferenceHelper;
	private String studyRootFolderURI;
	private Exception error;
	
	public BrowseRemoteStudiesOperation(SvnServerInfoPreferenceHelper svnServerInfoPreferenceHelper, String studyRootFolderURI) {
		this.svnServerInfoPreferenceHelper = svnServerInfoPreferenceHelper;
		this.studyRootFolderURI = studyRootFolderURI;
	}
	
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		final SvnServerInfo svnServerInfo = svnServerInfoPreferenceHelper.getSvnServerInfo();
		final List<String> remoteUserNames = new ArrayList<String>();
		SVNClientManager svnClientManager = svnServerInfoPreferenceHelper.getSVNClientManager();
		ISVNDirEntryHandler remoteUserNamesDirEntryHandler = new ISVNDirEntryHandler() {
			@Override
			public void handleDirEntry(SVNDirEntry entry) throws SVNException {
				if (!entry.getRelativePath().isEmpty()) { 
					if (entry.getAuthor().compareTo(svnServerInfo.getUserName())!=0) {
						remoteUserNames.add(entry.getName());
					}
				}
				
			}
		};
		
		ISVNDirEntryHandler remoteUserProjectsDirEntryHandler = new ISVNDirEntryHandler() {
			@Override
			public void handleDirEntry(SVNDirEntry entry) throws SVNException {
				if (!entry.getRelativePath().isEmpty()) {
					SharingInfo remoteSharingInfo = new SharingInfo();
					remoteSharingInfo.setOwnerName(entry.getAuthor());
					remoteSharingInfo.setProjectName(entry.getName());
					remoteSharingInfo.setUrl(entry.getURL());
					remoteSharingInfo.setCommitDate(entry.getDate());
					remoteSharingInfoList.add(remoteSharingInfo);
				}
			}
		};
		try {
			monitor.beginTask("Getting remote Studies.", 2);
			if (monitor.isCanceled())
				//throw new SharingException("Unable to browse remote Studies. Operation was cancelled.");
			monitor.setTaskName("Getting remote Users...");
			svnClientManager.getLogClient().doList(SVNURL.parseURIDecoded(studyRootFolderURI), SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, remoteUserNamesDirEntryHandler);
			monitor.worked(1);
			if (monitor.isCanceled())
				//throw new SharingException("Unable to browse remote Studies. Operation was cancelled.");
			monitor.setTaskName("Getting remote Study names...");
			for (String name : remoteUserNames) {
				svnClientManager.getLogClient().doList(SVNURL.parseURIDecoded(studyRootFolderURI + name), SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, remoteUserProjectsDirEntryHandler);
			}
			monitor.worked(1);
		} catch (SVNException ex) {
			error = ex;
			logger.error("SVN error, unable to browse remote studies", ex);
		} finally {
			monitor.done();
		}
	}
	
	public Exception getError() {
		return error;
	}
	
	public  List<SharingInfo> getRemoteSharingInfoList() {
		return remoteSharingInfoList;
	}
}
