/**
 *  File: AnotherLabelProvider.java
 *  Copyright (c) 2011
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

package synergyviewcore.navigation;

import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import uk.ac.durham.tel.commons.jface.node.AbstractBaseNode;
import uk.ac.durham.tel.commons.jface.node.INode;

/**
 * @author phyo
 *
 */
public class ObservableNavigatorLabelProvider extends ObservableMapLabelProvider implements IStyledLabelProvider {
	private LocalResourceManager resourceManager = new LocalResourceManager(
			JFaceResources.getResources());

	/**
	 * @param attributeMap
	 */
	public ObservableNavigatorLabelProvider(IObservableSet knownElements) {
		super(Properties.observeEach(knownElements, BeanProperties
				.values(new String[] { AbstractBaseNode.PROP_LABEL, AbstractBaseNode.PROP_LABEL_DECORATOR, AbstractBaseNode.PROP_ICON_DECORATOR }))); 
	}
	
	
	
	public Image getImage(Object element) {
		if (element instanceof INode) {
			return (Image) resourceManager.get(((INode) element).getIcon());
		}
		return null;
	}
	
	public void dispose() {
		resourceManager.dispose();
		super.dispose();

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider#getStyledText(java.lang.Object)
	 */
	public StyledString getStyledText(Object element) {
		StyledString styledString = new StyledString();
		if (!(element instanceof INode))
			return styledString.append("Unknown");
		INode node = (INode) element;
		styledString.append(node.getLabel());
		if (node.getLabelDecorator()!=null && !node.getLabelDecorator().isEmpty())
			styledString.append(String.format(" (%s)",node.getLabelDecorator()), StyledString.COUNTER_STYLER);
		return styledString;
	}

}
