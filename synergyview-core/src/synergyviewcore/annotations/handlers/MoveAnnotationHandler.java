package synergyviewcore.annotations.handlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.annotations.ui.AnnotationIntervalImpl;
import synergyviewcore.navigation.NavigatorLabelProvider;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.subjects.model.Subject;
import synergyviewcore.subjects.model.SubjectNode;

/**
 * The Class MoveAnnotationHandler.
 */
public class MoveAnnotationHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands .ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
	final ILog logger = Activator.getDefault().getLog();

	ISelection selection = HandlerUtil.getCurrentSelection(event);
	IWorkbenchPart activePart = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActivePart();
	if (!(selection instanceof IStructuredSelection)) {
	    return null;
	}
	IStructuredSelection structSel = (IStructuredSelection) selection;

	@SuppressWarnings("rawtypes")
	Iterator iteratorCaptionInterval = structSel.iterator();
	while (iteratorCaptionInterval.hasNext()) {
	    Object element = iteratorCaptionInterval.next();
	    if (element instanceof AnnotationIntervalImpl) {
		AnnotationIntervalImpl annotationInterval = (AnnotationIntervalImpl) element;
		try {

		    // TODO This can be moved to AnnotationSetNode
		    ElementListSelectionDialog dialog = new ElementListSelectionDialog(activePart.getSite().getShell(), new NavigatorLabelProvider());
		    AnnotationSetNode annotationSetNode = annotationInterval.getOwner().getAnnotationSetNode();
		    ProjectNode projectNode = (ProjectNode) annotationSetNode.getLastParent();

		    List<INode> nodesToShow = new ArrayList<INode>();
		    // Removing existing subjects from the list
		    List<Subject> existingSubjects = annotationSetNode.getSubjectList();
		    for (INode node : projectNode.getSubjectRootNode().getChildren()) {
			if (existingSubjects.contains(((SubjectNode) node).getResource()) && (node.getResource() != annotationInterval.getOwner().getSubject())) {
			    nodesToShow.add(node);
			}
		    }
		    dialog.setElements(nodesToShow.toArray(new INode[] {}));
		    dialog.open();
		    if (dialog.getFirstResult() != null) {
			SubjectNode result = (SubjectNode) dialog.getFirstResult();
			annotationSetNode.moveAnnotation(annotationInterval.getAnnotation(), annotationInterval.getOwner().getSubject(), result.getResource());
		    }

		} catch (Exception ex) {
		    IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
		    logger.log(status);
		}
		return null;
	    }
	}

	return null;

    }

}
