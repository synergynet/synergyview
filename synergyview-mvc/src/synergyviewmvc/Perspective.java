package synergyviewmvc;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import synergyviewmvc.annotations.ui.views.AnnotationAttributesViewPart;
import synergyviewmvc.annotations.ui.views.AnnotationPropertyViewPart;
import synergyviewmvc.projects.ui.ProjectExplorerViewPart;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.getViewLayout(ProjectExplorerViewPart.ID).setCloseable(false);
		layout.getViewLayout(AnnotationPropertyViewPart.ID).setCloseable(false);
		layout.getViewLayout(AnnotationAttributesViewPart.ID).setCloseable(false);
		layout.addView(ProjectExplorerViewPart.ID, IPageLayout.LEFT, 0.25f, editorArea);
		IFolderLayout folder = layout.createFolder("Annoataions", IPageLayout.BOTTOM, 0.5f, ProjectExplorerViewPart.ID);
		folder.addView(AnnotationPropertyViewPart.ID);
		folder.addView(AnnotationAttributesViewPart.ID);
	}
}
