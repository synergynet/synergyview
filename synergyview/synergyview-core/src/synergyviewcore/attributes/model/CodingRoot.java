/**
 *  File: AttributeRoot.java
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

package synergyviewcore.attributes.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.jpa.osgi.PersistenceProvider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.Activator;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.navigation.model.IViewerProvider;
import synergyviewcore.navigation.projects.model.IEMFactoryProvider;


/**
 * The Class CodingRoot.
 *
 * @author phyo
 */
public class CodingRoot extends ProjectAttributeRootNode implements IViewerProvider, IEMFactoryProvider {
	
	/** The Constant ATTRIBUTES_DB. */
	public static final String ATTRIBUTES_DB = "AttributesDB";
	
	/** The instance. */
	private static CodingRoot instance; 
	
	/** The _tree viewer. */
	private TreeViewer _treeViewer;

	/**
	 * Instantiates a new coding root.
	 */
	private CodingRoot() {
		super(null);
	}


	/**
	 * Gets the single instance of CodingRoot.
	 *
	 * @return single instance of CodingRoot
	 */
	public static CodingRoot getInstance() {
		if (instance == null) {
			instance = new CodingRoot();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.attributes.model.ProjectAttributeRootNode#getIcon()
	 */
	@Override
	public ImageDescriptor getIcon() {
		return null;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.AbstractNode#getParent()
	 */
	@Override
	public IParentNode getParent() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see synergyviewcore.attributes.model.ProjectAttributeRootNode#loadChildAttributeNodes()
	 */
	@Override
	public void loadChildAttributeNodes() {
		createAttributesDB();
		super.loadChildAttributeNodes();
	}

	/**
	 * Creates the attributes db.
	 */
	private void createAttributesDB() {
		Map<String, Object> map = new HashMap<String, Object>();
		String dbUri = String.format(
				"jdbc:derby:%s/%s;create=true", ResourcesPlugin.getWorkspace().getRoot().getLocation().toString(), ATTRIBUTES_DB);
		map.put(PersistenceUnitProperties.JDBC_URL, dbUri);
		map.put(PersistenceUnitProperties.CLASSLOADER, Activator.class.getClassLoader());
		eManagerFactory = new PersistenceProvider().createEntityManagerFactory(ATTRIBUTES_DB, map);
		eManagerFactory.createEntityManager().close();

		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			public void postShutdown(IWorkbench workbench) { }

			public boolean preShutdown(IWorkbench workbench, boolean forced) {
				if (CodingRoot.this.eManagerFactory.isOpen())
					CodingRoot.this.eManagerFactory.close();
				return true;
			}
		});
	}


	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.IViewerProvider#getTreeViewer()
	 */
	public TreeViewer getTreeViewer() {
		return _treeViewer;
	}

	/**
	 * Sets the tree viewer.
	 *
	 * @param _treeViewer the _treeViewer to set
	 */
	public void setTreeViewer(TreeViewer _treeViewer) {
		this._treeViewer = _treeViewer;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.projects.model.IEMFactoryProvider#getEntityManagerFactory()
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return eManagerFactory;
	}


}
