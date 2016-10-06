package synergyviewmvc.collections.ui.wizards;

import java.util.UUID;

import org.eclipse.jface.wizard.Wizard;

import synergyviewmvc.collections.model.Collection;
import synergyviewmvc.collections.model.CollectionsRootNode;
import synergyviewmvc.resource.ResourceLoader;
import synergyviewmvc.util.FileHelper;


public class NewCollectionWizard extends Wizard {

	private Collection collection;
	private CollectionsRootNode collectionRootNode;
	
	@Override
	public String getWindowTitle() {
		return ResourceLoader.getString("DIALOG_TITLE_NEW_MEDIA_COLLECTION");
	}
	
	public NewCollectionWizard(CollectionsRootNode collectionRootNode) {
		this.collectionRootNode = collectionRootNode;
	}

	@Override
	public void addPages() {
		initCollection();
		this.addPage(new NewCollectionWizardPage(collection, collectionRootNode.getChildrenNames()));
	}
	
	private void initCollection() {
		// Creating a new empty collection
		collection = new Collection();
		collection.setId(UUID.randomUUID().toString());
		collection.setFileName(FileHelper.getUniqueAFileName("xml"));
	}

	@Override
	public boolean performFinish() {
		collectionRootNode.addChildCollection(collection);
		return true;
	}
}
