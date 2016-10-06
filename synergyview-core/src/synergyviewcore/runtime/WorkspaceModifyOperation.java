package synergyviewcore.runtime;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;

public abstract class WorkspaceModifyOperation implements IRunnableWithProgress, IThreadListener {
    private ISchedulingRule rule;
    
    protected Throwable error;
    
	public Throwable getError() {
		return error;
	}
    /**
     * Creates a new operation.
     */
    protected WorkspaceModifyOperation() {
        this(ResourcesPlugin.getWorkspace().getRoot());
    }

    /**
     * Creates a new operation that will run using the provided
     * scheduling rule.
     * @param rule  The ISchedulingRule to use or <code>null.
     * @since 3.0
     */
    protected WorkspaceModifyOperation(ISchedulingRule rule) {
        this.rule = rule;
    }

    /**
     * Performs the steps that are to be treated as a single logical workspace
     * change.
     * <p>
     * Subclasses must implement this method.
     * </p>
     *
     * @param monitor the progress monitor to use to display progress and field
     *   user requests to cancel
     * @exception CoreException if the operation fails due to a CoreException
     * @exception InvocationTargetException if the operation fails due to an exception other than CoreException
     * @exception InterruptedException if the operation detects a request to cancel, 
     *  using <code>IProgressMonitor.isCanceled(), it should exit by throwing 
     *  <code>InterruptedException.  It is also possible to throw 
     *  <code>OperationCanceledException, which gets mapped to InterruptedException
     *  by the <code>run method.
     */
    protected abstract void execute(IProgressMonitor monitor)
            throws CoreException, InvocationTargetException,
            InterruptedException;

    /**
     * The <code>WorkspaceModifyOperation implementation of this 
     * <code>IRunnableWithProgress method initiates a batch of changes by 
     * invoking the <code>execute method as a workspace runnable 
     * (<code>IWorkspaceRunnable).
     */
    public synchronized final void run(IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {
        final InvocationTargetException[] iteHolder = new InvocationTargetException[1];
        try {
            IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
                public void run(IProgressMonitor pm) throws CoreException {
                    try {
                        execute(pm);
                    } catch (InvocationTargetException e) {
                        // Pass it outside the workspace runnable
                        iteHolder[0] = e;
                    } catch (InterruptedException e) {
                        // Re-throw as OperationCanceledException, which will be
                        // caught and re-thrown as InterruptedException below.
                        throw new OperationCanceledException(e.getMessage());
                    }
                    // CoreException and OperationCanceledException are propagated
                }
            };
            ResourcesPlugin.getWorkspace().run(workspaceRunnable,
                    rule, IResource.NONE, monitor);
        } catch (CoreException e) {
            throw new InvocationTargetException(e);
        } catch (OperationCanceledException e) {
            throw new InterruptedException(e.getMessage());
        }
        // Re-throw the InvocationTargetException, if any occurred
        if (iteHolder[0] != null) {
            throw iteHolder[0];
        }
    }
	/* (non-Javadoc)
	 * @see IThreadListener#threadChange(Thread);
	 * @since 3.2
	 */
	public void threadChange(Thread thread) {
		//we must make sure we aren't transferring control away from a thread that
		//already owns a scheduling rule because this is deadlock prone (bug 105491)
		if (rule == null) {
			return;
		}
		Job currentJob = Job.getJobManager().currentJob();
		if (currentJob == null) {
			return;
		}
		ISchedulingRule currentRule = currentJob.getRule();
		if (currentRule == null) {
			return;
		}
		throw new IllegalStateException("Cannot fork a thread from a thread owning a rule"); //$NON-NLS-1$
	}

	/**
	 * The scheduling rule.  Should not be modified.
	 * @return the scheduling rule, or <code>null.
	 * @since 3.4
	 */
	public ISchedulingRule getRule() {
		return rule;
	}
	
	
}
