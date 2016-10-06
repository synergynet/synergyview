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

package synergyviewmvc.annotations.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import synergyviewmvc.annotations.model.Annotation;
import synergyviewmvc.annotations.model.AnnotationSetNode;
import synergyviewmvc.annotations.model.IntervalAnnotation;
import synergyviewmvc.annotations.ui.events.AnnotationChangeEvent;
import synergyviewmvc.annotations.ui.events.MarkerInRangeEvent;
import synergyviewmvc.annotations.ui.events.MarkerInRangeListener;
import synergyviewmvc.annotations.ui.events.MediaTimeChangeListener;
import synergyviewmvc.annotations.ui.events.TimeChangeEvent;
import synergyviewmvc.attributes.model.Attribute;
import synergyviewmvc.attributes.model.AttributeNode;
import synergyviewmvc.collections.model.CollectionMedia;
import synergyviewmvc.collections.model.CollectionMediaClip;
import synergyviewmvc.collections.model.CollectionNode;
import synergyviewmvc.media.model.AbstractMedia;
import synergyviewmvc.media.model.MediaNode;
import synergyviewmvc.media.model.MediaRootNode;
import synergyviewmvc.navigation.NavigatorLabelProvider;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.subjects.model.Subject;
import synergyviewmvc.subjects.model.SubjectNode;
import synergyviewmvc.timebar.render.TimeBarIntervalRenderer;
import uk.ac.durham.tel.commons.jface.node.INode;
import de.jaret.util.date.Interval;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerImpl;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;
import de.jaret.util.ui.timebars.mod.DefaultIntervalModificator;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.ITimeBarChangeListener;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarModelListener;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * @author phyo
 *
 */
public class AnnotationsMediaControl extends Composite implements IAnnotationMediaControl, IAnnotationListener {
	
	private static final String NEWSUBJECT_ICON_IMAGE = "note_add.png";
	
	private TimeBarViewer timeBarViewer;
	private TimeBarMarkerImpl timebarMarker;
	private Scale timeScale;
	
	//TODO ZOOM should be based on the total length of the clip
	private static final int SCALE_ZOOM_TIMES = 20;
	private static final int STEP_MILLI = 500;
	private JaretDate mediaStartDateTime;
	private CollectionMediaClip collectionMediaClip;
	private List<CollectionMedia> collectionMediaList;
	private boolean isMarkerDragging = false;
	private DefaultTimeBarModel timebarModel;
	private Text captionText;
	private AnnotationSetNode annotationSetNode;
	private DefaultTimeBarRowModel timebarMediaRow;
	private boolean isPlaying;
	private MediaClipIntervalImpl currentTimeProvider;
	private List<MediaClipIntervalImpl> mediaClipsInMarkerRangeList = new ArrayList<MediaClipIntervalImpl>();
	private MediaTimeChangeListener mediaTimeChangeListener;
	private IntervalImpl _addedCaptionInterval = null;
	private SubjectRowModel intervalAddedRow;
	private List<ICaptionChangeListener> captionChangeListeners = new ArrayList<ICaptionChangeListener>();
	private Map<Object, String> captions = new HashMap<Object, String>();
	private ICaptionChangeListener captionChangeListener;
	private Text captionTextView;
	private IObservableMap mediaMap;
	private DropTarget dndTarget;
	private TimeBarIntervalRenderer timeBarIntervalRenderer;
	private Image addSubjectImage;
	/**
	 * @param parent
	 * @param style
	 */
	public AnnotationsMediaControl(Composite parent, int style, AnnotationSetNode annotationSetNodeValue, IObservableMap mediaMap) {
		super(parent, style);

		this.setLayout(new GridLayout(1, false));
		annotationSetNode = annotationSetNodeValue;
		collectionMediaList = ((CollectionNode) annotationSetNode.getParent().getParent()).getResource().getCollectionMediaList();
		collectionMediaClip = annotationSetNode.getResource().getCollectionMediaClip();
		this.mediaMap = mediaMap;
		for(CollectionMedia collectionMedia : collectionMediaList) {
			String mediaName = collectionMedia.getMediaName();
			MediaNode mediaNode = annotationSetNodeValue.getProjectPathProvider().getMediaRootNode().getMediaNode(mediaName);
			mediaMap.put(collectionMedia, mediaNode.createMediaInstance());
		}
		initUI();
		initData();
		setupDND();
	}

	private void initUI() {
		
		// Caption Text Box
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.heightHint = 30;
		captionTextView = new Text(this, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL | SWT.CENTER);
		captionTextView.setEditable(false);
		captionTextView.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		captionTextView.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		captionTextView.setLayoutData(gd);

		// Commands composite 
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		Composite annotationMediaControl = new Composite(this,SWT.NONE);
		annotationMediaControl.setLayout(new GridLayout(2,false));
		annotationMediaControl.setLayoutData(gd);
		
		// Add subject rows button
		final Button newSubjectRowButton = new Button(annotationMediaControl, SWT.NONE); 
		gd = new GridData();
		addSubjectImage = ResourceLoader.getIconDescriptor(NEWSUBJECT_ICON_IMAGE).createImage();
		newSubjectRowButton.setImage(addSubjectImage);
		newSubjectRowButton.setLayoutData(gd);
		newSubjectRowButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) { }
			public void widgetSelected(SelectionEvent e) {
				showAddSubjectsDialog();
			}
		});
		
		// Time slider for zooming in and out
		timeScale = new Scale(annotationMediaControl, SWT.NONE);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		timeScale.setLayoutData(gd);

		// Timebar viewer
		timeBarViewer = new TimeBarViewer(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		timeBarViewer.setLayoutData(gd);
		timeBarViewer.setDrawRowGrid(true);

		//Caption Text box for annotation intervals
		captionText = new Text(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.heightHint = 30;
		gd.grabExcessHorizontalSpace = true;
		captionText.setLayoutData(gd);

		
		//Register this composite for disposal
		this.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposeResources();
			}
		});

	}
	
	private void showAddSubjectsDialog() {
		List<Subject> subjectsToAdd = new ArrayList<Subject>();
		try {
			ElementListSelectionDialog dialog = new  ElementListSelectionDialog(AnnotationsMediaControl.this.getShell(), new NavigatorLabelProvider());
			dialog.setMultipleSelection(true);

			List<INode> nodesToShow = new ArrayList<INode>();
			//Removing existing subjects from the list
			List<Subject> existingSubjects = annotationSetNode.getResource().getSubjectList();
			
			dialog.setElements(nodesToShow.toArray(new INode[]{}));
			dialog.open();
			if (dialog.getResult() != null) {
				for (Object result : dialog.getResult()) {
					SubjectNode node = (SubjectNode) result;
					subjectsToAdd.add(node.getResource());
				}
				annotationSetNode.addSubjects(subjectsToAdd);
				addSubjectRow(subjectsToAdd);
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * This is called when the object is about to dispose
	 * 
	 */
	private void disposeResources() {
		
		timeBarViewer.getSelectionModel().clearSelection();
		timeBarViewer.fireSelectionChanged();
		dndTarget.dispose();
		
		if (timeBarIntervalRenderer != null)
			timeBarIntervalRenderer.disposeResource();
		
		if (addSubjectImage!=null && !addSubjectImage.isDisposed()) {
			addSubjectImage.dispose();
			addSubjectImage = null;
		}
	}

	private void addSubjectRow(List<Subject> subjectsToAdd) {
		for (Subject result : subjectsToAdd) {
			DefaultRowHeader header = new DefaultRowHeader(result.getName());
			SubjectRowModel captionBarRow = new SubjectRowModel(header, timebarModel, result, timebarMarker, captionChangeListener);
			captionBarRow.initCaptions(mediaStartDateTime.copy(), annotationSetNode.getAnnotationsForSubject(result));
			timebarModel.addRow(captionBarRow);
		}
	}
	
	private void removeSubjectRow(List<Subject> subjectsToAdd) {
		//
	}

	public TimeBarViewer getTimeBarViewer() {
		return timeBarViewer;
	}

	private void updateScale() {
		timeBarViewer.setPixelPerSecond((double)((((Composite) timeBarViewer).getClientArea().width - timeBarViewer.getYAxisWidth() - 3) * 1000)/(double)(collectionMediaClip.getDuration()));
		timeScale.setMaximum((int) (timeBarViewer.getPixelPerSecond() * 1000 * SCALE_ZOOM_TIMES));
		timeScale.setMinimum((int) (timeBarViewer.getPixelPerSecond() * 1000));         
		timeScale.setSelection((int) (timeBarViewer.getPixelPerSecond() * 1000));
	}

	private void initData() {
		mediaStartDateTime = new JaretDate().setTime(0, 0, 0, collectionMediaClip.getStartOffset());

		timebarModel = new DefaultTimeBarModel();		
		timeBarViewer.setTimeScalePosition(TimeBarViewer.TIMESCALE_POSITION_TOP);
		timeBarViewer.setMilliAccuracy(true);
		timeBarViewer.registerTimeBarRenderer(AnnotationIntervalImpl.class, new AnnotationIntervalRender());
		timeBarViewer.setMinDate(mediaStartDateTime.copy());
		timeBarViewer.setMaxDate(mediaStartDateTime.copy().advanceMillis((long) collectionMediaClip.getDuration()));
		timeBarViewer.setOptimizeScrolling(true);
		timeBarViewer.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
				//	
			}

			public void controlResized(ControlEvent e) {
				if (collectionMediaClip!=null) {
					updateScale();
				}
			}
		});

		timeScale.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// 
			}

			public void widgetSelected(SelectionEvent e) {
				timeBarViewer.setPixelPerSecond((double) timeScale.getSelection() / 1000);
			}
		});

		mediaTimeChangeListener = new MediaTimeChangeListener() {

			public void playBackChanged(final TimeChangeEvent eventArg) {

				Display.getDefault().syncExec(new Runnable(){
					public void run() {
						if (timeBarViewer.isDisposed()) 
							return;
						if (!isMarkerDragging) 
							timebarMarker.setDate(eventArg.getCurrentTime());
					}
				});
			}

		};

		captionChangeListener = new ICaptionChangeListener() {

			public void captionChange(Object source, AnnotationChangeEvent eventArg) {
				if (eventArg.getCaptionState() == CaptionPublishState.SET && !captions.containsKey(source)) {
					captions.put(source, eventArg.getCaptionText());
				}
				if (eventArg.getCaptionState() == CaptionPublishState.UNSET && captions.containsKey(source)) {
					captions.remove(source);
				}

				StringBuilder captionBuilder = new StringBuilder();
				for (Entry<Object, String> set : captions.entrySet()) {
					AnnotationIntervalImpl cI = (AnnotationIntervalImpl) set.getKey();
					captionBuilder.append("[");
					captionBuilder.append(cI.getOwner().getSubject().getName());
					captionBuilder.append(": ");
					captionBuilder.append(set.getValue());
					captionBuilder.append("] ");
				}

				captionTextView.setText(captionBuilder.toString());
			}
		};


		timebarModel.addTimeBarModelListener(new TimeBarModelListener() {

			public void elementAdded(TimeBarModel arg0, TimeBarRow arg1,
					Interval arg2) {
				if (arg2 instanceof AnnotationIntervalImpl && arg1 instanceof SubjectRowModel) {
					
					//Update the model
					AnnotationIntervalImpl annotationInterval = (AnnotationIntervalImpl) arg2;
					List<Annotation> annotationsToBeAdded = new ArrayList<Annotation>();
					annotationsToBeAdded.add(annotationInterval.getAnalysisCaption());
					annotationSetNode.addAnnotations(annotationsToBeAdded,((SubjectRowModel) arg1).getSubject());
				}
			}

			public void elementChanged(TimeBarModel arg0, TimeBarRow arg1,
					Interval arg2) {
			}

			public void elementRemoved(TimeBarModel arg0, TimeBarRow arg1,
					Interval arg2) {
				if (arg2 instanceof AnnotationIntervalImpl) {
					
					//update the model
					AnnotationIntervalImpl annotationInterval = (AnnotationIntervalImpl) arg2;
					annotationInterval.removeCaptionListener(captionChangeListener);
					List<Annotation> annotationsToBeRemoved = new ArrayList<Annotation>();
					annotationsToBeRemoved.add(annotationInterval.getAnalysisCaption());
					annotationSetNode.removeAnnoations(annotationsToBeRemoved);
				}
			}

			public void headerChanged(TimeBarModel arg0, TimeBarRow arg1,
					Object arg2) {
			}

			public void modelDataChanged(TimeBarModel arg0) {
			}

			public void rowAdded(TimeBarModel arg0, TimeBarRow arg1) {				
			}

			public void rowDataChanged(TimeBarModel arg0, TimeBarRow arg1) {
			}

			public void rowRemoved(TimeBarModel arg0, TimeBarRow arg1) {
				if (arg1 instanceof SubjectRowModel) {
					SubjectRowModel row = (SubjectRowModel) arg1;
					List<Subject> subjectsToBeRemoved = new ArrayList<Subject>();
					subjectsToBeRemoved.add(row.getSubject());
					annotationSetNode.removeSubjects(subjectsToBeRemoved);
				}
			}

		});


		disableMediaRowSelection();

		timebarMarker = new TimeBarMarkerImpl(true, mediaStartDateTime.copy());

		registerDraggingListener();

		DefaultRowHeader header = new DefaultRowHeader("Collection Media");
		timebarMediaRow = new DefaultTimeBarRowModel(header);
		initMediaClipRows();
		timebarModel.addRow(timebarMediaRow);
		timeBarViewer.setModel(timebarModel);
		timeBarViewer.addMarker(timebarMarker);
		
		timeBarViewer.getSelectionModel().setMultipleSelectionAllowed(true);
		timeBarViewer.setLineDraggingAllowed(false);
		timeBarIntervalRenderer = new TimeBarIntervalRenderer();
		timeBarViewer.registerTimeBarRenderer(MediaClipIntervalImpl.class, timeBarIntervalRenderer);
		registerIntervalModifier();
		registerMediaPropertyChangeListener();
		updateScale();

		// Adding rows
		addSubjectRow(annotationSetNode.getResource().getSubjectList());

	}

	private void disableMediaRowSelection() {
		timeBarViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection != null && selection instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection) selection).getFirstElement();
					if (timebarMediaRow == object) {
						timeBarViewer.getSelectionModel().clearRowSelection();
					}
				}
			}			
		});
	}

	private void addAnnotationInterval() {
		IntervalAnnotation analysisCaption = new IntervalAnnotation();
		analysisCaption.setId(UUID.randomUUID().toString());
		int markerDateMilliValue = (int) _addedCaptionInterval.getBegin().copy().getMillisInDay() + _addedCaptionInterval.getBegin().copy().getMillis();
		analysisCaption.setStartTime(markerDateMilliValue);
		analysisCaption.setDuration((int) _addedCaptionInterval.getEnd().diffMilliSeconds(_addedCaptionInterval.getBegin()));
		analysisCaption.setText(captionText.getText());
		analysisCaption.setSubject(intervalAddedRow.getSubject());
		analysisCaption.setAnnotationSet(annotationSetNode.getResource());
		List<Annotation> captionsToAdd = new ArrayList<Annotation>();
		captionsToAdd.add(analysisCaption);
		AnnotationIntervalImpl mediaInterval = new AnnotationIntervalImpl(_addedCaptionInterval.getBegin().copy(),_addedCaptionInterval.getEnd().copy(), analysisCaption, timebarMarker, intervalAddedRow, captionChangeListener);
		mediaInterval.setLabel(captionText.getText());
		intervalAddedRow.addInterval(mediaInterval);
		timeBarViewer.setFocussedInterval(intervalAddedRow, mediaInterval);
		captionText.setText("");
		captionText.setFocus();
	}
	
	private void removeAnnotationInterval(AnnotationIntervalImpl annotationInterval) {
		//
	}

	private void registerMediaPropertyChangeListener() {
		//
	}

	private void registerDraggingListener() {
		timebarMarker.addTimeBarMarkerListener(new TimeBarMarkerListener() {
			public void markerDescriptionChanged(TimeBarMarker arg0,
					String arg1, String arg2) {
				//
			}

			public void markerMoved(TimeBarMarker arg0, JaretDate arg1,
					JaretDate arg2) {
				if (isMarkerDragging) {
					for (Interval interval : timebarMediaRow.getIntervals(timebarMarker.getDate())) {
						MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
						mediaInterval.updateMediaTime();
					}
				}

				if (_addedCaptionInterval!=null) {
					_addedCaptionInterval.setEnd(arg2.copy());
				}


			}
		});

		timeBarViewer.addTimeBarChangeListener(new ITimeBarChangeListener() {
			public void intervalChangeCancelled(TimeBarRow row, Interval interval) {}
			public void intervalChangeStarted(TimeBarRow row, Interval interval) {}
			public void intervalChanged(TimeBarRow row, Interval interval, JaretDate oldBegin, JaretDate oldEnd) {
				
				//Save changes to the annotation after user update
				if (interval instanceof AnnotationIntervalImpl) {
					annotationSetNode.updateAnnotation(((AnnotationIntervalImpl) interval).getAnalysisCaption());
				}
				
			}
			public void intervalIntermediateChange(TimeBarRow arg0,Interval arg1, JaretDate arg2, JaretDate arg3) {}
			public void markerDragStarted(TimeBarMarker marker) {
				isMarkerDragging = true;
			}

			public void markerDragStopped(TimeBarMarker marker) {
				isMarkerDragging = false;
				captionText.setFocus();
			}
		});
	}

	private void registerIntervalModifier(){
		timeBarViewer.addIntervalModificator(new DefaultIntervalModificator(){

			@Override
			public boolean isShiftingAllowed(TimeBarRow row, Interval interval) {
				if (row == timebarMediaRow)
					return false;
				else return true;

			}

			@Override
			public boolean isSizingAllowed(TimeBarRow row, Interval interval) {
				if (row == timebarMediaRow)
					return false;
				else return true;
			}

			@Override
			public boolean newBeginAllowed(TimeBarRow row, Interval interval,
					JaretDate newBegin) {
				if (row == timebarMediaRow)
					return false;
				else return true;
			}

			@Override
			public boolean newEndAllowed(TimeBarRow row, Interval interval,
					JaretDate newEnd) {
				if (row == timebarMediaRow)
					return false;
				else return true;
			}

			@Override
			public boolean shiftAllowed(TimeBarRow row, Interval interval,
					JaretDate newBegin) {
				if (row == timebarMediaRow)
					return false;
				else return true;
			}			
		});
	}

	private void initMediaClipRows() {
		for (CollectionMedia collectionMedia : collectionMediaList) {
			MediaClipIntervalImpl mediaClipInterval = new MediaClipIntervalImpl(collectionMedia, ((AbstractMedia)mediaMap.get(collectionMedia)), mediaStartDateTime, collectionMediaClip.getDuration(), timebarMarker, this);
			mediaClipInterval.addClipInMarkerRangeListener(new MarkerInRangeListener() {

				public void markerInRangeChanged(MarkerInRangeEvent eventArg) {
					if (eventArg.getTimeAvailable() && !mediaClipsInMarkerRangeList.contains(eventArg.getSource()))
						mediaClipsInMarkerRangeList.add((MediaClipIntervalImpl) eventArg.getSource());
					if (!eventArg.getTimeAvailable() && mediaClipsInMarkerRangeList.contains(eventArg.getSource()))
						mediaClipsInMarkerRangeList.remove(eventArg.getSource());
					updateTimeListener();
					if (AnnotationsMediaControl.this.isPlaying() && mediaClipsInMarkerRangeList.size()==0)
						AnnotationsMediaControl.this.setPlaying(false);
				}

			});
			if (mediaClipInterval.isTimeAvailable())
			{
				mediaClipsInMarkerRangeList.add(mediaClipInterval);
				if (currentTimeProvider == null) {
					currentTimeProvider = mediaClipInterval;
					currentTimeProvider.addTimeChangeListener(mediaTimeChangeListener);
				}
			}
			timebarMediaRow.addInterval(mediaClipInterval);
		}
	}

	/**
	 * 
	 * 
	 */
	private void updateTimeListener() {
		if (currentTimeProvider==null) {
			if (mediaClipsInMarkerRangeList.size() > 0) {
				currentTimeProvider = mediaClipsInMarkerRangeList.get(0);
				currentTimeProvider.addTimeChangeListener(mediaTimeChangeListener);
			}
		} else {
			if (!mediaClipsInMarkerRangeList.contains(currentTimeProvider)) {
				currentTimeProvider.removeTimeChangeListener(mediaTimeChangeListener);
				if (mediaClipsInMarkerRangeList.size() > 0) {
					currentTimeProvider = mediaClipsInMarkerRangeList.get(0);
					currentTimeProvider.addTimeChangeListener(mediaTimeChangeListener);
				} else currentTimeProvider = null;
			}
		}

	}




	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#isPlaying()
	 */
	public boolean isPlaying() {
		return isPlaying;
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#setPlaying(boolean)
	 */
	public void setPlaying(boolean playValue) {

		for (Interval interval : timebarMediaRow.getIntervals()) {
			MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
			if (playValue) 
				mediaInterval.play();
			else mediaInterval.stop();
		}
		isPlaying = playValue;

	}




	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#setRowSelection(int)
	 */
	public void setRowSelection(int number) {
		if (number < timebarModel.getRowCount()) {
			timeBarViewer.getSelectionModel().setSelectedRow(timebarModel.getRow(number));
			captionText.setFocus();
		}

	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#startCaptionMark()
	 */
	public void startCaptionMark() {
		if (_addedCaptionInterval != null) 
			return;
		if (!timeBarViewer.getSelectionModel().isEmpty() && captionText.getText().compareTo("") != 0) {
			intervalAddedRow = (SubjectRowModel) timeBarViewer.getSelectionModel().getSelectedRows().get(0);
			_addedCaptionInterval = new IntervalImpl();
			_addedCaptionInterval.setBegin(timebarMarker.getDate().copy());
			_addedCaptionInterval.setEnd(timebarMarker.getDate().copy().advanceMillis(10));
			intervalAddedRow.addInterval(_addedCaptionInterval);
			timeBarViewer.setFocussedInterval(intervalAddedRow, _addedCaptionInterval);
			if (!isPlaying)
				this.setPlaying(true);
		} else {
			MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_INFORMATION);
			messageBox.setText("Error adding a new entry!");
			messageBox.setMessage("Please ensure a row is selected and the text is not empty.");
			messageBox.open();
		}
	}



	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#stepFF()
	 */
	public void stepFF() {
		for (Interval interval : timebarMediaRow.getIntervals()) {
			MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
			mediaInterval.stepFF(STEP_MILLI);
		}
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#stepRE()
	 */
	public void stepRE() {
		for (Interval interval : timebarMediaRow.getIntervals()) {
			MediaClipIntervalImpl mediaInterval = (MediaClipIntervalImpl) interval;
			mediaInterval.stepRE(STEP_MILLI);
		}
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#stopCaptionMark()
	 */
	public void stopCaptionMark() {
		if (_addedCaptionInterval != null) {
			addAnnotationInterval();
			intervalAddedRow.remInterval(_addedCaptionInterval);
			_addedCaptionInterval = null;
			intervalAddedRow = null;
			if (isPlaying)
				this.setPlaying(false);
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#isMute(uk.ac.durham.tel.synergynet.ats.subtitle.ui.MediaClipIntervalImpl)
	 */
	public boolean isMute(MediaClipIntervalImpl mediaClipIntervalImpl) {
		return mediaClipIntervalImpl.isMute();
	}





	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ISubtitleControl#setMute(uk.ac.durham.tel.synergynet.ats.subtitle.ui.MediaClipIntervalImpl, boolean)
	 */
	public void setMute(MediaClipIntervalImpl mediaClipIntervalImpl,
			boolean muteValue) {
		mediaClipIntervalImpl.setMute(muteValue);

	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener#addCaptionListener(uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener.ICaptionChangeListener)
	 */
	public void addCaptionListener(ICaptionChangeListener captionChangeListener) {
		captionChangeListeners.add(captionChangeListener);

	}
	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener#removeCaptionListener(uk.ac.durham.tel.synergynet.ats.subtitle.ui.ICaptionListener.ICaptionChangeListener)
	 */
	public void removeCaptionListener(
			ICaptionChangeListener captionChangeListener) {
		if (!captionChangeListeners.contains(captionChangeListener))
			captionChangeListeners.remove(captionChangeListener);

	}
	
	private void setupDND() {

		// ////////////////////
		// Drop target

		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		dndTarget = new DropTarget(timeBarViewer, operations);
		
		// Receive data in Text
		final TextTransfer textTransfer = TextTransfer.getInstance();
		Transfer[] types = new Transfer[] {textTransfer, LocalSelectionTransfer.getTransfer()};
		dndTarget.setTransfer(types);

		dndTarget.addDropListener(new DropTargetListener() {
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
										annotationSetNode.addAttributeToAnnotation(((AnnotationIntervalImpl) il.get(0)).getAnalysisCaption(), list);
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
}

