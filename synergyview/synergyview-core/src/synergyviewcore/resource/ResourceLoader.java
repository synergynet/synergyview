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

public class ResourceLoader {
	private static final String BUNDLE_NAME = "synergyviewcore.resource.custom";
	private static ResourceBundle rb = null;
	
	public static void setBundle(Locale locale) {
		final ILog logger = Activator.getDefault().getLog();
		try {
			rb = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			rb = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
		}
	}

	public static String getString(String key) {
		final ILog logger = Activator.getDefault().getLog();
		try {
			String keyValue = new String(rb.getString(key).getBytes(
			        "ISO-8859-1"), "UTF-8");
			return keyValue;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return key;
		}
	}

	public static ImageDescriptor getIconDescriptor(String fileName) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/" + fileName);
	}
	
	public static ImageDescriptor getIconFromProgram(Program program) {
		
		ImageData imageData = program.getImageData();
		if (imageData != null) {
			return ImageDescriptor.createFromImageData(imageData);
		}
		return null;
	}

}

