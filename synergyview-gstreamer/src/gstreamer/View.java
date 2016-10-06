package gstreamer;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.gstreamer.Gst;
import org.gstreamer.State;
import org.gstreamer.elements.PlayBin;
import org.gstreamer.swt.VideoComponent;

public class View extends ViewPart {
	public static final String ID = "gstreamer.view";

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		Gst.init();
		final PlayBin playbin = new PlayBin("VideoPlayer");
        playbin.setInputFile(new File("/Users/phyo/Desktop/AB.mov"));
        VideoComponent vc = new VideoComponent(parent,SWT.EMBEDDED);
        vc.showFPS(true);
        playbin.setVideoSink(vc.getElement());
        playbin.setState(State.PLAYING);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {

	}
}