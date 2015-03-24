package synergyviewcore.timebar.action;

import java.util.List;

import org.eclipse.jface.action.Action;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarViewerDelegate;
import de.jaret.util.ui.timebars.model.ISelectionRectListener;
import de.jaret.util.ui.timebars.model.TBRect;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;


/**
 * The Class BaseTimeBarAction.
 */
public abstract class BaseTimeBarAction extends Action implements ISelectionRectListener{

	/** The _tbv. */
	protected TimeBarViewer _tbv;
	
	/**
	 * Instantiates a new base time bar action.
	 *
	 * @param _tbv the _tbv
	 */
	public BaseTimeBarAction(TimeBarViewer _tbv) {
		this._tbv = _tbv;
        this._tbv.addSelectionRectListener(this);    
        init();
	}
	
	/**
	 * Inits the.
	 */
	protected abstract void init();
	
	/* (non-Javadoc)
	 * @see de.jaret.util.ui.timebars.model.ISelectionRectListener#regionRectChanged(de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.ui.timebars.model.TBRect)
	 */
	public void regionRectChanged(TimeBarViewerDelegate arg0, TBRect arg1) {
		
	}

	/* (non-Javadoc)
	 * @see de.jaret.util.ui.timebars.model.ISelectionRectListener#regionRectClosed(de.jaret.util.ui.timebars.TimeBarViewerDelegate)
	 */
	public void regionRectClosed(TimeBarViewerDelegate arg0) {
		
	}

	/* (non-Javadoc)
	 * @see de.jaret.util.ui.timebars.model.ISelectionRectListener#selectionRectChanged(de.jaret.util.ui.timebars.TimeBarViewerDelegate, de.jaret.util.date.JaretDate, de.jaret.util.date.JaretDate, java.util.List)
	 */
	public void selectionRectChanged(TimeBarViewerDelegate arg0,
			JaretDate arg1, JaretDate arg2, List<TimeBarRow> arg3) {
		
	}

	/* (non-Javadoc)
	 * @see de.jaret.util.ui.timebars.model.ISelectionRectListener#selectionRectClosed(de.jaret.util.ui.timebars.TimeBarViewerDelegate)
	 */
	public void selectionRectClosed(TimeBarViewerDelegate arg0) {
		
	}

}
