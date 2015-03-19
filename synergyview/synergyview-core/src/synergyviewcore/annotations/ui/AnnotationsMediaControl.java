/**
 *  File: SubtitleMediaController.java
 *  Copyright (c) 2010
 *  phyo
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package synergyviewcore.annotations.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import synergyviewcore.Activator;
import synergyviewcore.annotations.model.Annotation;
import synergyviewcore.annotations.model.AnnotationSet;
import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.annotations.model.IntervalAnnotation;
import synergyviewcore.annotations.ui.events.CaptionChangeEvent;
import synergyviewcore.annotations.ui.events.ICaptionChangeListener;
import synergyviewcore.annotations.ui.events.MediaTimeChangeListener;
import synergyviewcore.annotations.ui.events.TimeAvailableEvent;
import synergyviewcore.annotations.ui.events.TimeAvailableListener;
import synergyviewcore.annotations.ui.events.TimeChangeEvent;
import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.attributes.model.AttributeNode;
import synergyviewcore.attributes.model.ProjectAttributeRootNode;
import synergyviewcore.attributes.ui.views.CodingExplorerViewPart;
import synergyviewcore.collections.model.CollectionMedia;
import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionNode;
import synergyviewcore.media.model.AbstractMedia;
import synergyviewcore.media.model.MediaNode;
import synergyviewcore.media.model.MediaRootNode;
import synergyviewcore.model.ModelPersistenceException;
import synergyviewcore.navigation.NavigatorLabelProvider;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.subjects.model.Subject;
import synergyviewcore.subjects.model.SubjectNode;
import synergyviewcore.timebar.render.TimeBarIntervalRenderer;
import synergyviewcore.util.DateTimeHelper;
import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.ITimeBarChangeListener;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarModelListener;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * @author phyot
 *
 */
public class AnnotationsMediaControl extends Composite implements IAnnotationMediaControl, ISelectionListener {
	private TimeBarViewer timeBarViewer;
	private TimeBarMarkerImpl marker;
	private Scale timeScaleSlider;
	private LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	
	private int zoom_times;
	private static final int STEP_MILLI = 500;
	private static final int MARKER_VISIABLE_PIXEL = 10;
	private static final int MIN_ANNOTATION_INTERVAL_MILLISEC = 500;
	private JaretDate mediaClipStartDate;
	private CollectionMediaClip collectionMediaClip;
	private List<CollectionMedia> collectionMediaList;
	private boolean isMarkerDragging = false;
	
	private Text annotationTextWidget;
	private AnnotationSetNode annotationSetNode;
	private AnnotationMediaTimeBarRowModel mediaRowModel;
	private boolean _isPlaying;
	private MediaClipIntervalImpl currentTimeListener;
	private List<MediaClipIntervalImpl> timeAvailableIntervalList = new ArrayList<MediaClipIntervalImpl>();
	private MediaTimeChangeListener mediaTimeChangeListener;
	private IntervalImpl addedAnnotationInterval = null;
	private SubjectRowModel addedSubjectRow;

	private Map<Object, String> captionChangeProviders = new HashMap<Object, String>();
	private ICaptionChangeListener captionChangeListener;
	
	private AnnotationTimeBarModel annotationTimeBarModel;
	private Text captionTextView;
	private IObservableMap _mediaMap;
	private ProjectAttributeRootNode projectAttributeRootNode;
	private DropTarget _target;
	private TimeBarIntervalRenderer timeBarIntervalRenderer;
	private ILog logger;
	private boolean initialised = false;

	private Button captionButton;
	private Button lockButton;
	private GridData captionTextCompositeGd;
	private GridData annotationTextCompositeGd;
	private PropertyChangeListener captionHideListener;
	private PropertyChangeListener lockAnnotationsListener;

	private AnnotationIntervalRender annotationIntervalRender;
	private Button textLabelButton;
	private Button barLabelButton;
	private ISelectionService selectionService;
	private Button attributesSelectionLinkButton;
	
	/**
	 * @param parent
	 * @param style
	 */
	public AnnotationsMediaControl(Composite parent, int style, AnnotationSetNode annotationSetNodeValue, IObservableMap mediaMap, MediaRootNode mediaFolder) {
		super(parent, style);

		logger = Activator.getDefault().getLog(); 

		this.setLayout(new GridLayout(1, false));
		annotationSetNode = annotationSetNodeValue;
		projectAttributeRootNode = ((ProjectNode) annotationSetNode.getLastParent()).getProjectAttributeRootNode();
		collectionMediaList = ((CollectionNode) annotationSetNode.getParent().getParent()).getResource().getCollectionMediaList();
		collectionMediaClip = annotationSetNode.getResource().getCollectionMediaClip();
		this._mediaMap = mediaMap;
		for(CollectionMedia cMedia : collectionMediaList) {
			String mediaName = cMedia.getMediaName();
			MediaNode mediaNode = mediaFolder.getMediaNode(mediaName);
			_mediaMap.put(cMedia, mediaNode.createMediaInstance());
		}
		initUI();
		initData();
		setupDND();
	}

	private void initUI() {

		Composite captionTextComposite = new Composite(this, SWT.NONE);
		captionTextCompositeGd = new GridData();
		captionTextCompositeGd.horizontalAlignment = SWT.FILL;
		captionTextCompositeGd.grabExcessHorizontalSpace = true;
		captionTextCompositeGd.heightHint =  40;
		captionTextComposite.setLayoutData(captionTextCompositeGd);
		captionTextComposite.setLayout(new GridLayout(1,false));
		captionTextView = new Text(captionTextComposite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CENTER);
		captionTextView.setEditable(false);
		captionTextView.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		captionTextView.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		captionTextView.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));

		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		Composite mediaControls = new Composite(this,SWT.NONE);
		mediaControls.setLayoutData(gd);
		mediaControls.setLayout(new GridLayout(6,false));

		textLabelButton = new Button(mediaControls, SWT.TOGGLE);
		textLabelButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("text.gif")));
		textLabelButton.setLayoutData(new GridData());
		
		barLabelButton = new Button(mediaControls, SWT.TOGGLE);
		barLabelButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("grouping.gif")));
		barLabelButton.setLayoutData(new GridData());
		
		lockButton = new Button(mediaControls, SWT.TOGGLE);
		lockButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("lock_open.png")));
		lockButton.setLayoutData(new GridData());

		captionButton = new Button(mediaControls, SWT.TOGGLE);
		captionButton.setLayoutData(new GridData());
		
		attributesSelectionLinkButton = new Button(mediaControls, SWT.TOGGLE);
		attributesSelectionLinkButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("layout_link.png")));
		attributesSelectionLinkButton.setLayoutData(new GridData());

		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		timeScaleSlider = new Scale(mediaControls, SWT.NONE);
		timeScaleSlider.setLayoutData(gd);

		timeBarViewer = new TimeBarViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		timeBarViewer.setLayoutData(gd);
		timeBarViewer.setDrawRowGrid(true);
		timeBarViewer.setRowHeight(30);
		
		Composite annotationTextComposite = new Composite(this, SWT.NONE);
		annotationTextCompositeGd = new GridData();
		annotationTextCompositeGd.horizontalAlignment = SWT.FILL;
		annotationTextCompositeGd.grabExcessHorizontalSpace = true;
		annotationTextCompositeGd.heightHint = 50;
		annotationTextComposite.setLayoutData(annotationTextCompositeGd);
		annotationTextComposite.setLayout(new GridLayout(2, false));
		Label annotationTextLabel = new Label(annotationTextComposite, SWT.NONE);
		annotationTextLabel.setLayoutData(new GridData());
		annotationTextLabel.setText("Annotation Text:");
		annotationTextWidget = new Text(annotationTextComposite, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		annotationTextWidget.setTextLimit(Short.MAX_VALUE);
		//
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.heightHint = 40;
		gd.grabExcessHorizontalSpace = true;
		annotationTextWidget.setLayoutData(gd);

		this.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});

		lockButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				annotationSetNode.setLock(lockButton.getSelection());
			}
		});
		
		attributesSelectionLinkButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				annotationIntervalRender.setShowAttributeSelection(attributesSelectionLinkButton.getSelection());
				timeBarViewer.repaint();
			}
		});

		captionButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {				
				annotationSetNode.hideCaption(captionButton.getSelection());
			}
		});
		
		textLabelButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {				
				annotationIntervalRender.setShowText(textLabelButton.getSelection());
				timeBarViewer.redraw();
			}
		});
		
		barLabelButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {				
				annotationIntervalRender.setShowBorder(barLabelButton.getSelection());
				timeBarViewer.redraw();
			}
		});

		//Updates UI according to the data properties
		updateCaptionComposite(annotationSetNode.getResource().isHideCaption());
		updateAnnotationTextComposite(annotationSetNode.getResource().isLock());
	}

	private void updateAnnotationTextComposite(boolean isLock) {
		annotationTextCompositeGd.heightHint = isLock ? 0 : 50;
		String iconName = isLock ? "lock.png" : "lock_open.png";
		lockButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor(iconName)));
		lockButton.setSelection(isLock);
		layout();	
	}

	private void updateCaptionComposite(boolean hideCaption) {
		captionTextCompositeGd.heightHint = hideCaption ? 0 : 40;
		String iconName = hideCaption ? "comments_delete.png" : "comments.png";
		captionButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor(iconName)));
		captionButton.setSelection(hideCaption);
		layout();	
	}

	public void showSubjectsDialogToAdd() {
		List<Subject> subjectsToAdd = new ArrayList<Subject>();
		try {

			//TODO This can be moved to AnnotationSetNode
			ElementListSelectionDialog dialog = new  ElementListSelectionDialog(AnnotationsMediaControl.this.getShell(), new NavigatorLabelProvider());
			dialog.setTitle("Select Subjects to add");
			dialog.setMultipleSelection(true);
			ProjectNode projectNode = (ProjectNode) projectAttributeRootNode.getLastParent();

			List<INode> nodesToShow = new ArrayList<INode>();
			//Removing existing subjects from the list
			List<Subject> existingSubjects = annotationSetNode.getSubjectList();
			for (INode node : projectNode.getSubjectRootNode().getChildren()) {
				if (!existingSubjects.contains(((SubjectNode)node).getResource()))
					nodesToShow.add(node);
			}
			dialog.setElements(nodesToShow.toArray(new INode[]{}));
			dialog.open();
			if (dialog.getResult() != null) {
				for (Object result : dialog.getResult()) {
					SubjectNode node = (SubjectNode) result;
					subjectsToAdd.add(node.getResource());
				}

				//Ask the controller to add the subjects
				annotationSetNode.addSubjects(subjectsToAdd);
			}

		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(),ex);
			logger.log(status); 
		}
	}

	/**
	 * This is called when the object is about to dispose
	 * 
	 */
	public void dispose() {
		resourceManager.dispose();
		timeBarViewer.getSelectionModel().clearSelection();
		timeBarViewer.fireSelectionChanged();
		_target.dispose();

		if (timeBarIntervalRenderer!=null)
			timeBarIntervalRenderer.disposeResource();

		annotationSetNode.getResource().removePropertyChangeListener(AnnotationSet.PROP_HIDECAPTION, captionHideListener);
		annotationSetNode.getResource().removePropertyChangeListener(AnnotationSet.PROP_LOCK, lockAnnotationsListener);
		if (this.selectionService!=null) {
			this.selectionService.removeSelectionListener(this);
			this.selectionService.removeSelectionListener(this.annotationIntervalRender);
		}
	}



	public TimeBarViewer getTimeBarViewer() {
		return timeBarViewer;
	}

	private void updateScale() {
		timeBarViewer.setPixelPerSecond((double)((((Composite) timeBarViewer).getClientArea().width - timeBarViewer.getYAxisWidth() - 3) * 1000)/(double)(collectionMediaClip.getDuration()));
		timeScaleSlider.setMaximum((int) (timeBarViewer.getPixelPerSecond() * 1000 * zoom_times));
		timeScaleSlider.setMinimum((int) (timeBarViewer.getPixelPerSecond() * 1000));
		timeScaleSlider.setSelection((int) (timeBarViewer.getPixelPerSecond() * 1000));
	}

	private void initData() {
		mediaClipStartDate = new JaretDate().setTime(0, 0, 0, collectionMediaClip.getStartOffset());
		marker = new TimeBarMarkerImpl(true, mediaClipStartDate.copy());
		
		
		//Creates a new AnnotationTimeBarModel
		annotationTimeBarModel = new AnnotationTimeBarModel(annotationSetNode, marker);		
		
		timeBarViewer.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
		timeBarViewer.setMilliAccuracy(true);
		annotationIntervalRender = new AnnotationIntervalRender();
		
		timeBarViewer.registerTimeBarRenderer(AnnotationIntervalImpl.class, annotationIntervalRender);
		timeBarViewer.setMinDate(mediaClipStartDate.copy());
		timeBarViewer.setMaxDate(mediaClipStartDate.copy().advanceMillis((long)collectionMediaClip.getDuration()));
		timeBarViewer.setOptimizeScrolling(true);
		timeBarViewer.addControlListener(new ControlListener() {


			public void controlMoved(ControlEvent e) {
				//	
			}

			public void controlResized(ControlEvent e) {
				if (collectionMediaClip!=null) {
					if (!initialised) {
						updateScale();
						initialised = true;
					}
				}
			}
		});


		zoom_times = (collectionMediaClip.getDuration() / 1000) / 10;
		if (zoom_times < 4)
			zoom_times = 4;

		timeScaleSlider.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// 
			}

			public void widgetSelected(SelectionEvent e) {

				if (timeScaleSlider.getSelection() == timeScaleSlider.getMinimum()) {
					timeBarViewer.setPixelPerSecond((double)((((Composite) timeBarViewer).getClientArea().width - timeBarViewer.getYAxisWidth() - 3) * 1000)/(double)(collectionMediaClip.getDuration()));
				} else {
					timeBarViewer.setPixelPerSecond((double) timeScaleSlider.getSelection() / 1000);
				}
				timeBarViewer.scrollDateToVisible(marker.getDate().copy().advanceMillis((long) ((MARKER_VISIABLE_PIXEL * 1000) / timeBarViewer.getPixelPerSecond())));
			}
		});

		mediaTimeChangeListener = new MediaTimeChangeListener() {

			public void playBackChanged(final TimeChangeEvent eventArg) {

				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						if (timeBarViewer.isDisposed()) 
							return;
						if (!isMarkerDragging) 
							marker.setDate(eventArg.getCurrentTime());
					}
				});
			}

		};

		captionChangeListener = new ICaptionChangeListener() {

			public void captionChange(Object source, CaptionChangeEvent eventArg) {
				if (!annotationSetNode.getResource().isHideCaption()) {
					if (eventArg.getCaptionState() == ICaptionChangeListener.CaptionPublishState.SET && !captionChangeProviders.containsKey(source)) {
						captionChangeProviders.put(source, eventArg.getCaptionText());
					}
					if (eventArg.getCaptionState() == CaptionPublishState.UNSET && captionChangeProviders.containsKey(source)) {
						captionChangeProviders.remove(source);
					}

					StringBuilder captionBuilder = new StringBuilder();
					for (Entry<Object, String> set : captionChangeProviders.entrySet()) {
						AnnotationIntervalImpl cI = (AnnotationIntervalImpl) set.getKey();
						captionBuilder.append("[");
						captionBuilder.append(cI.getOwner().getSubject().getName());
						captionBuilder.append(": ");
						captionBuilder.append(set.getValue());
						captionBuilder.append("] ");
					}
					captionTextView.setText(captionBuilder.toString());
				}
			}
		};

		captionHideListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				AnnotationsMediaControl.this.updateCaptionComposite(annotationSetNode.getResource().isHideCaption());
				if (annotationSetNode.getResource().isHideCaption()) {
					AnnotationsMediaControl.this.captionChangeProviders.clear();
					captionTextView.setText("");
				}
			}
		};

		lockAnnotationsListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				AnnotationsMediaControl.this.updateAnnotationTextComposite(annotationSetNode.getResource().isLock());
				if (annotationSetNode.getResource().isLock() && AnnotationsMediaControl.this.addedAnnotationInterval!=null)
					AnnotationsMediaControl.this.stopCaptionMark();
			}
		};

		annotationSetNode.getResource().addPropertyChangeListener(AnnotationSet.PROP_HIDECAPTION, captionHideListener);
		annotationSetNode.getResource().addPropertyChangeListener(AnnotationSet.PROP_LOCK, lockAnnotationsListener);

		

		registerDraggingListener();

		
		mediaRowModel = new AnnotationMediaTimeBarRowModel(collectionMediaList);
		initMediaRow();
		annotationTimeBarModel.addRow(0, mediaRowModel);
		timeBarViewer.setModel(annotationTimeBarModel);
		timeBarViewer.addMarker(marker);

		timeBarViewer.setLineDraggingAllowed(false);
		timeBarIntervalRenderer = new TimeBarIntervalRenderer();
		timeBarViewer.registerTimeBarRenderer(MediaClipIntervalImpl.class, timeBarIntervalRenderer);
		registerIntervalModifier();
		registerMediaPropertyChangeListener();
		//updateScale();
		

		registerCaptionListeners();
	}

	/**
	 * 
	 */
	private void registerCaptionListeners() {
		for(int i=0;i<annotationTimeBarModel.getRowCount();i++) {
			for(Interval interval : annotationTimeBarModel.getRow(i).getIntervals()) {
				if (interval instanceof AnnotationIntervalImpl) {
					AnnotationIntervalImpl addedAnnotationIntervalImpl = (AnnotationIntervalImpl) interval;
					addedAnnotationIntervalImpl.addCaptionChangeListener(captionChangeListener);
				}
			}
		}
		annotationTimeBarModel.addTimeBarModelListener(new TimeBarModelListener() {

			public void elementAdded(TimeBarModel arg0, TimeBarRow arg1,
					Interval arg2) {
				if (arg2 instanceof AnnotationIntervalImpl) {
					AnnotationIntervalImpl addedAnnotationIntervalImpl = (AnnotationIntervalImpl) arg2;
					addedAnnotationIntervalImpl.addCaptionChangeListener(captionChangeListener);
				}
			}

			public void elementRemoved(TimeBarModel arg0, TimeBarRow arg1,
					Interval arg2) {
				if (arg2 instanceof AnnotationIntervalImpl) {
					AnnotationIntervalImpl removedAnnotationIntervalImpl = (AnnotationIntervalImpl) arg2;
					removedAnnotationIntervalImpl.removeCaptionChangeListener(captionChangeListener);
				}
			}
			public void elementChanged(TimeBarModel arg0, TimeBarRow arg1,
					Interval arg2) {}
			public void headerChanged(TimeBarModel arg0, TimeBarRow arg1,
					Object arg2) {}
			public void modelDataChanged(TimeBarModel arg0) {}
			public void rowAdded(TimeBarModel arg0, TimeBarRow arg1) {}
			public void rowDataChanged(TimeBarModel arg0, TimeBarRow arg1) {}
			public void rowRemoved(TimeBarModel arg0, TimeBarRow arg1) {}

		});
	}

	private void addAnnotation() {
		IntervalAnnotation annotationItem = new IntervalAnnotation();
		if (addedAnnotationInterval.getEnd().diffMilliSeconds(addedAnnotationInterval.getBegin())<MIN_ANNOTATION_INTERVAL_MILLISEC)
			addedAnnotationInterval.setEnd(addedAnnotationInterval.getBegin().copy().advanceMillis(MIN_ANNOTATION_INTERVAL_MILLISEC));
		annotationItem.setId(UUID.randomUUID().toString());
		annotationItem.setHr(addedAnnotationInterval.getBegin().getHours());
		annotationItem.setMi(addedAnnotationInterval.getBegin().getMinutes());
		annotationItem.setSec(addedAnnotationInterval.getBegin().getSeconds());
		annotationItem.setMilliSec(addedAnnotationInterval.getBegin().getMillis());
		annotationItem.setStartTime(DateTimeHelper.getMilliFromJaretDate(addedAnnotationInterval.getBegin()));
		annotationItem.setDuration(addedAnnotationInterval.getEnd().diffMilliSeconds(addedAnnotationInterval.getBegin()));
		annotationItem.setText(annotationTextWidget.getText());
		annotationItem.setSubject(addedSubjectRow.getSubject());
		annotationItem.setAnnotationSet(annotationSetNode.getResource());
		List<Annotation> annotationsToAdd = new ArrayList<Annotation>();
		annotationsToAdd.add(annotationItem);
		try {
			annotationSetNode.addAnnotations(annotationsToAdd, addedSubjectRow.getSubject());
			annotationTextWidget.setText("");
			annotationTextWidget.setFocus();
		} catch (Exception ex) {
			//
		}
	}

	private void registerMediaPropertyChangeListener() {
		//
	}

	private void registerDraggingListener() {
		marker.addTimeBarMarkerListener(new TimeBarMarkerListener() {
			public void markerDescriptionChanged(TimeBarMarker arg0,
					String arg1, String arg2) {
				//
			}

			public void markerMoved(TimeBarMarker arg0, JaretDate arg1,
					JaretDate arg2) {
				if (isMarkerDragging) {
					for (Interval interval : mediaRowModel.getIntervals(marker.getDate())) {
						MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
						mediaInterval.updateMediaTime();

					}

					// Stops the marker from moving beyond the media clips
					if (marker.getDate().diffMilliSeconds(mediaClipStartDate) < 0) {
						marker.setDate(mediaClipStartDate);
					} 
					if (marker.getDate().diffMilliSeconds(mediaClipStartDate.copy().advanceMillis((long)collectionMediaClip.getDuration())) > 0) {
						marker.setDate(mediaClipStartDate.copy().advanceMillis((long)collectionMediaClip.getDuration()));
					}
				} 

				//Make the marker visible when moved
				timeBarViewer.scrollDateToVisible(marker.getDate().copy().advanceMillis((long) ((MARKER_VISIABLE_PIXEL * 1000) / timeBarViewer.getPixelPerSecond())));

				if (addedAnnotationInterval!=null && arg2.diffMilliSeconds(addedAnnotationInterval.getBegin().copy().advanceMillis(MIN_ANNOTATION_INTERVAL_MILLISEC))>0) {
					addedAnnotationInterval.setEnd(arg2.copy());
				}
			}			
		});

		timeBarViewer.addTimeBarChangeListener(new ITimeBarChangeListener() {

			public void intervalChangeCancelled(TimeBarRow row, Interval interval) {}
			public void intervalChangeStarted(TimeBarRow row, Interval interval) {}
			public void intervalChanged(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {
				//Save changes to the annotation after user update
				if (interval instanceof AnnotationIntervalImpl) {
					try {
						annotationSetNode.updateAnnotation(((AnnotationIntervalImpl) interval).getAnnotation());
					} catch (ModelPersistenceException e) {
						IStatus status = new Status(IStatus.WARNING,Activator.PLUGIN_ID,e.getMessage(), e);
						logger.log(status);
						MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error updating Annotation", e.getMessage());
					}
				}
			}
			
			public void intervalIntermediateChange(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {
				if (interval instanceof AnnotationIntervalImpl) {
					AnnotationIntervalImpl changedInterval = (AnnotationIntervalImpl) interval;
					if (changedInterval.getBegin().diffMilliSeconds(mediaClipStartDate) < 0) {
						changedInterval.setBegin(mediaClipStartDate);
					} 
					if (changedInterval.getEnd().diffMilliSeconds(mediaClipStartDate.copy().advanceMillis((long)collectionMediaClip.getDuration())) > 0) {
						changedInterval.setEnd(mediaClipStartDate.copy().advanceMillis((long)collectionMediaClip.getDuration()));
					}
					long duration = changedInterval.getEnd().diffMilliSeconds(interval.getBegin());
					if (duration < MIN_ANNOTATION_INTERVAL_MILLISEC) {
						changedInterval.setEnd(changedInterval.getBegin().copy().advanceMillis(MIN_ANNOTATION_INTERVAL_MILLISEC));
					}
				}
			}
			public void markerDragStarted(TimeBarMarker marker) {
				isMarkerDragging = true;
			}

			public void markerDragStopped(TimeBarMarker marker) {
				isMarkerDragging = false;
				annotationTextWidget.setFocus();
			}
		});
	}

	private void registerIntervalModifier(){
		timeBarViewer.addIntervalModificator(new DefaultIntervalModificator(){

			@Override
			public boolean isShiftingAllowed(TimeBarRow row, Interval interval) {
				return (row == mediaRowModel || annotationSetNode.getResource().isLock()) ? false : true;
			}

			@Override
			public boolean isSizingAllowed(TimeBarRow row, Interval interval) {
				if (row == mediaRowModel || annotationSetNode.getResource().isLock())  
					return false;
				if (interval instanceof AnnotationIntervalImpl) {
					long duration = interval.getEnd().diffMilliSeconds(interval.getBegin());
					if (duration >= MIN_ANNOTATION_INTERVAL_MILLISEC)
						return true;
					else return false;
				}
				return false;
			}

			@Override
			public boolean newBeginAllowed(TimeBarRow row, Interval interval,
					JaretDate newBegin) {
				return (row == mediaRowModel || annotationSetNode.getResource().isLock()) ? false : true;
			}

			@Override
			public boolean newEndAllowed(TimeBarRow row, Interval interval,
					JaretDate newEnd) {
				return (row == mediaRowModel || annotationSetNode.getResource().isLock()) ? false : true;
			}

			@Override
			public boolean shiftAllowed(TimeBarRow row, Interval interval,
					JaretDate newBegin) {
				return (row == mediaRowModel || annotationSetNode.getResource().isLock()) ? false : true;
			}			
		});
	}

	private void initMediaRow() {
		for (CollectionMedia collectionMedia : collectionMediaList) {
			MediaClipIntervalImpl interval = new MediaClipIntervalImpl(collectionMedia, ((AbstractMedia)_mediaMap.get(collectionMedia)), mediaClipStartDate, collectionMediaClip.getDuration(), marker, this);
			interval.addTimeAvailableListener(new TimeAvailableListener() {

				public void timeAvailableChanged(TimeAvailableEvent eventArg) {
					if (eventArg.getTimeAvailable() && !timeAvailableIntervalList.contains(eventArg.getSource()))
						timeAvailableIntervalList.add((MediaClipIntervalImpl) eventArg.getSource());
					if (!eventArg.getTimeAvailable() && timeAvailableIntervalList.contains(eventArg.getSource()))
						timeAvailableIntervalList.remove(eventArg.getSource());
					updateTimeListener();
					if (AnnotationsMediaControl.this.isPlaying() && timeAvailableIntervalList.size()==0)
						AnnotationsMediaControl.this.setPlaying(false);
				}
			});
			if (interval.isTimeAvailable())
			{
				timeAvailableIntervalList.add(interval);
				// Add the first interval as the current one
				if (currentTimeListener == null) {
					currentTimeListener = interval;
					currentTimeListener.addTimeChangeListener(mediaTimeChangeListener);
				}
			}
			mediaRowModel.addInterval(interval);
		}
	}

	private void updateTimeListener() {
		if (currentTimeListener==null) {
			if (timeAvailableIntervalList.size()>0) {
				currentTimeListener = timeAvailableIntervalList.get(0);
				currentTimeListener.addTimeChangeListener(mediaTimeChangeListener);
			}
		} else {
			if (!timeAvailableIntervalList.contains(currentTimeListener)) {
				currentTimeListener.removeTimeChangeListener(mediaTimeChangeListener);
				if (timeAvailableIntervalList.size()>0) {
					currentTimeListener = timeAvailableIntervalList.get(0);
					currentTimeListener.addTimeChangeListener(mediaTimeChangeListener);
				} else currentTimeListener = null;
			}
		}

	}




	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#isPlaying()
	 */
	public boolean isPlaying() {
		return _isPlaying;
	}


	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#setPlaying(boolean)
	 */
	public void setPlaying(boolean playValue) {

		for (Interval interval : mediaRowModel.getIntervals()) {
			MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
			if (playValue) 
				mediaInterval.play();
			else mediaInterval.stop();
		}
		_isPlaying = playValue;

	}

	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#setRowSelection(int)
	 */
	public void setRowSelection(int number) {
		if (number < annotationTimeBarModel.getRowCount()) {
			timeBarViewer.getSelectionModel().setSelectedRow(annotationTimeBarModel.getRow(number));
			annotationTextWidget.setFocus();
		}
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#startCaptionMark()
	 */
	public void startCaptionMark() {
		if (annotationSetNode.getResource().isLock()) {
			MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_ERROR);
			messageBox.setText("Locked!");
			messageBox.setMessage("Please ensure the annotation set is unlocked.");
			messageBox.open();
			return;
		}
		if (!timeBarViewer.getSelectionModel().isEmpty() && annotationTextWidget.getText().compareTo("")!=0  && addedAnnotationInterval==null) {
			addedSubjectRow = (SubjectRowModel) timeBarViewer.getSelectionModel().getSelectedRows().get(0);
			addedAnnotationInterval = new IntervalImpl();
			JaretDate d = marker.getDate().copy();
			addedAnnotationInterval.setBegin(d);
			addedAnnotationInterval.setEnd(d.advanceMillis(MIN_ANNOTATION_INTERVAL_MILLISEC));
			addedSubjectRow.addInterval(addedAnnotationInterval);
			timeBarViewer.setFocussedInterval(addedSubjectRow, addedAnnotationInterval);
			if (!_isPlaying)
				this.setPlaying(true);
		} else {
			MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_INFORMATION);
			messageBox.setText("Error adding a new entry!");
			messageBox.setMessage("Please ensure a subject is selected, the text is not empty.");
			messageBox.open();
		}
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#stepFF()
	 */
	public void stepForward() {
		for (Interval interval : mediaRowModel.getIntervals()) {
			MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
			mediaInterval.stepFF(STEP_MILLI);
		}
	}


	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#stepRE()
	 */
	public void stepRewind() {
		for (Interval interval : mediaRowModel.getIntervals()) {
			MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
			mediaInterval.stepRE(STEP_MILLI);
		}
	}


	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#stopCaptionMark()
	 */
	public void stopCaptionMark() {
		if (addedAnnotationInterval!=null) {
			if (!annotationSetNode.getResource().isLock())
				addAnnotation();
			addedSubjectRow.remInterval(addedAnnotationInterval);
			addedAnnotationInterval = null;
			addedSubjectRow = null;
			if (_isPlaying)
				this.setPlaying(false);
		}

	}





	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#isMute(synergyviewcore.subtitle.ui.MediaClipIntervalImpl)
	 */
	public boolean isMute(MediaClipIntervalImpl mediaClipIntervalImpl) {
		return mediaClipIntervalImpl.isMute();
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.subtitle.ui.ISubtitleControl#setMute(synergyviewcore.subtitle.ui.MediaClipIntervalImpl, boolean)
	 */
	public void setMute(MediaClipIntervalImpl mediaClipIntervalImpl,
			boolean muteValue) {
		mediaClipIntervalImpl.setMute(muteValue);
	}


	private void setupDND() {

		// ////////////////////
		// Drop target

		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		_target = new DropTarget(timeBarViewer, operations);

		// Receive data in Text
		final TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] types = new Transfer[] {textTransfer, LocalSelectionTransfer.getTransfer()};
		_target.setTransfer(types);

		_target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
			}

			public void dragOver(DropTargetEvent event) {
				int Y = Display.getCurrent().map(null, timeBarViewer, event.x, event.y).y;
				int X = Display.getCurrent().map(null, timeBarViewer, event.x, event.y).x;
				List<Interval> il = timeBarViewer.getIntervalsAt(X, Y);
				if (il.size()>0) {
					if (il.get(0) instanceof AnnotationIntervalImpl) {
						event.detail = DND.DROP_COPY;
						timeBarViewer.setFocussedInterval(il.get(0));
					}
				}
			}

			public void dragOperationChanged(DropTargetEvent event) {
			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dropAccept(DropTargetEvent event) {
			}


			public void drop(DropTargetEvent event) {
				int Y = Display.getCurrent().map(null, timeBarViewer, event.x, event.y).y;
				int X = Display.getCurrent().map(null, timeBarViewer, event.x, event.y).x;
				List<Interval> il = timeBarViewer.getIntervalsAt(X, Y);
				if (il.size()>0) {
					if (il.get(0) instanceof AnnotationIntervalImpl) {
						if (event.data instanceof TreeSelection) {
							TreeSelection selection = (TreeSelection) event.data;
							if (!selection.isEmpty()) {
								@SuppressWarnings("rawtypes")
								Iterator i = selection.iterator();
								List<Attribute> list = new ArrayList<Attribute>();
								while (i.hasNext()) {
									Object o = i.next();
									if (o instanceof AttributeNode) {
										AttributeNode attributeNode = (AttributeNode) o;
										if (attributeNode.getChildren().isEmpty()) {
											if (!list.contains(attributeNode.getResource()))
												list.add(attributeNode.getResource());
										}
										annotationSetNode.getAnnotationAttributeController(((AnnotationIntervalImpl) il.get(0)).getAnnotation()).addAttributeList(list);
										timeBarViewer.setFocussedInterval(il.get(0));

										timeBarViewer.getSelectionModel().setSelectedInterval(il.get(0));
										timeBarViewer.fireSelectionChanged();
									}
								}
							}
						}
					}
				}
			}
		});

	}


	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@SuppressWarnings("rawtypes")
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		
		if (attributesSelectionLinkButton.getSelection() && part instanceof CodingExplorerViewPart) {
			this.timeBarViewer.repaint();
		} else {
		
			IStructuredSelection structSel = (IStructuredSelection) selection;
			Iterator iterator = structSel.iterator();
			Map<Annotation, AnnotationIntervalImpl> selected = new HashMap<Annotation, AnnotationIntervalImpl>();
			while (iterator.hasNext()) {
				Object obj = iterator.next();
				if (!(obj instanceof Annotation))
					return;
				for(int i = 0; i < annotationTimeBarModel.getRowCount(); i++) {
					if (annotationTimeBarModel.getRow(i) instanceof SubjectRowModel) {
						AnnotationIntervalImpl impl = ((SubjectRowModel) annotationTimeBarModel.getRow(i)).getAnnotationNode((Annotation) obj);
						if (impl != null)
							selected.put((Annotation) obj, impl);
					}
				}
				
			}
			if (selected.entrySet().size()==1) {
				timeBarViewer.getSelectionModel().setSelectedInterval(selected.entrySet().iterator().next().getValue());
				timeBarViewer.scrollIntervalToVisible(timeBarViewer.getSelectionModel().getSelectedIntervals().get(0));
			} else if (selected.entrySet().size()>1) {
				for (Map.Entry<Annotation, AnnotationIntervalImpl> entry : selected.entrySet()) {
					timeBarViewer.getSelectionModel().addSelectedInterval(entry.getValue());
				}
			} 
		}
		
			
	}	

	/**
	 * @param selectionService
	 */
	public void setSelectionService(ISelectionService selectionService) {
		this.selectionService = selectionService;
		this.selectionService.addSelectionListener(this);
		this.selectionService.addSelectionListener(this.annotationIntervalRender);
	}
}

