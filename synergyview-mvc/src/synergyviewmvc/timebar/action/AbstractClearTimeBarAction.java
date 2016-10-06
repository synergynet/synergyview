package synergyviewmvc.timebar.action;

import org.eclipse.swt.SWT;

import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public abstract class AbstractClearTimeBarAction extends BaseTimeBarAction {

    public AbstractClearTimeBarAction(TimeBarViewer tbv) {
    	 super(tbv);              
    }

    /**
     * {@inheritDoc}
     */
    public void run() {
         	
    	 	
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return "Clear";
    }


	@Override
	protected void init() {
		this.setEnabled(true);
        this.setAccelerator(SWT.DEL);
        this.setToolTipText("Clear time bar");		
	}

}
