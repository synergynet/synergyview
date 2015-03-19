package synergyviewcore;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import synergyviewcore.plugin.Activator;

public class LogUtil {

	public static enum LogStatus {INFO, WARNING, ERROR, OK};
	
	public static void log(LogStatus logStatus, String message, Throwable throwable) {
		ILog log = Activator.getDefault().getLog();
		IStatus status = null;
		switch (logStatus) {
			case INFO:
				status = new Status(IStatus.INFO, Activator.PLUGIN_ID,  message, throwable);
				break;
			case WARNING:
				status = new Status(IStatus.WARNING, Activator.PLUGIN_ID,  message, throwable);
				break;
			case ERROR:
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,  message, throwable);
				break;
			case OK:
				status = new Status(IStatus.OK, Activator.PLUGIN_ID,  message);
				break;
		}
		log.log(status);
	}
	
}
