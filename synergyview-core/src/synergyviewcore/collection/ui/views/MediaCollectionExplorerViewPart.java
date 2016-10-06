package synergyviewcore.collection.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import synergyviewcore.LogUtil;
import synergyviewcore.LogUtil.LogStatus;
import synergyviewcore.collection.ui.model.MediaCollectionRootNode;
import synergyviewcore.navigation.NavigatorContentProvider;
import synergyviewcore.navigation.NavigatorLabelDecorator;
import synergyviewcore.navigation.ObservableNavigatorLabelProvider;
import synergyviewcore.project.OpenedProjectController;
import uk.ac.durham.tel.commons.jface.node.DisposeException;

public class MediaCollectionExplorerViewPart extends ViewPart {
	public static final String ID = "uk.ac.durham.tel.synergynet.covanto.collection.MediaCollectionExplorerViewPart";
	private Label mediaCollectionControllerNotAvailableLabel;
	private TreeViewer mediaCollectionTreeViewer;
	private Composite parentComposite;
	private MediaCollectionRootNode mediaClipRootNode;
	private PropertyChangeListener mediaCollectionDataAvailableListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					updateMediaDataAvailableUI(((Boolean) evt.getNewValue()).booleanValue());
				}
			});
		}
	};
	public MediaCollectionExplorerViewPart() {
		//
	}

	private void updateMediaDataAvailableUI(boolean booleanValue) {
		if (booleanValue) {
			disposeMediaInformationNotAvailableLabel();
			this.setPartName(String.format("Media Collection Explorer (%s)", OpenedProjectController.getInstance().getOpenedProject().getName()));
			setMediaInformationTreeViewer();
		} else {
			disposeMediaInformationTreeViewer();
			this.setPartName("Media Collection Explorer");
			setMediaInformationNotAvailableLabel();
		}
	}

	private void disposeMediaInformationTreeViewer() {
		if (mediaCollectionTreeViewer!=null && !mediaCollectionTreeViewer.getControl().isDisposed()) {
			mediaCollectionTreeViewer.setInput(null);
			mediaCollectionTreeViewer.getControl().dispose();
			mediaCollectionTreeViewer = null;
		}
	}

	private void disposeMediaInformationNotAvailableLabel() {
		if (mediaCollectionControllerNotAvailableLabel!=null && !mediaCollectionControllerNotAvailableLabel.isDisposed()) {
			mediaCollectionControllerNotAvailableLabel.dispose();
			mediaCollectionControllerNotAvailableLabel = null;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		addMediaCollectionControllerAvailableListener();
		parentComposite = parent;
		updateMediaDataAvailableUI(OpenedProjectController.getInstance().isMediaControllerAvailable());
	}
	
	@Override
	public void dispose() {
		removeMediaCollectionControllerAvailableListener();
		super.dispose();
	}

	private void removeMediaCollectionControllerAvailableListener() {
		OpenedProjectController.getInstance().removePropertyChangeListener(OpenedProjectController.PROP_IS_MEDIA_COLLECTION_CONTROLLER_AVAILABLE, mediaCollectionDataAvailableListener);
	}

	private void addMediaCollectionControllerAvailableListener() {
		OpenedProjectController.getInstance().addPropertyChangeListener(OpenedProjectController.PROP_IS_MEDIA_COLLECTION_CONTROLLER_AVAILABLE, mediaCollectionDataAvailableListener);
	}

	private void setMediaInformationNotAvailableLabel() {
		parentComposite.setLayout(new GridLayout(1, false));
		mediaCollectionControllerNotAvailableLabel = new Label(parentComposite, SWT.NONE);
		mediaCollectionControllerNotAvailableLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		mediaCollectionControllerNotAvailableLabel.setText("Media Clips Data is not available");
		parentComposite.layout();
	}
	
	private void setMediaInformationTreeViewer() {
		parentComposite.setLayout(new FillLayout());
		mediaCollectionTreeViewer = new TreeViewer(parentComposite, SWT.MULTI);
		parentComposite.layout();
		setupMediaInfoTreeViewerContent();
		setupMenuContribution();
		setupSelectionProvider();
	}

	private void setupSelectionProvider() {
		getSite().setSelectionProvider(mediaCollectionTreeViewer);
	}

	private void setupMenuContribution() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu (mediaCollectionTreeViewer.getControl());
		mediaCollectionTreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, mediaCollectionTreeViewer);
	}

	private void setupMediaInfoTreeViewerContent() {
		NavigatorContentProvider workspaceProjectsContentProvider = new NavigatorContentProvider(); 
		mediaCollectionTreeViewer.setContentProvider(workspaceProjectsContentProvider);
		mediaCollectionTreeViewer.setLabelProvider(new DecoratingStyledCellLabelProvider(new ObservableNavigatorLabelProvider(workspaceProjectsContentProvider.getKnownElements()), new NavigatorLabelDecorator(), null));
		mediaClipRootNode = new MediaCollectionRootNode(OpenedProjectController.getInstance().getMediaCollectionController());
		mediaCollectionTreeViewer.setInput(mediaClipRootNode);
		mediaCollectionTreeViewer.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					disposeMediaRootNode();
				} catch (DisposeException ex) {
					LogUtil.log(LogStatus.ERROR, "Unable to dispose Media Collection Node.", ex);
				}
			}

			
		});
	}
	
	private void disposeMediaRootNode() throws DisposeException {
		if (mediaClipRootNode != null) {
			mediaClipRootNode.dispose();
			mediaClipRootNode=null;
		}
	}
	
	@Override
	public void setFocus() {
		if (mediaCollectionTreeViewer!=null && !mediaCollectionTreeViewer.getControl().isDisposed())
			mediaCollectionTreeViewer.getControl().setFocus();
		if (mediaCollectionControllerNotAvailableLabel!=null && !mediaCollectionControllerNotAvailableLabel.isDisposed()) 
			mediaCollectionControllerNotAvailableLabel.setFocus();
	}
}
