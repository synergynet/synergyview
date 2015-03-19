/**
 *  File: CollectionMediaClipAnnotationEditor.java
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

package synergyviewmvc.annotations.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
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

import synergyviewmvc.annotations.model.AnnotationSetNode;
import synergyviewmvc.annotations.ui.AnnotationsMediaControl;
import synergyviewmvc.collections.ui.MediaPreviewControl;
import synergyviewmvc.media.model.MediaRootNode;
import synergyviewmvc.projects.model.ProjectNode;
import synergyviewmvc.projects.ui.NodeEditorInput;

/**
 * @author phyo
 *
 */
public class CollectionMediaClipAnnotationEditor extends EditorPart  {

	public static final String ID = "uk.ac.durham.tel.synergynet.ats.annotations.ui.editors.collectionMediaClipAnnotationSetEditor";

	private AnnotationSetNode annotationSetNode;
	private AnnotationsMediaControl annotationMediaControl;
	private MediaPreviewControl mediaPreviewControl;
	private IPartListener refreshListener;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */

	@Override
	public void doSave(IProgressMonitor monitor) {
		//
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		this.getSite().getWorkbenchWindow().getPartService().removePartListener(refreshListener);
	}

	public AnnotationsMediaControl getSubtitleMediaControl() {
		return annotationMediaControl;
	}
	
	public AnnotationSetNode getAnnotationSetNode() {
		return annotationSetNode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException {
		this.setSite(site);
		this.setInput(input);

		annotationSetNode = (AnnotationSetNode) ((NodeEditorInput) input).getNode();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		SashForm container = new SashForm(parent, SWT.VERTICAL);
		addMediaPreview(container);
		addMediaController(container);

//
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		annotationMediaControl.getTimeBarViewer().setMenu(menuManager.createContextMenu(annotationMediaControl));
		getSite().registerContextMenu(menuManager, annotationMediaControl.getTimeBarViewer());
		getSite().setSelectionProvider(annotationMediaControl.getTimeBarViewer());
		
		refreshListener = new IPartListener() {

			public void partActivated(IWorkbenchPart part) {}

			public void partBroughtToTop(IWorkbenchPart part) { }

			public void partClosed(IWorkbenchPart part) {
			}

			public void partDeactivated(IWorkbenchPart part) {
				if (part == CollectionMediaClipAnnotationEditor.this){
					if (CollectionMediaClipAnnotationEditor.this.annotationMediaControl.isPlaying()) {
						CollectionMediaClipAnnotationEditor.this.annotationMediaControl.setPlaying(false);
					}
					CollectionMediaClipAnnotationEditor.this.mediaPreviewControl.redraw();
					CollectionMediaClipAnnotationEditor.this.mediaPreviewControl.update();
					
				}
			}

			public void partOpened(IWorkbenchPart part) { }

		};
		
		this.getSite().getWorkbenchWindow().getPartService().addPartListener(refreshListener);
		this.setPartName(String.format("%s (Annotation)", annotationSetNode.getLabel()));
	}
	
	

	private void addMediaPreview(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER | SWT.CENTER);
		container.setLayout(new GridLayout());
		mediaPreviewControl = new MediaPreviewControl(container, SWT.NONE);
		mediaPreviewControl.setLayoutData(new GridData(GridData.FILL_BOTH));

	}


	/**
	 * 
	 * To add media controller
	 * 
	 */
	private void addMediaController(Composite parent) {
		Composite container = new Composite(parent, SWT.BORDER | SWT.CENTER);
		container.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.verticalAlignment = SWT.FILL;
		gd.grabExcessVerticalSpace = true;


		annotationMediaControl = new AnnotationsMediaControl(container, SWT.NONE, annotationSetNode, mediaPreviewControl.getObservableMediaPreviewList());
		annotationMediaControl.setLayoutData(gd);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() { }

}
