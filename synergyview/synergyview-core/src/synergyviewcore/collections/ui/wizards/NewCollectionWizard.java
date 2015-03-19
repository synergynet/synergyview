package synergyviewcore.collections.ui.wizards;

import java.util.UUID;

import org.eclipse.jface.wizard.Wizard;

import synergyviewcore.collections.model.Collection;
import synergyviewcore.collections.model.CollectionRootNode;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.util.FileHelper;


public class NewCollectionWizard extends Wizard {

	private Collection collectionToAddNew;
	private CollectionRootNode collectionRootNode;
	
	@Override
	public String getWindowTitle() {
		return ResourceLoader.getString("DIALOG_TITLE_NEW_MEDIA_COLLECTION");
	}
	
	public NewCollectionWizard(CollectionRootNode collectionFolder) {
		collectionRootNode = collectionFolder;
	}

	@Override
	public void addPages() {
		initCollection();
		this.addPage(new NewCollectionWizardPage(collectionToAddNew, collectionRootNode.getChildrenNames()));
	}
	
	private void initCollection() {
		// Creating a new empty collection
		collectionToAddNew = new Collection();
		collectionToAddNew.setId(UUID.randomUUID().toString());
		collectionToAddNew.setFileName(FileHelper.getUniqueAFileName("xml"));
	}

	@Override
	public boolean performFinish() {
		collectionRootNode.addChildCollection(collectionToAddNew);
		return true;
	}
}
