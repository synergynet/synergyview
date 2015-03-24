package synergyviewcore.timebar.action;

import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * The Class ViewPropertyAction.
 */
public class ViewPropertyAction extends BaseTimeBarAction {
	
	/**
	 * Instantiates a new view property action.
	 * 
	 * @param tbv
	 *            the tbv
	 */
	public ViewPropertyAction(TimeBarViewer tbv) {
		super(tbv);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getText() {
		return "Property";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
	 */
	@Override
	protected void init() {
		this.setEnabled(true);
		this.setToolTipText("View the property");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void run() {
		
	}
	
}
