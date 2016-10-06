package synergyviewmvc.projects.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.osgi.PersistenceProvider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import synergyviewmvc.Activator;
import synergyviewmvc.attributes.model.ProjectAttributesRootNode;
import synergyviewmvc.collections.model.CollectionsRootNode;
import synergyviewmvc.media.model.MediaRootNode;
import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.navigation.projects.model.IEMFactoryProvider;
import synergyviewmvc.navigation.projects.model.IProjectPathsProvider;
import synergyviewmvc.projects.ResourceHelper;
import synergyviewmvc.subjects.model.SubjectsRootNode;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

public class ProjectNode extends AbstractParent<IProject> implements IEMFactoryProvider, IProjectPathsProvider {

	public static final String PROJECT_ICON = "project.png";
	public static final String PROJECT_DB = "ProjectDB";
	private MediaRootNode mediaRootNode;
	private CollectionsRootNode collectionsRootNode;
	private EntityManagerFactory eManagerFactory;
	private ProjectAttributesRootNode attributesRootNode;
	private SubjectsRootNode subjectsRootNode;

	public ProjectNode(IProject projectValue, IParentNode parentValue) {
		super(projectValue, parentValue);
		
		// Creating a DB Entity Manager
		createProjectDB();
		this.setLabel(projectValue.getName());
		if (projectValue.exists(new Path(MediaRootNode.getMediaFolderName()))) {
			mediaRootNode = new MediaRootNode(projectValue.getFolder(MediaRootNode.getMediaFolderName()), this);
			addNode(mediaRootNode);
		}
		collectionsRootNode = new CollectionsRootNode(this);
		addNode(collectionsRootNode);
		attributesRootNode = new ProjectAttributesRootNode(this);
		addNode(attributesRootNode);
		subjectsRootNode = new SubjectsRootNode(this);
		addNode(subjectsRootNode);
	}
	
	
	private void createProjectDB() {
		Map<String, Object> map = new HashMap<String, Object>();
		String dbUri = String.format(
				"jdbc:derby:%s/%s;create=true", ((IResource)this.getResource()).getLocation().toString(), PROJECT_DB);
		map.put(PersistenceUnitProperties.JDBC_URL, dbUri);
		map.put(PersistenceUnitProperties.CLASSLOADER, Activator.class.getClassLoader());
		eManagerFactory = new PersistenceProvider().createEntityManagerFactory(PROJECT_DB, map);
		//Initialise the database on startup
		eManagerFactory.createEntityManager().close();
		
		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			public void postShutdown(IWorkbench workbench) {
				// TODO Auto-generated method stub
				
			}

			public boolean preShutdown(IWorkbench workbench, boolean forced) {
				if (ProjectNode.this.eManagerFactory.isOpen())
					ProjectNode.this.eManagerFactory.close();
				return true;
			}
			
		});
	}

	private void addNode(INode folderValue) {
		children.add(folderValue);
		this.fireChildrenChanged();
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() {
		try {
			deleteChildren(new INode[] {mediaRootNode, collectionsRootNode, attributesRootNode});
			ResourceHelper.deleteResources(new IResource[]{this.resource});
		} catch (NodeRemoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.projects.model.IEMFactoryProvider#getEntityManagerFactory()
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return eManagerFactory;
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.commons.jface.node.AbstractBaseNode#getIcon()
	 */
	@Override
	public ImageDescriptor getIcon() {
		return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT);
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.projects.model.IEMFactoryProvider#getProjectRootNode()
	 */
	public ProjectNode getProjectRootNode() {
		return this;
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.projects.model.IEMFactoryProvider#getCollectionsRootNode()
	 */
	public CollectionsRootNode getCollectionsRootNode() {
		return collectionsRootNode;
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.projects.model.IEMFactoryProvider#getAttributesRootNode()
	 */
	public ProjectAttributesRootNode getAttributesRootNode() {
		return attributesRootNode;
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.projects.model.IEMFactoryProvider#getSubjectsRootNode()
	 */
	public SubjectsRootNode getSubjectsRootNode() {
		return subjectsRootNode;
	}


	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.projects.model.IProjectPathsProvider#getMediaRootNode()
	 */
	public MediaRootNode getMediaRootNode() {
		return mediaRootNode;
	}
}
