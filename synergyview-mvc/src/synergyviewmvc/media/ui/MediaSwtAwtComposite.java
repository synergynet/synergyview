package synergyviewmvc.media.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import synergyviewmvc.media.model.AbstractMedia;
import synergyviewmvc.resource.ResourceLoader;

public class MediaSwtAwtComposite extends Composite {

	private AbstractMedia media;
	private Composite videoPreviewContainer;
	private Label videoDuration;
	private Frame awtFrame;
	private Composite video_SWT_AWT_container;
	private static final int PADDING = 5;
	private static final int MEDIA_DURATION_LABEL_HEIGHT = 20;

	public MediaSwtAwtComposite(Composite parent, int style) {
		super(parent, style);
		setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		GridLayout layout = new GridLayout(1, false);
		setLayout(layout);


		videoPreviewContainer = new Composite(this,SWT.NONE);
		videoPreviewContainer.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		videoPreviewContainer.setLayoutData(data);
		videoPreviewContainer.setLayout(new GridLayout(1,false));
		videoPreviewContainer.addControlListener(new ControlListener(){
			public void controlMoved(ControlEvent e) {
				//
			}

			public void controlResized(ControlEvent e) {
				if (awtFrame!=null) {
					GridData swtAwtGridData = (GridData) video_SWT_AWT_container.getLayoutData();
					Dimension d = calculateVideoSize(new Dimension(videoPreviewContainer.getBounds().width, videoPreviewContainer.getBounds().height));
					swtAwtGridData.heightHint = (int) d.getHeight();
					swtAwtGridData.widthHint = (int) d.getWidth();
					videoPreviewContainer.layout();
				}
			}
		});
		video_SWT_AWT_container = new Composite(videoPreviewContainer, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		GridData swtAwtGridData = new GridData();
		swtAwtGridData.horizontalAlignment = SWT.FILL;
		swtAwtGridData.grabExcessHorizontalSpace = true;  
		swtAwtGridData.verticalAlignment = SWT.FILL;
		swtAwtGridData.grabExcessVerticalSpace = true;
		video_SWT_AWT_container.setLayoutData(swtAwtGridData);
		videoDuration = new Label(this,SWT.SHADOW_IN);
		data = new GridData();
		data.horizontalAlignment = GridData.CENTER;
		data.grabExcessHorizontalSpace = true;
		data.heightHint = MEDIA_DURATION_LABEL_HEIGHT;
		videoDuration.setLayoutData(data);
		videoDuration.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		videoDuration.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		videoDuration.setText("No media available.");
	}

	private void updateTimes() {
		Display.getDefault().syncExec(new Runnable(){
			public void run() {
				if (MediaSwtAwtComposite.this.isDisposed())
					return;
				videoDuration.setText(String.format("%s/%s",media.getFormattedTime(),media.getFormattedDuration()));
			}
		});
	}

	public void addMedia(AbstractMedia mediaValue) {
		removeMedia();

		this.media = mediaValue;
		this.media.addPropertyChangeListener(AbstractMedia.PROP_TIME, new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				updateTimes();
			}

		});
		updateTimes();

		awtFrame = SWT_AWT.new_Frame(video_SWT_AWT_container);
		Panel awtPanel = new Panel();
		awtPanel.setLayout(new BorderLayout());
		awtPanel.add(this.media.getUIComponent(), BorderLayout.CENTER);
		awtFrame.add(awtPanel);
		GridData swtAwtGridData = (GridData) video_SWT_AWT_container.getLayoutData();
		Dimension d = calculateVideoSize(new Dimension(videoPreviewContainer.getBounds().width, videoPreviewContainer.getBounds().height));
		swtAwtGridData.widthHint = d.width;
		swtAwtGridData.heightHint = d.height;
		swtAwtGridData.horizontalAlignment = SWT.CENTER;
		swtAwtGridData.grabExcessHorizontalSpace = true;  
		swtAwtGridData.verticalAlignment = SWT.CENTER;
		swtAwtGridData.grabExcessVerticalSpace = true;
		videoPreviewContainer.layout();
		this.layout();
	}

	private void disposeVideoAwtFrame() {
		try {
			EventQueue.invokeAndWait(new Runnable () {
				public void run() {
					if (awtFrame!=null) {
						awtFrame.dispose();
						awtFrame = null; 
					}
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private Dimension calculateVideoSize(Dimension parentDimension) {
		Dimension d = media.getSize();
		final double movieratio = d.getHeight() / d.getWidth();  
		double windowratio =  (double) parentDimension.height / (double) parentDimension.width;
		if (windowratio < movieratio) {
			int width = (int) ((parentDimension.height * d.width) / d.height);
			return new Dimension(width - PADDING, parentDimension.height - PADDING);
		} else {
			int height = (int) ((parentDimension.width * d.height) / d.width);
			return new Dimension(parentDimension.width - PADDING, height - PADDING);
		}
	}

	public void removeMedia() {
		if (media!=null) {
			media.dispose();
			media = null;
		}

		disposeVideoAwtFrame();
		if (!video_SWT_AWT_container.isDisposed()) {
			GridData swtAwtGridData = (GridData) video_SWT_AWT_container.getLayoutData();
			swtAwtGridData.horizontalAlignment = SWT.FILL;
			swtAwtGridData.grabExcessHorizontalSpace = true;  
			swtAwtGridData.verticalAlignment = SWT.FILL;
			swtAwtGridData.grabExcessVerticalSpace = true;
			videoDuration.setText(ResourceLoader.getString("MEDIA_PREVIEW_NO_MEDIA_MESSAGE"));
			videoPreviewContainer.layout(true);
			this.layout(true);
			this.redraw();
			this.update();
		}
	}

	@Override
	public void dispose() {
		removeMedia();
		super.dispose();
	}

}
