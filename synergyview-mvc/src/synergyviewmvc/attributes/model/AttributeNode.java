package synergyviewmvc.attributes.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewmvc.navigation.model.AbstractParent;
import synergyviewmvc.resource.ResourceLoader;
import uk.ac.durham.tel.commons.jface.node.DisposeException;
import uk.ac.durham.tel.commons.jface.node.INode;
import uk.ac.durham.tel.commons.jface.node.IParentNode;
import uk.ac.durham.tel.commons.jface.node.NodeRemoveException;

public class AttributeNode extends AbstractParent<Attribute> implements IAttributeNode {

	public static final String ATTRIBUTE_PARENT_ICON = "folder_table.png";
	public static final String ATTRIBUTE_ICON = "table.png";
	protected EntityManagerFactory _eManagerFactory;

	@Override
	public ImageDescriptor getIcon() {
		if (children.size()>0)
			return ResourceLoader.getIconDescriptor(ATTRIBUTE_PARENT_ICON);
		else return ResourceLoader.getIconDescriptor(ATTRIBUTE_ICON);
	}

	public AttributeNode(Attribute attributeValue, IParentNode parentValue) {
		super(attributeValue, parentValue);
		if (attributeValue!=null)
			this.setLabel(attributeValue.getName());
		_eManagerFactory = this.getEMFactoryProvider().getEntityManagerFactory();
		loadChildAttributeNodes();
	}


	public void addChildAttribute(Attribute attributeValue) {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			if (resource!=null) {
				resource.getChildren().add(attributeValue);
				attributeValue.setParent(resource);
			}
			entityManager.persist(attributeValue);
			entityManager.getTransaction().commit();
			AttributeNode node = new AttributeNode(attributeValue, this);
			children.add(node);
			this.fireChildrenChanged();
			if (children.size() == 0 || children.size() == 1)
				this.getViewerProvider().getTreeViewer().update(this, null);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}


	protected void loadChildAttributeNodes() {
		List<Attribute> result = resource.getChildren();
		if (result.size() > 0) {
			for(Attribute attribute : result) {
				AttributeNode attributeNode = new AttributeNode(attribute, this);
				children.add(attributeNode);
			}
			this.fireChildrenChanged();
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		EntityManager entityManager = null;
		try {
			this.deleteChildren(this.children.toArray(new INode[]{}));
			try {
				entityManager = _eManagerFactory.createEntityManager();
				entityManager.getTransaction().begin();

				Attribute attributeToDelete = entityManager.merge(resource);
				entityManager.remove(attributeToDelete);
				entityManager.getTransaction().commit();
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new DisposeException("This attribute may be referenced by others.");
			} finally {
				if (entityManager.isOpen())
					entityManager.close();
			}
		} catch (NodeRemoveException e) {
			e.printStackTrace();
			throw new DisposeException("Unable to remove the Attribute node.", e);
		}
	}

	public void removeChildAttributeNode(AttributeNode nodeValue) throws Exception {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			if (resource!=null) {
				resource.getChildren().remove(nodeValue.getResource());
			}
			Attribute attributeToDelete = entityManager.merge(nodeValue.getResource());
			entityManager.remove(attributeToDelete);
			entityManager.getTransaction().commit();
			this.deleteChildren(new INode[]{nodeValue});
			if (children.size() == 0 || children.size() == 1)
				this.getViewerProvider().getTreeViewer().update(this, null);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new Exception("This attribute may be referenced by others.");
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}

	}

	/* (non-Javadoc)
	 * @see uk.ac.durham.tel.synergynet.ats.attributes.model.IAttributeNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		List<String> nameList = new ArrayList<String>();
		for (INode attributeNode : children) {
			nameList.add(((AttributeNode) attributeNode).getResource().getName());
		}
		return nameList;
	}

	/**
	 * 
	 */
	public void updateResource() {
		EntityManager entityManager = null;
		try {
			entityManager = _eManagerFactory.createEntityManager();
			entityManager.getTransaction().begin();
			entityManager.merge(this.getResource());
			entityManager.getTransaction().commit();
			this.setLabel(resource.getName());
			this.getViewerProvider().getTreeViewer().refresh(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (entityManager.isOpen())
				entityManager.close();
		}
	}

}
