package synergyviewcore.intro;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import synergyviewcore.collection.ui.views.MediaCollectionExplorerViewPart;
import synergyviewcore.media.ui.views.MediaExplorerViewPart;
import synergyviewcore.media.ui.views.VideoPlayerViewPart;
import synergyviewcore.workspace.ui.view.StudyWorkspaceExplorerViewPart;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.addView(StudyWorkspaceExplorerViewPart.ID, IPageLayout.LEFT, 0.25f, editorArea);
		layout.addView(MediaCollectionExplorerViewPart.ID, IPageLayout.BOTTOM, 0.3f, StudyWorkspaceExplorerViewPart.ID);
		layout.addView(MediaExplorerViewPart.ID, IPageLayout.BOTTOM, 0.5f, MediaCollectionExplorerViewPart.ID);
		layout.addStandaloneView(VideoPlayerViewPart.ID, false, IPageLayout.TOP, 0.25f, editorArea);
		layout.addPlaceholder("org.eclipse.ui.views.ProgressView", IPageLayout.BOTTOM, 0.75f, editorArea);
	}

}
