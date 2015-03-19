/**
  *  File: AbstractMediaPreviewControl.java
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

package synergyviewmvc.media.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import synergyviewmvc.media.model.AbstractMedia;

/**
 * @author phyo
 *
 */
public class MediaPreviewControl extends Composite {


	private Map<AbstractMedia, MediaSwtAwtComposite> mediaSwtAwtCompositeList = new HashMap<AbstractMedia, MediaSwtAwtComposite>();
	private IObservableMap observableMediaPreviewMap;
	private IMapChangeListener mapChangedListener;
	
	public IObservableMap getObservableMediaPreviewList() {
		return observableMediaPreviewMap;
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public MediaPreviewControl(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout(SWT.HORIZONTAL));
		this.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				disposeResourse();
			}
			
		});
		observableMediaPreviewMap = new WritableMap(SWTObservables.getRealm(parent.getDisplay()), String.class, AbstractMedia.class);
		mapChangedListener = new IMapChangeListener() {

			public void handleMapChange(MapChangeEvent arg0) {
				for (Object key : arg0.diff.getAddedKeys()) {
					MediaSwtAwtComposite mediaComposite = new MediaSwtAwtComposite(MediaPreviewControl.this, SWT.None);
					mediaComposite.addMedia((AbstractMedia) observableMediaPreviewMap.get(key));
					mediaSwtAwtCompositeList.put((AbstractMedia) observableMediaPreviewMap.get(key), mediaComposite);
				}
				for (Object key : arg0.diff.getRemovedKeys()) {
					AbstractMedia media = (AbstractMedia) arg0.diff.getOldValue(key);
					mediaSwtAwtCompositeList.get(media).dispose();
					mediaSwtAwtCompositeList.remove(media);
				}
				MediaPreviewControl.this.layout(true);
				
			}
			
		};
		
		observableMediaPreviewMap.addMapChangeListener(mapChangedListener);
    }
	
	private void disposeResourse() {
		observableMediaPreviewMap.clear();
		observableMediaPreviewMap.removeMapChangeListener(mapChangedListener);
	}
}
