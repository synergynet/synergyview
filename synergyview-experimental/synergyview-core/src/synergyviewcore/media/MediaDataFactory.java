package synergyviewcore.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;

import synergyviewcore.media.gstreamer.GstreamerMediaUtil;
import synergyviewcore.media.model.Media;
import synergyviewcore.media.model.MediaInfo;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.runtime.RunnableUtil;

public class MediaDataFactory {
	private static Logger logger = Logger.getLogger(MediaDataFactory.class);
	
	public static Media getMedia(File mediaFile, Shell shell) {
		GetMediaOperation op = new GetMediaOperation(mediaFile, shell);
		RunnableUtil.runWithProgress(op);
		return op.getMedia();
	}
	
	private static class GetMediaOperation implements IRunnableWithProgress {
		private MediaInfo mediaInfo;
		private File mediaFile;
		private Media media;
		private Shell shell;
		public GetMediaOperation(File mediaFile, Shell shell) {
			this.mediaFile = mediaFile;
			this.shell = shell;
		}
		
		public Media getMedia() {
			return media;
		}
		
		@Override
		public void run(IProgressMonitor monitor)
				throws InvocationTargetException, InterruptedException {
			try {
				monitor.beginTask("Creating Media Entry", 6);
				if(monitor.isCanceled())
					throw new OperationCanceledException("Validation was cancelled.");
				
				mediaInfo = GstreamerMediaUtil.getMediaInfo(mediaFile, new SubProgressMonitor(monitor, 4));
				media = new PlayableMedia();
				((PlayableMedia) media).setMediaInfo(mediaInfo); 
				monitor.worked(1);
			} catch (IllegalMediaFormatException e) {
				logger.info("Not a playable media.", null);
				MessageDialog.openWarning(shell, "Warning", 
						String.format("The '%s' file does not seem to be a playable media file. It will be imported as a miscellaneous item.", mediaFile.getName()));
				media = new Media();
				monitor.worked(1);
			} finally {
				media.setFileSize(mediaFile.length());
				media.setId(mediaFile.getName());
				monitor.worked(1);
				monitor.done();
			}
		}
	}

	public static void importExternalMediaFiles(final String[] fileNames, final IContainer destination) throws Exception {
		final StringBuilder errorMessages = new StringBuilder();
		WorkspaceJob job = new WorkspaceJob("Copying files to workspace.") {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				try {
					monitor.beginTask("", fileNames.length);
					for(String fileName : fileNames) {
						File file = new File(fileName);
						FileInputStream fileInputStream = null;
						try {
							fileInputStream = new FileInputStream(fileName);
							IFile fileResource = destination.getFile(new Path(file.getName()));
							monitor.setTaskName("Copying "+file.getName());
							if (monitor.isCanceled())
								break;
							if (fileResource.exists()) {
								fileResource.setContents(fileInputStream, false, false, monitor);
							} else {
								fileResource.create(fileInputStream, false, monitor);
							}
						} catch (FileNotFoundException e) {
							logger.error("Unable to find " + file.getName(), e);
							errorMessages.append("Unable to find " + file.getName());
						} finally {
							if (fileInputStream!=null) {
								try {
									fileInputStream.close();
								} catch (IOException e) {
									logger.error("Unable to close file stream..", e);
									errorMessages.append("Unable to close file stream..");
								}
							}
							monitor.worked(1);
						}
					}
					monitor.done();
				} finally {
					monitor.done();
				}				
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		if (!errorMessages.toString().isEmpty())
			throw new Exception(errorMessages.toString());
	}
	
	
}
