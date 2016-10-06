package synergyviewcore.plugin;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.gstreamer.Gst;
import org.gstreamer.GstException;
import org.osgi.framework.BundleContext;

import synergyviewcore.runtime.RunnableUtil;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "uk.ac.durham.tel.synergynet.covanto"; //$NON-NLS-1$
	private static Logger logger = Logger.getLogger(Activator.class);
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		startGstreamer();
	}

	private void startGstreamer() throws Exception {
		try {
			Gst.init();
		} catch (GstException ex) {
			logger.error("Gstreamer can't be initialised", ex);
			throw new Exception("Gstreamer can't be initialised", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		stopGstreamer();
		super.stop(context);
	}

	private void stopGstreamer() {
		IRunnableWithProgress stopGstOperation = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Shutting down media library", 1);
				Gst.deinit();
				monitor.done();
			}
		};
		RunnableUtil.runWithProgress(stopGstOperation);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
}
