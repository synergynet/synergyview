package synergyviewcore;

import java.lang.management.ManagementFactory;
import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;

import uk.co.caprica.vlcj.discovery.NativeDiscovery;

/**
 * The Class ApplicationWorkbenchAdvisor.
 */
@SuppressWarnings("restriction")
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
	/** The Constant PERSPECTIVE_ID. */
	private static final String PERSPECTIVE_ID = "synergyviewcore.perspective";
	
	/** The logger. */
	private ILog logger;
	
	/** The shut down done. */
	boolean shutDownDone = false;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.application.WorkbenchAdvisor#createWorkbenchWindowAdvisor
	 * (org.eclipse.ui.application.IWorkbenchWindowConfigurer)
	 */
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		logger = Activator.getDefault().getLog();
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}
	
	/**
	 * Declare workbench image.
	 * 
	 * @param ideBundle
	 *            the ide bundle
	 * @param symbolicName
	 *            the symbolic name
	 * @param path
	 *            the path
	 * @param shared
	 *            the shared
	 */
	private void declareWorkbenchImage(Bundle ideBundle, String symbolicName,
			String path, boolean shared) {
		URL url = ideBundle.getEntry(path);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
	}
	
	/**
	 * Declare workbench images.
	 */
	private void declareWorkbenchImages() {
		final String ICONS_PATH = "icons/full/";//$NON-NLS-1$
		final String PATH_ELOCALTOOL = ICONS_PATH + "elcl16/"; //Enabled toolbar icons.//$NON-NLS-1$
		final String PATH_ETOOL = ICONS_PATH + "etool16/"; //Enabled toolbar icons.//$NON-NLS-1$
		final String PATH_DTOOL = ICONS_PATH + "dtool16/"; //Disabled toolbar icons.//$NON-NLS-1$
		final String PATH_OBJECT = ICONS_PATH + "obj16/"; //Model object icons//$NON-NLS-1$
		final String PATH_WIZBAN = ICONS_PATH + "wizban/"; //Wizard icons//$NON-NLS-1$
		
		Bundle ideBundle = Platform.getBundle(IDEWorkbenchPlugin.IDE_WORKBENCH);
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC, PATH_ETOOL
						+ "build_exec.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC_HOVER,
				PATH_ETOOL + "build_exec.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_BUILD_EXEC_DISABLED,
				PATH_DTOOL + "build_exec.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC, PATH_ETOOL
						+ "search_src.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC_HOVER,
				PATH_ETOOL + "search_src.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_SEARCH_SRC_DISABLED,
				PATH_DTOOL + "search_src.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_NEXT_NAV, PATH_ETOOL
						+ "next_nav.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_ETOOL_PREVIOUS_NAV, PATH_ETOOL
						+ "prev_nav.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_WIZBAN_NEWPRJ_WIZ, PATH_WIZBAN
						+ "newprj_wiz.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_WIZBAN_NEWFOLDER_WIZ,
				PATH_WIZBAN + "newfolder_wiz.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_WIZBAN_NEWFILE_WIZ, PATH_WIZBAN
						+ "newfile_wiz.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_WIZBAN_IMPORTDIR_WIZ,
				PATH_WIZBAN + "importdir_wiz.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_WIZBAN_IMPORTZIP_WIZ,
				PATH_WIZBAN + "importzip_wiz.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_WIZBAN_EXPORTDIR_WIZ,
				PATH_WIZBAN + "exportdir_wiz.gif", false); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_WIZBAN_EXPORTZIP_WIZ,
				PATH_WIZBAN + "exportzip_wiz.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
		
		IDEInternalWorkbenchImages.IMG_WIZBAN_RESOURCEWORKINGSET_WIZ,
				PATH_WIZBAN + "workset_wiz.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_DLGBAN_SAVEAS_DLG, PATH_WIZBAN
						+ "saveas_wiz.gif", false); //$NON-NLS-1$
		
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT,
				PATH_OBJECT + "prj_obj.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT
						+ "cprj_obj.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OPEN_MARKER,
				PATH_ELOCALTOOL + "gotoobj_tsk.gif", true); //$NON-NLS-1$
		
		// task objects
		
		// declareRegistryImage(IDEInternalWorkbenchImages.IMG_OBJS_HPRIO_TSK,
		// PATH_OBJECT+"hprio_tsk.gif");
		
		// declareRegistryImage(IDEInternalWorkbenchImages.IMG_OBJS_MPRIO_TSK,
		// PATH_OBJECT+"mprio_tsk.gif");
		
		// declareRegistryImage(IDEInternalWorkbenchImages.IMG_OBJS_LPRIO_TSK,
		// PATH_OBJECT+"lprio_tsk.gif");
		
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJS_TASK_TSK,
				PATH_OBJECT + "taskmrk_tsk.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJS_BKMRK_TSK,
				PATH_OBJECT + "bkmrk_tsk.gif", true); //$NON-NLS-1$
		
		String string = IDEInternalWorkbenchImages.IMG_OBJS_COMPLETE_TSK;
		declareWorkbenchImage(ideBundle, string, PATH_OBJECT
				+ "complete_tsk.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_OBJS_INCOMPLETE_TSK, PATH_OBJECT
						+ "incomplete_tsk.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_ITEM, PATH_OBJECT
						+ "welcome_item.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_BANNER, PATH_OBJECT
						+ "welcome_banner.gif", true); //$NON-NLS-1$
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#getDefaultPageInput()
	 */
	@Override
	public IAdaptable getDefaultPageInput() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.application.WorkbenchAdvisor#getInitialWindowPerspectiveId
	 * ()
	 */
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	};
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.application.WorkbenchAdvisor#initialize(org.eclipse.ui
	 * .application.IWorkbenchConfigurer)
	 */
	@Override
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		org.eclipse.ui.ide.IDE.registerAdapters();
		declareWorkbenchImages();
		
		boolean found = false;
		String vlcLib = ManagementFactory.getRuntimeMXBean()
				.getSystemProperties().get("vlc");
		if (vlcLib != null) {
    		found = true;
    		NativeLibrary.addSearchPath("vlc", vlcLib); 
		}
		if (!found) {
			found = new NativeDiscovery().discover();
		}
		if (!found) {
			MessageDialog
					.openError(
							PlatformUI.getWorkbench().getDisplay()
									.getActiveShell(),
							"Error",
						"Cannot play videos.  VLC is either not installed or located in an unexpected directory.  " + 
						"If VLC is installed in an unexpected directory you can provide the path to its library. " + 
						"Find SynergyView.ini and add a line to the end (if not already there) starting with " + 
						"-Dvlc= followed by the path to an installation instance of VLC's lib folder."); 
		}
		
	};
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#postShutdown()
	 */
	@Override
	public void postShutdown() {
		shutDownDone = true;
		super.postShutdown();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchAdvisor#preShutdown()
	 */
	@Override
	public boolean preShutdown() {
		boolean toReturn = super.preShutdown();
		
		// emergency timer
		Runnable checkForFreeze = new Runnable() {
			public void run() {
				
				int loop = 0;
				
				while (loop < 1000) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (shutDownDone) {
						break;
					}
					loop++;
				}
				
				if (!shutDownDone) {
					logger.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Failed to close SynergyView - Forcing Shutdown"));
					System.exit(1);
				}
			}
		};
		
		Thread checkForFreezeThread = new Thread(checkForFreeze);
		checkForFreezeThread.start();
		
		return toReturn;
	}
}