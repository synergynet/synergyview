package synergyviewcore.timebar.action;

import org.eclipse.swt.SWT;

import de.jaret.util.ui.timebars.swt.TimeBarViewer;

/**
 * The Class AbstractClearTimeBarAction.
 */
public abstract class AbstractClearTimeBarAction extends BaseTimeBarAction {

    /**
     * Instantiates a new abstract clear time bar action.
     * 
     * @param tbv
     *            the tbv
     */
    public AbstractClearTimeBarAction(TimeBarViewer tbv) {
	super(tbv);
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
	return "Clear";
    }

    /*
     * (non-Javadoc)
     * 
     * @see synergyviewcore.timebar.action.BaseTimeBarAction#init()
     */
    @Override
    protected void init() {
	this.setEnabled(true);
	this.setAccelerator(SWT.DEL);
	this.setToolTipText("Clear time bar");
    }

    /**
     * {@inheritDoc}
     */
    public void run() {

    }

}
