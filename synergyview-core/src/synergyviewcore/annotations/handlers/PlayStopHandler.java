/**
 * File: PlayStopHandler.java Copyright (c) 2010 phyokyaw This program is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */

package synergyviewcore.annotations.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import synergyviewcore.annotations.ui.editors.CollectionMediaClipAnnotationEditor;

/**
 * The Class PlayStopHandler.
 * 
 * @author phyokyaw
 */
public class PlayStopHandler extends AbstractHandler implements IHandler {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands. ExecutionEvent)
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
	IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	IWorkbenchPage page = window.getActivePage();
	if (page.getActiveEditor() instanceof CollectionMediaClipAnnotationEditor) {
	    CollectionMediaClipAnnotationEditor annotationEditor = (CollectionMediaClipAnnotationEditor) page.getActiveEditor();
	    if (!annotationEditor.getAnnotationMediaControl().isPlaying()) {
		annotationEditor.getAnnotationMediaControl().setPlaying(true);
	    } else {
		annotationEditor.getAnnotationMediaControl().setPlaying(false);
	    }
	}
	return null;
    }

}
