package synergyviewmvc.timebar.action;

import java.util.List;

import org.eclipse.jface.action.Action;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.ISelectionRectListener;
import de.jaret.util.ui.timebars.model.TBRect;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public abstract class BaseTimeBarAction extends Action implements ISelectionRectListener{

	protected TimeBarViewer _tbv;
	
	public BaseTimeBarAction(TimeBarViewer _tbv) {
		this._tbv = _tbv;
        this._tbv.addSelectionRectListener(this);    
        init();
	}
	
	protected abstract void init();
	
	public void regionRectChanged(TimeBarViewerDelegate arg0, TBRect arg1) {
		
	}

	public void regionRectClosed(TimeBarViewerDelegate arg0) {
		
	}

	public void selectionRectChanged(TimeBarViewerDelegate arg0,
			JaretDate arg1, JaretDate arg2, List<TimeBarRow> arg3) {
		
	}

	public void selectionRectClosed(TimeBarViewerDelegate arg0) {
		
	}

}
