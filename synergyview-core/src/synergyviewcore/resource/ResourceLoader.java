package synergyviewcore.resource;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import synergyviewcore.Activator;

/**
 * The Class ResourceLoader.
 */
public class ResourceLoader {
	
	/** The Constant BUNDLE_NAME. */
	private static final String BUNDLE_NAME = "synergyviewcore.resource.custom";
	
	/** The rb. */
	private static ResourceBundle rb = null;
	
	/**
	 * Gets the icon descriptor.
	 * 
	 * @param fileName
	 *            the file name
	 * @return the icon descriptor
	 */
	public static ImageDescriptor getIconDescriptor(String fileName) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				"icons/" + fileName);
	}
	
	/**
	 * Gets the icon from program.
	 * 
	 * @param program
	 *            the program
	 * @return the icon from program
	 */
	public static ImageDescriptor getIconFromProgram(Program program) {
		
		ImageData imageData = program.getImageData();
		if (imageData != null) {
			return ImageDescriptor.createFromImageData(imageData);
		}
		return null;
	}
	
	/**
	 * Gets the string.
	 * 
	 * @param key
	 *            the key
	 * @return the string
	 */
	public static String getString(String key) {
		final ILog logger = Activator.getDefault().getLog();
		try {
			String keyValue = new String(rb.getString(key).getBytes(
					"ISO-8859-1"), "UTF-8");
			return keyValue;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			return key;
		}
	}
	
	/**
	 * Sets the bundle.
	 * 
	 * @param locale
	 *            the new bundle
	 */
	public static void setBundle(Locale locale) {
		final ILog logger = Activator.getDefault().getLog();
		try {
			rb = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					ex.getMessage(), ex);
			logger.log(status);
			rb = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
		}
	}
	
}
