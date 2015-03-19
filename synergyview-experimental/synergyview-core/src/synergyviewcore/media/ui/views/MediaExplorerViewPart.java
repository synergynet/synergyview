package synergyviewcore.media.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
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

import synergyviewcore.media.model.Media;
import synergyviewcore.media.model.MediaInfo;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.media.ui.MediaFileTreeDropAdapter;
import synergyviewcore.media.ui.model.MediaNode;
import synergyviewcore.media.ui.model.MediaRootNode;
import synergyviewcore.navigation.NavigatorContentProvider;
import synergyviewcore.navigation.NavigatorLabelDecorator;
import synergyviewcore.navigation.ObservableNavigatorLabelProvider;
import synergyviewcore.project.OpenedProjectController;
import uk.ac.durham.tel.commons.jface.node.DisposeException;

public class MediaExplorerViewPart extends ViewPart {
	public static final String ID = "uk.ac.durham.tel.synergynet.covanto.media.MediaExplorerViewPart";
	private Label mediaDataNotAvailableLabel;
	private TreeViewer mediaDataTreeViewer;
	private Composite parentComposite;
	private MediaRootNode mediaRootNode;
	private static Logger logger = Logger.getLogger(MediaExplorerViewPart.class);
	private PropertyChangeListener mediaInformationAvailableListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					updateMediaInformationUI(((Boolean) evt.getNewValue()).booleanValue());
				}
			});
		}
	};
	public MediaExplorerViewPart() {
		//
	}

	private void updateMediaInformationUI(boolean booleanValue) {
		if (booleanValue) {
			disposeMediaInformationNotAvailableLabel();
			this.setPartName(String.format("Media Explorer (%s)", OpenedProjectController.getInstance().getOpenedProject().getName()));
			setMediaInformationTreeViewer();
		} else {
			disposeMediaInformationTreeViewer();
			this.setPartName("Media Explorer");
			setMediaInformationNotAvailableLabel();
		}
	}

	private void disposeMediaInformationTreeViewer() {
		if (mediaDataTreeViewer!=null && !mediaDataTreeViewer.getControl().isDisposed()) {
			mediaDataTreeViewer.setInput(null);
			mediaDataTreeViewer.getControl().dispose();
			mediaDataTreeViewer = null;
		}
	}

	private void disposeMediaInformationNotAvailableLabel() {
		if (mediaDataNotAvailableLabel!=null && !mediaDataNotAvailableLabel.isDisposed()) {
			mediaDataNotAvailableLabel.dispose();
			mediaDataNotAvailableLabel = null;
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		addMediaInformationAvailableListener();
		parentComposite = parent;
		updateMediaInformationUI(OpenedProjectController.getInstance().isMediaControllerAvailable());
	}
	
	@Override
	public void dispose() {
		removeMediaInformationAvailableListener();
		super.dispose();
	}

	private void removeMediaInformationAvailableListener() {
		OpenedProjectController.getInstance().removePropertyChangeListener(OpenedProjectController.PROP_IS_MEDIA_CONTROLLER_AVAILABLE, mediaInformationAvailableListener);
	}

	private void addMediaInformationAvailableListener() {
		OpenedProjectController.getInstance().addPropertyChangeListener(OpenedProjectController.PROP_IS_MEDIA_CONTROLLER_AVAILABLE, mediaInformationAvailableListener);
	}

	private void setMediaInformationNotAvailableLabel() {
		parentComposite.setLayout(new GridLayout(1, false));
		mediaDataNotAvailableLabel = new Label(parentComposite, SWT.NONE);
		mediaDataNotAvailableLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		mediaDataNotAvailableLabel.setText("Media Information is not available");
		parentComposite.layout();
	}
	
	private void setMediaInformationTreeViewer() {
		parentComposite.setLayout(new FillLayout());
		mediaDataTreeViewer = new TreeViewer(parentComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		parentComposite.layout();
		setupMediaInfoTreeViewerContent();
		setupMenuContribution();
		setupSelectionProvider();
		setupDnDSupport();
	}

	private void setupDnDSupport() {
		int ops = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		   Transfer[] transfers = new Transfer[] {FileTransfer.getInstance()};
		   mediaDataTreeViewer.addDropSupport(ops, transfers, new MediaFileTreeDropAdapter(mediaDataTreeViewer));
	}

	private void setupSelectionProvider() {
		getSite().setSelectionProvider(mediaDataTreeViewer);
	}

	private void setupMenuContribution() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu (mediaDataTreeViewer.getControl());
		mediaDataTreeViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, mediaDataTreeViewer);
	}

	private void setupMediaInfoTreeViewerContent() {
		NavigatorContentProvider navigatorContentProvider = new NavigatorContentProvider(); 
		mediaDataTreeViewer.setContentProvider(navigatorContentProvider);
		mediaDataTreeViewer.getTree().setLinesVisible(true);
		mediaDataTreeViewer.getTree().setHeaderVisible(true);
		TreeViewerColumn name = new TreeViewerColumn(mediaDataTreeViewer, SWT.NONE);
		name.getColumn().setText("Name");
		name.getColumn().setWidth(150);
		name.setLabelProvider(new DecoratingStyledCellLabelProvider(new ObservableNavigatorLabelProvider(navigatorContentProvider.getKnownElements()), new NavigatorLabelDecorator(), null));
		
		TreeViewerColumn duration = new TreeViewerColumn(mediaDataTreeViewer, SWT.CENTER);
		duration.getColumn().setText("Duration");
		duration.getColumn().setWidth(80);
		duration.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				MediaNode node = (MediaNode) element;
				Media media;
				try {
					media = OpenedProjectController.getInstance().getMediaController().find(node.getResource());
					if (media instanceof PlayableMedia) {
						MediaInfo mediaInfo = ((PlayableMedia) media).getMediaInfo();
						if (mediaInfo!=null)
							return mediaInfo.getFormattedLength();
					} 
				} catch (Exception e) {
					//
				}
				return "";
			}
		});
		
		TreeViewerColumn format = new TreeViewerColumn(mediaDataTreeViewer, SWT.CENTER);
		format.setLabelProvider(new ColumnLabelProvider(){
			@Override
			public String getText(Object element) {
				MediaNode node = (MediaNode) element;
				Media media;
				try {
					media = OpenedProjectController.getInstance().getMediaController().find(node.getResource());
					if (media instanceof PlayableMedia) {
						MediaInfo mediaInfo = ((PlayableMedia) media).getMediaInfo();
						if (mediaInfo!=null)
							return mediaInfo.getContainerFormat();
					} 
				} catch (Exception e) {
					//
				}
				return "";
			}
		});
		format.getColumn().setText("Format");
		format.getColumn().setWidth(100);
		
		mediaRootNode = new MediaRootNode(OpenedProjectController.getInstance().getMediaController());
		mediaDataTreeViewer.setInput(mediaRootNode);
		mediaDataTreeViewer.getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				try {
					disposeMediaRootNode();
				} catch (DisposeException ex) {
					logger.error("Unable to dispose Media root node.", ex);
				}
			}

			
		});
	}
	
	private void disposeMediaRootNode() throws DisposeException {
		if (mediaRootNode != null) {
			mediaRootNode.dispose();
			mediaRootNode=null;
		}
	}
	
	@Override
	public void setFocus() {
		if (mediaDataTreeViewer!=null && !mediaDataTreeViewer.getControl().isDisposed())
			mediaDataTreeViewer.getControl().setFocus();
		if (mediaDataNotAvailableLabel!=null && !mediaDataNotAvailableLabel.isDisposed()) 
			mediaDataNotAvailableLabel.setFocus();
	}

}
