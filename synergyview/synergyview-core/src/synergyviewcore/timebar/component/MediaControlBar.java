package synergyviewcore.timebar.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import synergyviewcore.resource.ResourceLoader;


/**
 * The Class MediaControlBar.
 */
public class MediaControlBar extends Composite {

	/* (non-Javadoc)
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		resourceManager.dispose();
		super.dispose();
	}

	/** The listeners. */
	protected List<MediaControlListener> listeners = new ArrayList<MediaControlListener>();
	
	/** The resource manager. */
	protected LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	
	/** The play button. */
	protected Button playButton;
	
	/** The mute button. */
	protected Button muteButton;
	
	/** The lock button. */
	protected Button lockButton;
	
		
	/**
	 * Instantiates a new media control bar.
	 *
	 * @param parent the parent
	 * @param style the style
	 */
	public MediaControlBar(Composite parent, int style) {
		super(parent, style);
		this.createControls(parent);
	}
		
	/**
	 * Creates the controls.
	 *
	 * @param parent the parent
	 */
	protected void createControls(Composite parent){
		GridData gd = new GridData();
		gd.widthHint = 50;
		gd.verticalAlignment = SWT.FILL;
	    this.setLayoutData(gd);
	    GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 1;
	    this.setLayout(gridLayout);
		
	    playButton = new Button(this, SWT.TOGGLE);
	    gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.LEFT;
		playButton.setLayoutData(gd);
		playButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("control_play_blue.png")));
		playButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {			
				if (playButton.getSelection()) {
					playButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("control_pause_blue.png")));
					for (MediaControlListener l: listeners)
						l.play();
				} else {
					playButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("control_play_blue.png")));
					for (MediaControlListener l: listeners)
						l.stop();
				}
			}
		});  
		
		muteButton = new Button(this, SWT.TOGGLE);
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.LEFT;
		muteButton.setLayoutData(gd);
		muteButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("sound.png")));
		muteButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (muteButton.getSelection()) {
					muteButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("sound_mute.png")));
				} else {
					muteButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("sound.png")));
				}
				
				for (MediaControlListener l: listeners)
					l.setMute(muteButton.getSelection());
			}
		}); 
		
		lockButton = new Button(this, SWT.TOGGLE);
		gd = new GridData();
		gd.verticalAlignment = SWT.TOP;
		gd.horizontalAlignment = SWT.LEFT;
		lockButton.setLayoutData(gd);
		lockButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("lock_open.png")));
		lockButton.setSelection(true);
		lockButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (lockButton.getSelection()) {
					lockButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("lock.png")));
				} else {
					lockButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("lock_open.png")));
				}
				
				for (MediaControlListener l: listeners)
					l.setLock(lockButton.getSelection());
			}
		}); 
	}
	
	/**
	 * Sets the play button enabled.
	 *
	 * @param enabled the new play button enabled
	 */
	public void setPlayButtonEnabled(boolean enabled){
		if (enabled) {
			playButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("control_play_blue.png")));
			playButton.setSelection(false);
		}
		else {
			playButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("control_pause_blue.png")));
			playButton.setSelection(true);
		}
	}
	
	/**
	 * Sets the lock button enabled.
	 *
	 * @param enabled the new lock button enabled
	 */
	public void setLockButtonEnabled(boolean enabled){
		if (enabled) {
			lockButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("lock.png")));
			lockButton.setSelection(true);
		}
		else {
			lockButton.setImage((Image) resourceManager.get(ResourceLoader.getIconDescriptor("lock_open.png")));
			lockButton.setSelection(false);
		}
	}
	
	
	/**
	 * Adds the media control listener.
	 *
	 * @param mediaControlListener the media control listener
	 */
	public void addMediaControlListener(MediaControlListener mediaControlListener){
		listeners.add(mediaControlListener);
	}
	
	/**
	 * The listener interface for receiving mediaControl events.
	 * The class that is interested in processing a mediaControl
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addMediaControlListener<code> method. When
	 * the mediaControl event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see MediaControlEvent
	 */
	public interface MediaControlListener {
		
		/**
		 * Play.
		 */
		public void play();
		
		/**
		 * Stop.
		 */
		public void stop();
		
		/**
		 * Sets the mute.
		 *
		 * @param mute the new mute
		 */
		public void setMute(boolean mute);
		
		/**
		 * Sets the lock.
		 *
		 * @param lock the new lock
		 */
		public void setLock(boolean lock);
	}
	

}
