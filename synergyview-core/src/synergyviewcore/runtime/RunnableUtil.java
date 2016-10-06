package synergyviewcore.runtime;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.PlatformUI;

public class RunnableUtil {
	private static Logger logger = Logger.getLogger(RunnableUtil.class);
	public static void runWithProgress(IRunnableWithProgress runnableWithProgress) {
		try {
			if (PlatformUI.isWorkbenchRunning())
				PlatformUI.getWorkbench().getProgressService().run(false, true, runnableWithProgress);
		} catch (InvocationTargetException e) {
			logger.error("Long running process error.", e);
		} catch (InterruptedException e) {
			logger.error("Long running process interrupted error.", e);
		}
	}

}
