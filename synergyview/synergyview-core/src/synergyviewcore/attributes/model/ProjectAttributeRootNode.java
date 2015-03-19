/**
 *  File: ProjectAttribute.java
 *  Copyright (c) 2010
 *  phyokyaw
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import synergyviewcommons.collections.CollectionChangeEvent;
import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.CollectionDiffEntry;
import synergyviewcommons.collections.IObservableList;
import synergyviewcommons.collections.ObservableList;
import synergyviewcore.Activator;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.DisposeException;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.navigation.model.IViewerProvider;
import synergyviewcore.resource.ResourceLoader;

/**
 * @author phyokyaw
 *
 */
public class ProjectAttributeRootNode extends AbstractParent<Attribute> implements CollectionChangeListener, IViewerProvider {	
	private Map<Attribute, IObservableList<List<Attribute>, Attribute>> attributesMap;
	private IObservableList<List<Attribute>, Attribute> rootAttributesList;
	protected EntityManagerFactory eManagerFactory;
	private Map<Attribute, AttributeNode> map = new HashMap<Attribute, AttributeNode>();
	public static final String ATTRIBUTE_PARENT_ICON = "folder_table.png";
	private TreeViewer treeViewer;	
	
	@Override
	public ImageDescriptor getIcon() {
		return ResourceLoader.getIconDescriptor(ATTRIBUTE_PARENT_ICON);
	}
	
	/**
	 * @param resourceValue
	 * @param parentValue
	 */
	public ProjectAttributeRootNode(IParentNode parentValue) {
		super(null, parentValue);
		this.setLabel("Attributes");
		attributesMap = new HashMap<Attribute, IObservableList<List<Attribute>, Attribute>>();
		rootAttributesList = new ObservableList<List<Attribute>, Attribute>(new ArrayList<Attribute>());
		eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();		
	}
	

	private void addObservables(Attribute attribute) {
		attributesMap.put(attribute, new ObservableList<List<Attribute>,Attribute>(attribute.getChildren()));
		for(Attribute attributeToAdd : attribute.getChildren()) {
			attributesMap.put(attributeToAdd, new ObservableList<List<Attribute>,Attribute>(attributeToAdd.getChildren()));
			addObservables(attributeToAdd);
		}
	}

	public void loadChildAttributeNodes() {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			TypedQuery<Attribute> query = entityManager.createQuery("select a from Attribute a where a.parent is null", Attribute.class);
			List<Attribute> result = query.getResultList();
			if (result.size() > 0) {
				for(Attribute attribute : result) {
					addObservables(attribute);
					rootAttributesList.add(attribute);
					AttributeNode attributeNode = new AttributeNode(attribute, this);
					_children.add(attributeNode);
					map.put(attribute, attributeNode);
				}
				this.fireChildrenChanged();
			}
			rootAttributesList.addChangeListener(this);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	public List<Attribute> getAttributesList(Attribute parentAttribute) {
		if (parentAttribute == null) 
			return rootAttributesList.getReadOnlyList();
		return attributesMap.get(parentAttribute).getReadOnlyList();
	}
	
	public void addAttributeListChangeListener(CollectionChangeListener listener, Attribute parentAttribute) {
		//TODO throws exception
		IObservableList<List<Attribute>, Attribute> tempList = attributesMap.get(parentAttribute);
		if (tempList!=null) 
			tempList.addChangeListener(listener);
	}
	
	public Attribute getAttribute(Attribute attribute) {
		for (Attribute listAttribute : attributesMap.keySet()) {
			if (listAttribute.equals(attribute))
				return listAttribute;
		}
		return null;
	}
	
	public void removeAttributeListChangeListener(CollectionChangeListener listener, Attribute parentAttribute) {
		//TODO throws exception
		IObservableList<List<Attribute>, Attribute> tempList = attributesMap.get(parentAttribute);
		if (tempList!=null) 
			tempList.removeChangeListener(listener);
	}
	
	public void removeAttribute(Attribute attributeValue, Attribute parentAttribute) throws Exception {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			if (parentAttribute!=null) {
				attributesMap.get(parentAttribute).remove(attributeValue);
				entityManager.merge(parentAttribute);
			} else {
				Attribute attributeToDelete = entityManager.merge(attributeValue);
				entityManager.remove(attributeToDelete);
			}
			entityManager.getTransaction().commit();
			if (parentAttribute!=null) {
				attributesMap.remove(attributeValue);
			} else {
				rootAttributesList.remove(attributeValue);
			}
		} catch (Exception ex) {
			if (parentAttribute!=null) {
				attributesMap.get(parentAttribute).add(attributeValue);
			}
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			throw new Exception("This attribute may be referenced by others.");
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	

	public void addAttribute(Attribute attributeValue, Attribute parentAttribute) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			
			if (parentAttribute != null) {
				attributeValue.setParent(parentAttribute);
				Attribute parent = entityManager.find(Attribute.class, parentAttribute.getId());
				parent.getChildren().add(attributeValue);
				entityManager.merge(parent);
			} else {
				attributeValue.setParent(null);
				entityManager.persist(attributeValue);
			}
			entityManager.getTransaction().commit();
			attributesMap.put(attributeValue, new ObservableList<List<Attribute>,Attribute>(attributeValue.getChildren()));
			if (parentAttribute != null) {
				attributesMap.get(parentAttribute).add(attributeValue);
			} else {
				rootAttributesList.add(attributeValue);
			}
		} catch (Exception ex) {
			
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}
	
	/**
	 * 
	 */
	public void updateResource(Attribute attribute) {
		EntityManager entityManager = null;
		try {
			entityManager = eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.merge(attribute);
			entityManager.getTransaction().commit();
			this.getViewerProvider().getTreeViewer().refresh(this, true);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.IParentNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		rootAttributesList.removeChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see synergyviewcommons.collections.ListChangeListener#listChanged(synergyviewcommons.collections.ListChangeEvent)
	 */
	public void listChanged(CollectionChangeEvent event) {
		for(CollectionDiffEntry<?> entry : event.getListDiff().getDifferences()) {
			Attribute attribute = (Attribute) entry.getElement();
			if (entry.isAddition()) {
				AttributeNode attributeNode = new AttributeNode(attribute, this);
				_children.add(attributeNode);
				map.put(attribute, attributeNode);
			} else {
				this.deleteChildren(new INode[]{map.get(attribute)});
				map.remove(attribute);
			}
		}
		this.fireChildrenChanged();
		if (_children.size() == 0 || _children.size() == 1)
			this.getViewerProvider().getTreeViewer().refresh(this, true);
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.IViewerProvider#getTreeViewer()
	 */
	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
	
	public void setTreeViewer(TreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}
	
	/**
	 * @param name
	 * @return
	 */
	public Attribute getAttributeByName(String name) {
		for (Attribute listAttribute : attributesMap.keySet()) {
			if (listAttribute.getName().equals(name))
				return listAttribute;
		}
		return null;

	}
}
