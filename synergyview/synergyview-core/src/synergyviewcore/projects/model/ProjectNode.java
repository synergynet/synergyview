package synergyviewcore.projects.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.osgi.PersistenceProvider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.Activator;
import synergyviewcore.attributes.model.ProjectAttributeRootNode;
import synergyviewcore.collections.model.CollectionRootNode;
import synergyviewcore.media.model.MediaRootNode;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.navigation.projects.model.IEMFactoryProvider;
import synergyviewcore.projects.ResourceHelper;
import synergyviewcore.subjects.model.SubjectRootNode;

public class ProjectNode extends AbstractParent<IProject> implements IEMFactoryProvider {

	public static final String PROJECT_ICON = "project.png";
	public static final String PROJECT_DB = "ProjectDB";
	private MediaRootNode mediaRootNode;
	private CollectionRootNode collectionRootNode;
	private EntityManagerFactory eManagerFactory;
	private ProjectAttributeRootNode attributeRootNode;
	private SubjectRootNode subjectRootNode;
	
	public ProjectNode(IProject projectValue, IParentNode parentValue) throws Exception {
		super(projectValue, parentValue);
		// Creating a DB Entity Manager
		createProjectDB();
		this.setLabel(projectValue.getName());
		if (projectValue.exists(new Path(MediaRootNode.getMediaFolderName()))) {
			mediaRootNode = new MediaRootNode(projectValue.getFolder(MediaRootNode.getMediaFolderName()), this);
			addNode(mediaRootNode);
		}
		attributeRootNode = new ProjectAttributeRootNode(this);
		
		//TODO This has to set after creating a attribute root node because the attribute nodes need the root to be created
		attributeRootNode.loadChildAttributeNodes();
		//addNode(attributeRootNode);
		collectionRootNode = new CollectionRootNode(this);
		addNode(collectionRootNode);
		subjectRootNode = new SubjectRootNode(this);
		addNode(subjectRootNode);
	}
	
	
	private void createProjectDB() throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String dbUri = String.format(
					"jdbc:derby:%s/%s;create=true", ((IResource)this.getResource()).getLocation().toString(), PROJECT_DB);
			map.put(PersistenceUnitProperties.JDBC_URL, dbUri);
			map.put(PersistenceUnitProperties.CLASSLOADER, Activator.class.getClassLoader());
			eManagerFactory = new PersistenceProvider().createEntityManagerFactory(PROJECT_DB, map);
			//Initialise the database on startup
			eManagerFactory.createEntityManager().close();
		} catch (Exception ex) {
			throw new Exception("Unable to initialise Project Database", ex);
		}
		
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


	public MediaRootNode getMediaRootNode() {
		return mediaRootNode;
	}
	
	public ProjectAttributeRootNode getProjectAttributeRootNode() {
		return attributeRootNode;
	}
	
	public SubjectRootNode getSubjectRootNode() {
		return subjectRootNode;
	}

	private void addNode(INode node) {
		_children.add(node);
		this.fireChildrenChanged();
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() {
		this.deleteChildren(_children.toArray(new INode[]{}));
		if (ProjectNode.this.eManagerFactory.isOpen())
			ProjectNode.this.eManagerFactory.close();
		ResourceHelper.deleteResources(new IResource[]{this.resource});
	}


	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}


	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.projects.model.IEMFactoryProvider#getEntityManagerFactory()
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return eManagerFactory;
	}
}
