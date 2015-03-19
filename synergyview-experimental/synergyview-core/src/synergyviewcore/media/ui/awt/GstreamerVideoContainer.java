package synergyviewcore.media.ui.awt;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JApplet;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;


public class GstreamerVideoContainer extends Composite {
	private Frame frame;
	private Container contentPane;
	private static Logger logger = Logger.getLogger(GstreamerVideoContainer.class);
	public GstreamerVideoContainer(Composite parent, int style) {
		super(parent, style);
		this.setLayout(new FillLayout());
		Composite swtAwtComposite = new Composite (this, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		createAwtVideoFrame(swtAwtComposite);
		this.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				disposeResources();
			}
			
		});
	}

	protected void disposeResources() {
		if (frame!=null) {
			EventQueue.invokeLater(new Runnable () {
				public void run() {
					frame.dispose();
					logger.debug("awt frame disposed");
				}
			});
		}
	}

	private void createAwtVideoFrame(Composite swtAwtComposite) {
		frame = SWT_AWT.new_Frame(swtAwtComposite);
		JApplet rootVideoContainer = new JApplet();
		frame.add(rootVideoContainer);
		rootVideoContainer.setBackground(Color.BLACK);
		contentPane = rootVideoContainer.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.LINE_AXIS));
		contentPane.setBackground(Color.BLACK);
	}

	public VideoWindow createVideoWindow() {
		VideoWindow window = new VideoWindow();
		contentPane.add(window);
		return window;
	}
	
	public void deleteVideoWindow(VideoWindow window) {
		contentPane.remove(window);
	}

}
