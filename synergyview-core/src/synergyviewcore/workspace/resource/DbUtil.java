package synergyviewcore.workspace.resource;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;

import synergyviewcore.LogUtil;
import synergyviewcore.LogUtil.LogStatus;
import synergyviewcore.project.OpenedProjectController;
import synergyviewcore.runtime.RunnableUtil;
import synergyviewcore.runtime.WorkspaceModifyOperation;

public class DbUtil {
	public static void importDataToDB(final String tableName, final IFile dataFile) throws Exception {
		EntityManager entityManager = null;
		try {
			EntityManagerFactory eManagerFactory = OpenedProjectController.getInstance().getEntityManagerFactory();
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			Query query = entityManager.createNativeQuery("CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE(null, ?, ?, null, null, 'UTF-8', 0)");
			query.setParameter(1, tableName);
			query.setParameter(2, dataFile.getLocation().toString());
			query.executeUpdate();
			entityManager.getTransaction().commit();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	public static void exportDataFromDB(final String tableName, final IFile dataFile) throws Exception {
		WorkspaceModifyOperation exportDataFromDBOperation = new WorkspaceModifyOperation() {
			@Override
			protected void execute(IProgressMonitor monitor)
					throws CoreException, InvocationTargetException,
					InterruptedException {
				EntityManager entityManager = null;
				try {
					IFile tempDataFile;
					if (!dataFile.exists()) {
						tempDataFile = dataFile;
					} else {
						tempDataFile = dataFile.getParent().getFile(new Path("tmp.data"));
					}
					EntityManagerFactory eManagerFactory = OpenedProjectController.getInstance().getEntityManagerFactory();
					entityManager = eManagerFactory.createEntityManager();
					entityManager.getTransaction().begin();
					Query query = entityManager.createNativeQuery("CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE(null, ?, ?, null, null, 'UTF-8')");
					query.setParameter(1, tableName);
					query.setParameter(2,tempDataFile.getLocation().toString());
					query.executeUpdate();
					entityManager.getTransaction().commit();
					tempDataFile.refreshLocal(IResource.DEPTH_ZERO, monitor);
					if (tempDataFile.exists() && tempDataFile!=dataFile) {
						dataFile.setContents(tempDataFile.getContents(), false, false, monitor);
						tempDataFile.delete(false, monitor);
					}
				} catch(Exception ex) {
					LogUtil.log(LogStatus.ERROR, "Unable to export data from database", ex);
					error = ex;
				} finally {
					if (entityManager.isOpen())
						entityManager.close();
					monitor.done();
				}
			}

		};
		RunnableUtil.runWithProgress(exportDataFromDBOperation);
		if (exportDataFromDBOperation.getError()!=null)
			throw new Exception("Unable to export data from database", exportDataFromDBOperation.getError());
	}
}
