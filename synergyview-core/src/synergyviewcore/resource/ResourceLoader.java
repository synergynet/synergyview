package synergyviewcore.resource;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import synergyviewcore.plugin.Activator;

public class ResourceLoader {
	private static final String BUNDLE_NAME = "uk.ac.durham.tel.synergynet.covanto.resource.custom";
	private static ResourceBundle rb = null;
	private static final String DEFAULT_ITEM_ICON = "default-item.png";
	
	static {
		rb = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
	}
	
	public static void setBundle(Locale locale) {
		try {
			rb = ResourceBundle.getBundle(BUNDLE_NAME, locale);
		} catch (Exception ex) {
			rb = ResourceBundle.getBundle(BUNDLE_NAME, Locale.ENGLISH);
		}
	}
	
	public static String getString(String key) {
		try {
			return new String(rb.getString(key).getBytes("ISO-8859-1"), "UTF-8");			
		} catch (Exception ex) {
			return key;
		}
	}
	
	public static ImageDescriptor getIconDescriptor(String iconFileName) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/" + iconFileName);
	}
	
	public static ImageDescriptor getIconFromProgram(Program program) {
		ImageData imageData = program.getImageData();
		if (imageData != null) {
			return ImageDescriptor.createFromImageData(imageData);
		}
		return getIconDescriptor(DEFAULT_ITEM_ICON);
	}
	
	public static ImageDescriptor getIconFromProgram(String fileExtension) {
		Program program = Program.findProgram(fileExtension);
		if (program != null)
			return ResourceLoader.getIconFromProgram(program);
		return getIconDescriptor(DEFAULT_ITEM_ICON);
	}

	public static ImageDescriptor getDefaultIconDescriptor() {
		return getIconDescriptor(DEFAULT_ITEM_ICON);
	}
	
	public static String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
	}
	
}

