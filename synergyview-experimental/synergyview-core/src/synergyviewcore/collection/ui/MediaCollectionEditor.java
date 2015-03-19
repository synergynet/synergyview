package synergyviewcore.collection.ui;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import synergyviewcore.collection.gstreamer.GstreamerMediaCollectionPlayer;
import synergyviewcore.collection.model.MediaCollection;
import synergyviewcore.collection.model.MediaCollectionEntry;
import synergyviewcore.collection.ui.timebar.MediaCollectionEntryInterval;
import synergyviewcore.collection.ui.timebar.MediaControllerTimebarEditor;
import synergyviewcore.controller.ModelPersistenceException;
import synergyviewcore.controller.ObjectNotfoundException;
import synergyviewcore.media.model.PlayableMedia;
import synergyviewcore.media.ui.awt.GstreamerVideoContainer;
import synergyviewcore.project.OpenedProjectController;
import synergyviewcore.resource.ResourceLoader;

public class MediaCollectionEditor extends Composite {

	public static final String ADD = "add.gif";
	public static final String REMOVE = "remove.gif";
	public static final String START = "start.png";
	public static final String STOP = "stop.png";
	private String mediaCollectionId;
	private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	private static Logger logger = Logger.getLogger(MediaCollectionEditor.class);
	private MediaControllerTimebarEditor mediaControllerTimebarEditor;
	private Button removeMediaCollectionEntryButton;
	private Button playPalseButton;
	private ISelectionChangedListener mediaCollectionEditorTimebarSelectionListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			updateRemoveMediaCollectionEntryButtonState();
		}
	};
	
	public MediaCollectionEditor(String mediaCollectionId, Composite parent, int style) throws ObjectNotfoundException {
		super(parent, style);
		this.mediaCollectionId = mediaCollectionId;
		initUI();
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				disposeResources();				
			}
		});
	}
	
	private void updateRemoveMediaCollectionEntryButtonState() {
		if (removeMediaCollectionEntryButton == null || removeMediaCollectionEntryButton.isDisposed())
			return;
		IStructuredSelection selection = (IStructuredSelection) mediaControllerTimebarEditor.getSelection();
		if (selection.isEmpty() || !(selection.getFirstElement() instanceof MediaCollectionEntryInterval))
			removeMediaCollectionEntryButton.setEnabled(false);
		else removeMediaCollectionEntryButton.setEnabled(true);
	}

	private void initUI() throws ObjectNotfoundException {
		this.setLayout(new GridLayout());
		SashForm container = new SashForm(this, SWT.VERTICAL);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GstreamerVideoContainer gstreamerVideoComposite = new GstreamerVideoContainer(container, SWT.NONE);
		Composite editorControlComposite = new Composite(container, SWT.NONE);
		editorControlComposite.setLayout(new GridLayout(2, false));
		mediaControllerTimebarEditor = new MediaControllerTimebarEditor(editorControlComposite, mediaCollectionId);
		mediaControllerTimebarEditor.setLayoutData(new GridData(GridData.FILL_BOTH));
		mediaControllerTimebarEditor.addSelectionChangedListener(mediaCollectionEditorTimebarSelectionListener);
		Composite controls = new Composite(editorControlComposite, SWT.NONE);
		createMediaCollectionEditorControls(controls);
		try {
			new GstreamerMediaCollectionPlayer(mediaCollectionId, gstreamerVideoComposite);
		} catch (Exception ex) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to create media player.", ex.getMessage());
			playPalseButton.setEnabled(false);
		}
	}

	protected void disposeResources() {
		mediaControllerTimebarEditor.removeSelectionChangedListener(mediaCollectionEditorTimebarSelectionListener);
		resourceManager.dispose();
	}

	public void createMediaCollectionEditorControls(Composite controls) {
		controls.setLayoutData(new GridData(SWT.NONE,SWT.FILL,false,true));
		controls.setLayout(new GridLayout(1, false));
		playPalseButton = new Button(controls, SWT.NONE);
		playPalseButton.setImage(resourceManager.createImage(ResourceLoader.getIconDescriptor(START)));
		playPalseButton.setLayoutData(new GridData());
		Scale scale = new Scale(controls, SWT.VERTICAL);
		scale.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
		scale.setMaximum(10);
		scale.setPageIncrement(1);
		Button addMediaCollectionEntryButton = new Button(controls, SWT.NONE);
		addMediaCollectionEntryButton.setImage(resourceManager.createImage(ResourceLoader.getIconDescriptor(ADD)));
		addMediaCollectionEntryButton.setLayoutData(new GridData());
		addMediaCollectionEntryButton.addListener(SWT.Selection,new Listener() {
			@Override
			public void handleEvent(Event event) {
				addMediaCollectionEntry();
			}
		});
		removeMediaCollectionEntryButton = new Button(controls, SWT.NONE);
		removeMediaCollectionEntryButton.addListener(SWT.Selection,new Listener() {
			@Override
			public void handleEvent(Event event) {
				removeMediaCollectionEntry();
			}
		});
		removeMediaCollectionEntryButton.setImage(resourceManager.createImage(ResourceLoader.getIconDescriptor(REMOVE)));
		removeMediaCollectionEntryButton.setLayoutData(new GridData());
		updateRemoveMediaCollectionEntryButtonState();
	}
	
	private void removeMediaCollectionEntry() {
		IStructuredSelection selection = (IStructuredSelection) mediaControllerTimebarEditor.getSelection();
		if (selection.isEmpty() || !(selection.getFirstElement() instanceof MediaCollectionEntryInterval))
			return;
		String id = ((MediaCollectionEntryInterval) selection.getFirstElement()).getMediaCollectionEntryId();
		try {
			OpenedProjectController.getInstance().getMediaCollectionController().removeMediaCollectionEntryById(id);
		} catch (ModelPersistenceException e) {
			logger.error("Unable to remove media collectin entry", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to remove media collectin entry.", e.getMessage());
		}
	}

	private void addMediaCollectionEntry() {
		List<PlayableMedia> mediaList = showMediaSelection();
		if (mediaList.isEmpty())
			return;
		try {
			OpenedProjectController.getInstance().getMediaCollectionController().createMediaCollectionEntryFromMediaList(mediaList, mediaCollectionId);
		} catch (Exception e) {
			logger.error("Unable to add new media entry.", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to add new media entry.", e.getMessage());
		}
	}

	private List<PlayableMedia> showMediaSelection() {
		List<PlayableMedia> selectedMediaList = new ArrayList<PlayableMedia>();
		MediaSelectorDialog mediaSelectorDialog = new MediaSelectorDialog(Display.getDefault().getActiveShell(), new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				PlayableMedia media = (PlayableMedia) element;
				String extension = ResourceLoader.getFileExtension(media.getId());
				return resourceManager.createImage(ResourceLoader.getIconFromProgram(extension));
			}
			@Override
			public String getText(Object element) {
				PlayableMedia media = (PlayableMedia) element;
				return media.getId();
			}
		}, mediaCollectionId);
		int status = mediaSelectorDialog.open();
		if (status == MediaSelectorDialog.OK) {
			for (Object result : mediaSelectorDialog.getResult()) {
				selectedMediaList.add((PlayableMedia) result);
			}
		}
		return selectedMediaList;
	}
	
	private static class MediaSelectorDialog extends ElementListSelectionDialog {
		public MediaSelectorDialog(Shell parent, ILabelProvider renderer, String mediaCollectionId) {
			super(parent, renderer);
			this.setTitle("Select media files to be added to the collection");
			this.setAllowDuplicates(false);
			this.setMultipleSelection(true);
			setupData(mediaCollectionId);
		}

		private void setupData(String mediaCollectionId) {
			try {
				List<PlayableMedia> mediaList = OpenedProjectController.getInstance().getMediaController().getPlayableMediaList();
				MediaCollection collection = OpenedProjectController.getInstance().getMediaCollectionController().findMediaCollectionById(mediaCollectionId);
				mediaList.remove(collection.getMediaItem());
				for(MediaCollectionEntry entry : collection.getMediaCollectionEntryList()) {
					mediaList.remove(entry.getMediaItem());
				}
				this.setElements(mediaList.toArray());
			} catch (ObjectNotfoundException e) {
				logger.error("Unable to setup data for media selection dialog.", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Unable to setup data for media selection dialog.", e.getMessage());
			} 
		}
	}
}
