/**
 * File: ExportCollectionAnnotationWizard.java Copyright (c) 2011 phyo This
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

package synergyviewcore.collections.ui.wizards;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;

import synergyviewcore.collections.format.ICollectionClipAnnotationFormatter;
import synergyviewcore.collections.format.TsvCollectionClipAnnotationFormatter;
import synergyviewcore.collections.model.CollectionMediaClip;
import synergyviewcore.collections.model.CollectionNode;

/**
 * The Class ExportCollectionAnnotationWizard.
 * 
 * @author phyo
 */
public class ExportCollectionAnnotationWizard extends Wizard {

    /** The collection node. */
    private CollectionNode collectionNode;

    /** The export collection annotation file selector wizard page. */
    private ExportCollectionAnnotationFileSelectorWizardPage exportCollectionAnnotationFileSelectorWizardPage;

    /** The export collection annotation wizard page. */
    private ExportCollectionAnnotationWizardPage exportCollectionAnnotationWizardPage;

    /**
     * Instantiates a new export collection annotation wizard.
     * 
     * @param collectionNode
     *            the collection node
     */
    public ExportCollectionAnnotationWizard(CollectionNode collectionNode) {
	this.collectionNode = collectionNode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {
	exportCollectionAnnotationWizardPage = new ExportCollectionAnnotationWizardPage(collectionNode.getResource());
	exportCollectionAnnotationFileSelectorWizardPage = new ExportCollectionAnnotationFileSelectorWizardPage();
	this.addPage(exportCollectionAnnotationWizardPage);
	this.addPage(exportCollectionAnnotationFileSelectorWizardPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
     */
    @Override
    public String getWindowTitle() {
	return "Export Annotations in the Collection";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {
	try {
	    final List<CollectionMediaClip> selectedList = exportCollectionAnnotationWizardPage.getSelectedCollcationMediaClips();
	    final File selectedFileToSave = exportCollectionAnnotationFileSelectorWizardPage.getSelectedFile();
	    if ((selectedList.size() > 0) && (selectedFileToSave != null)) {

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(this.getShell());
		try {
		    dialog.run(true, true, new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			    monitor.beginTask("Exporting ...", selectedList.size());
			    BufferedOutputStream bufferedOutputStream = null;
			    PrintStream printStream = null;
			    try {
				bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(selectedFileToSave));
				printStream = new PrintStream(bufferedOutputStream, true);
				for (int i = 0; i < selectedList.size(); i++) {
				    ICollectionClipAnnotationFormatter formatter = new TsvCollectionClipAnnotationFormatter();
				    printStream.println(formatter.export(selectedList.get(i)));
				    monitor.worked(i + 1);
				}
			    } catch (Exception ex) {
				throw new InterruptedException("Unable to export the Annotations!");
			    } finally {
				monitor.done();
				if (printStream != null) {
				    printStream.close();
				}
			    }
			}
		    });
		} catch (Exception ex) {
		    MessageDialog.openError(this.getShell(), "Export Error", "Unable to export Annotations!");
		}

		return true;
	    } else {
		return false;
	    }
	} catch (Exception ex) {
	    return false;
	}

    }

}
