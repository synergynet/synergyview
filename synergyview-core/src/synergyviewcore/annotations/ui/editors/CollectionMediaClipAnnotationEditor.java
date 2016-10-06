/**
 * File: CollectionMediaClipAnnotationEditor.java Copyright (c) 2010 phyo This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.annotations.ui.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import synergyviewcore.annotations.model.AnnotationSetNode;
import synergyviewcore.annotations.ui.AnnotationsMediaControl;
import synergyviewcore.collections.ui.MediaPreviewControl;
import synergyviewcore.media.model.MediaRootNode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.projects.ui.NodeEditorInput;

/**
 * The Class CollectionMediaClipAnnotationEditor.
 * 
 * @author phyo
 */
public class CollectionMediaClipAnnotationEditor extends EditorPart implements
		ISelectionProvider {
	
	/** The Constant ID. */
	public static final String ID = "synergyviewcore.subtitle.ui.editors.collectionMediaClipAnnotationSetEditor";
	
	/** The annotation media control. */
	private AnnotationsMediaControl annotationMediaControl;
	
	/** The annotation set node. */
	private AnnotationSetNode annotationSetNode;
	
	/** The listeners. */
	private ListenerList listeners = new ListenerList();
	
	/** The media preview control. */
	private MediaPreviewControl mediaPreviewControl;
	
	/** The media root node. */
	private MediaRootNode mediaRootNode;
	
	/** The refresh listener. */
	private IPartListener refreshListener;
	
	/** The selection list. */
	private List<Object> selectionList = new ArrayList<Object>();
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	
	/**
	 * To add media controller.
	 * 
	 * @param parent
	 *            the parent
	 */
	private void addMediaController(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER | SWT.CENTER);
		container.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;
		
		annotationMediaControl = new AnnotationsMediaControl(container,
				SWT.NONE, annotationSetNode,
				mediaPreviewControl.getObservableMediaPreviewList(),
				mediaRootNode);
		annotationMediaControl.setLayoutData(gd);
		
	}
	
	/**
	 * Adds the media preview.
	 * 
	 * @param parent
	 *            the parent
	 */
	private void addMediaPreview(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER | SWT.CENTER);
		container.setLayout(new GridLayout());
		mediaPreviewControl = new MediaPreviewControl(container, SWT.NONE);
		mediaPreviewControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		SashForm container = new SashForm(parent, SWT.VERTICAL);
		addMediaPreview(container);
		addMediaController(container);
		
		//
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		annotationMediaControl.getTimeBarViewer().setMenu(
				menuManager.createContextMenu(annotationMediaControl
						.getTimeBarViewer()));
		getSite().registerContextMenu(menuManager, this);
		getSite().setSelectionProvider(this);
		annotationMediaControl.getTimeBarViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						ISelection selection = event.getSelection();
						if (selection instanceof IStructuredSelection) {
							selectionList.clear();
							IStructuredSelection sSelection = (IStructuredSelection) selection;
							@SuppressWarnings("rawtypes")
							Iterator i = sSelection.iterator();
							while (i.hasNext()) {
								Object o = i.next();
								if (o != null) {
									selectionList.add(o);
								}
							}
							if (!selectionList.isEmpty()) {
								CollectionMediaClipAnnotationEditor.this
										.setSelection(new StructuredSelection(
												selectionList));
							} else {
								CollectionMediaClipAnnotationEditor.this
										.setSelection(StructuredSelection.EMPTY);
							}
						}
					}
				});
		
		refreshListener = new IPartListener() {
			
			public void partActivated(IWorkbenchPart part) {
			}
			
			public void partBroughtToTop(IWorkbenchPart part) {
			}
			
			public void partClosed(IWorkbenchPart part) {
			}
			
			public void partDeactivated(IWorkbenchPart part) {
				if (part == CollectionMediaClipAnnotationEditor.this) {
					if (CollectionMediaClipAnnotationEditor.this.annotationMediaControl
							.isPlaying()) {
						CollectionMediaClipAnnotationEditor.this.annotationMediaControl
								.setPlaying(false);
					}
					CollectionMediaClipAnnotationEditor.this.mediaPreviewControl
							.redraw();
					CollectionMediaClipAnnotationEditor.this.mediaPreviewControl
							.update();
					
				}
			}
			
			public void partOpened(IWorkbenchPart part) {
			}
			
		};
		
		this.getSite().getWorkbenchWindow().getPartService()
				.addPartListener(refreshListener);
		this.setPartName(String.format("%s (Annotation)",
				annotationSetNode.getLabel()));
		this.getAnnotationMediaControl().setSelectionService(
				this.getSite().getWorkbenchWindow().getSelectionService());
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		this.getSite().getWorkbenchWindow().getPartService()
				.removePartListener(refreshListener);
	}
	
	@Override
	public void doSave(IProgressMonitor monitor) {
		//
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Gets the annotation media control.
	 * 
	 * @return the annotation media control
	 */
	public AnnotationsMediaControl getAnnotationMediaControl() {
		return annotationMediaControl;
	}
	
	/**
	 * Gets the annotation set node.
	 * 
	 * @return the annotation set node
	 */
	public AnnotationSetNode getAnnotationSetNode() {
		return annotationSetNode;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection() {
		return new StructuredSelection(selectionList);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		this.setSite(site);
		this.setInput(input);
		
		annotationSetNode = (AnnotationSetNode) ((NodeEditorInput) input)
				.getNode();
		mediaRootNode = ((ProjectNode) annotationSetNode.getLastParent())
				.getMediaRootNode();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection) {
		for (Object o : listeners.getListeners()) {
			((ISelectionChangedListener) o)
					.selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}
	
}
