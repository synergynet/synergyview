/**
 * File: NewAttributeWizard.java Copyright (c) 2010 phyo This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.attributes.ui.wizards;

import java.util.UUID;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import synergyviewcore.attributes.model.Attribute;
import synergyviewcore.attributes.model.AttributeNode;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.projects.model.ProjectNode;

/**
 * The Class NewAttributeWizard.
 * 
 * @author phyo
 */
public class NewAttributeWizard extends Wizard implements IWorkbenchWizard {

    /** The main page. */
    private NewAttributeWizardPage mainPage;

    /** The new attribute. */
    private Attribute newAttribute;

    /** The parent node. */
    private INode parentNode = null;

    /**
     * Instantiates a new new attribute wizard.
     */
    public NewAttributeWizard() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.IWizard#addPages()
     */
    public void addPages() {
	super.addPages();
	addPage(mainPage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench arg0, IStructuredSelection arg1) {
	if (arg1 != StructuredSelection.EMPTY) {
	    parentNode = (INode) arg1.getFirstElement();
	}
	newAttribute = new Attribute();
	newAttribute.setId(UUID.randomUUID().toString());
	mainPage = new NewAttributeWizardPage("New Attribute Page", newAttribute);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

	if (parentNode instanceof AttributeNode) {
	    ((ProjectNode) parentNode.getLastParent()).getProjectAttributeRootNode().addAttribute(newAttribute, ((AttributeNode) parentNode).getResource());
	} else {
	    ((ProjectNode) parentNode.getLastParent()).getProjectAttributeRootNode().addAttribute(newAttribute, null);
	}
	return true;
    }

}
