/**
 *  File: ExportCollectionAnnotationWizardPage.java
 *  Copyright (c) 2011
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

package synergyviewcore.collections.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import synergyviewcore.collections.model.Collection;
import synergyviewcore.collections.model.CollectionMediaClip;

/**
 * @author phyo
 * 
 */
public class ExportCollectionAnnotationWizardPage extends WizardPage {
	private Collection collection;
	private List<CollectionMediaClip> selectedCollcationMediaClips = new ArrayList<CollectionMediaClip>();
	
	/**
	 * @param pageName
	 */
	protected ExportCollectionAnnotationWizardPage(Collection collection) {
		super("Export Annotations in the Collection");
		this.setTitle("Export Annotations in the Collection");
		setDescription("Select the Collection Media Clips to Export");
		this.collection = collection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	public void createControl(Composite parent) {
		final Composite area = new Composite(parent, SWT.NONE);
		setControl(area);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		area.setLayoutData(gridData);
		GridLayout layoutData = new GridLayout(1, false);
		area.setLayout(layoutData);
		this.setPageComplete(false);
		Table table = new Table(area, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		table.setLayoutData(gridData);
		for (CollectionMediaClip clip : collection.getCollectionMediaClipList()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(clip.getClipName());
			item.setData(clip);
		}
		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK) {
					TableItem changedItem = (TableItem) event.item;
					if (changedItem.getChecked())
						selectedCollcationMediaClips.add((CollectionMediaClip) ((TableItem) event.item).getData());
					else selectedCollcationMediaClips.remove((CollectionMediaClip) ((TableItem) event.item).getData());
				}
				if (selectedCollcationMediaClips.isEmpty())
					ExportCollectionAnnotationWizardPage.this.setPageComplete(false);
				else 
					ExportCollectionAnnotationWizardPage.this.setPageComplete(true);
			}
		});
	}
	
	public List<CollectionMediaClip> getSelectedCollcationMediaClips() {
		return selectedCollcationMediaClips;
	}

}
