package synergyviewcore;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

import synergyviewcore.annotations.ui.views.AnnotationPropertyViewPart;
import synergyviewcore.annotations.ui.views.AnnotationTableViewPart;
import synergyviewcore.attributes.ui.views.CodingExplorerViewPart;
import synergyviewcore.projects.ui.ProjectExplorerViewPart;

/**
 * The Class Perspective.
 */
public class Perspective implements IPerspectiveFactory {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui .IPageLayout)
     */
    public void createInitialLayout(IPageLayout layout) {
	String editorArea = layout.getEditorArea();
	layout.getViewLayout(ProjectExplorerViewPart.ID).setCloseable(false);
	// layout.getViewLayout(AnnotationPropertyViewPart.ID).setCloseable(false);
	layout.addView(ProjectExplorerViewPart.ID, IPageLayout.LEFT, 0.25f, editorArea);
	layout.addView(CodingExplorerViewPart.ID, IPageLayout.BOTTOM, 0.25f, ProjectExplorerViewPart.ID);
	layout.addPlaceholder(AnnotationTableViewPart.ID, IPageLayout.BOTTOM, 0.7f, editorArea);
	IPlaceholderFolderLayout folder = layout.createPlaceholderFolder("Annoataions", IPageLayout.BOTTOM, 0.5f, CodingExplorerViewPart.ID);
	folder.addPlaceholder(AnnotationPropertyViewPart.ID);
	folder.addPlaceholder("org.eclipse.pde.runtime.LogView");
    }
}
