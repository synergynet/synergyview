package synergyviewcore.collections.ui.wizards;

import java.util.UUID;

import org.eclipse.jface.wizard.Wizard;

import synergyviewcore.collections.model.Collection;
import synergyviewcore.collections.model.CollectionRootNode;
import synergyviewcore.resource.ResourceLoader;
import synergyviewcore.util.FileHelper;

/**
 * The Class NewCollectionWizard.
 */
public class NewCollectionWizard extends Wizard {
	
	/** The collection root node. */
	private CollectionRootNode collectionRootNode;
	
	/** The collection to add new. */
	private Collection collectionToAddNew;
	
	/**
	 * Instantiates a new new collection wizard.
	 * 
	 * @param collectionFolder
	 *            the collection folder
	 */
	public NewCollectionWizard(CollectionRootNode collectionFolder) {
		collectionRootNode = collectionFolder;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	@Override
	public void addPages() {
		initCollection();
		this.addPage(new NewCollectionWizardPage(collectionToAddNew,
				collectionRootNode.getChildrenNames()));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
		return ResourceLoader.getString("DIALOG_TITLE_NEW_MEDIA_COLLECTION");
	}
	
	/**
	 * Inits the collection.
	 */
	private void initCollection() {
		// Creating a new empty collection
		collectionToAddNew = new Collection();
		collectionToAddNew.setId(UUID.randomUUID().toString());
		collectionToAddNew.setFileName(FileHelper.getUniqueAFileName("xml"));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		collectionRootNode.addChildCollection(collectionToAddNew);
		return true;
	}
}
