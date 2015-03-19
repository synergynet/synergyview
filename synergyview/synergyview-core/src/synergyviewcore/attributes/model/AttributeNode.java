package synergyviewcore.attributes.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;

import synergyviewcommons.collections.CollectionChangeEvent;
import synergyviewcommons.collections.CollectionChangeListener;
import synergyviewcommons.collections.CollectionDiffEntry;
import synergyviewcore.navigation.model.AbstractParent;
import synergyviewcore.navigation.model.DisposeException;
import synergyviewcore.navigation.model.INode;
import synergyviewcore.navigation.model.IParentNode;
import synergyviewcore.projects.model.ProjectNode;
import synergyviewcore.resource.ResourceLoader;

public class AttributeNode extends AbstractParent<Attribute> implements CollectionChangeListener {

	public static final String ATTRIBUTE_PARENT_ICON = "folder_table.png";
	public static final String ATTRIBUTE_ICON = "table.png";
	private ProjectAttributeRootNode rootNode;
	private Map<Attribute, AttributeNode> map = new HashMap<Attribute, AttributeNode>();
	
	@Override
	public ImageDescriptor getIcon() {
		if (_children.size()>0)
			return ResourceLoader.getIconDescriptor(ATTRIBUTE_PARENT_ICON);
		else return ResourceLoader.getIconDescriptor(ATTRIBUTE_ICON);
	}

	public AttributeNode(Attribute attributeValue, IParentNode parentValue) {
		super(attributeValue, parentValue);
		if (attributeValue!=null)
			this.setLabel(attributeValue.getName());
		loadChildAttributeNodes();
	}
	
	public ProjectAttributeRootNode getProjectAttributeRootNode() {
		return rootNode;
	}

	private void loadChildAttributeNodes() {
		rootNode = ((ProjectNode) this.getLastParent()).getProjectAttributeRootNode();
		rootNode.addAttributeListChangeListener(this, this.resource);
		List<Attribute> result = rootNode.getAttributesList(this.resource);
		if (result.size() > 0) {
			for(Attribute attribute : result) {
				AttributeNode attributeNode = new AttributeNode(attribute, this);
				_children.add(attributeNode);
				map.put(attribute, attributeNode);
			}
			this.fireChildrenChanged();
		}
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.navigation.model.INode#dispose()
	 */
	public void dispose() throws DisposeException {
		this.deleteChildren(this._children.toArray(new INode[]{}));
		rootNode.removeAttributeListChangeListener(this, resource);
	}

	/* (non-Javadoc)
	 * @see synergyviewcore.attributes.model.IAttributeNode#getChildrenNames()
	 */
	public List<String> getChildrenNames() {
		List<String> nameList = new ArrayList<String>();
		for (INode attributeNode : _children) {
			nameList.add(((AttributeNode) attributeNode).getResource().getName());
		}
		return nameList;
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


}
