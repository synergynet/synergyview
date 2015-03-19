package synergyviewmvc.timebar.component;

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

import synergyviewmvc.resource.ResourceLoader;

public class MediaControlBar extends Composite {

	@Override
	public void dispose() {
		resourceManager.dispose();
		super.dispose();
	}

	protected List<MediaControlListener> listeners = new ArrayList<MediaControlListener>();
	protected LocalResourceManager resourceManager = new LocalResourceManager(JFaceResources.getResources());
	protected Button playButton;
	protected Button muteButton;
	protected Button lockButton;
	
		
	public MediaControlBar(Composite parent, int style) {
		super(parent, style);
		this.createControls(parent);
	}
		
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
	
	
	public void addMediaControlListener(MediaControlListener mediaControlListener){
		listeners.add(mediaControlListener);
	}
	
	public interface MediaControlListener {
		public void play();
		public void stop();
		public void setMute(boolean mute);
		public void setLock(boolean lock);
	}
	

}
