package synergyviewmvc.timebar.action;

import de.jaret.util.ui.timebars.swt.TimeBarViewer;

public class ViewPropertyAction  extends BaseTimeBarAction {

    public ViewPropertyAction(TimeBarViewer tbv) {
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
        return "Property";
    }

	@Override
	protected void init() {
		this.setEnabled(true);
        this.setToolTipText("View the property");	
	}

}
