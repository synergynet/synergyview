package synergyviewcore.media.model;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWTException;

import quicktime.QTException;
import quicktime.app.view.GraphicsImporterDrawer;
import quicktime.app.view.QTFactory;
import quicktime.app.view.QTImageProducer;
import quicktime.io.QTFile;
import quicktime.qd.Pict;
import quicktime.qd.QDColor;
import quicktime.qd.QDDimension;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.clocks.RateCallBack;
import quicktime.std.clocks.TimeRecord;
import quicktime.std.image.GraphicsImporter;
import quicktime.std.image.Matrix;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.TimeInfo;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.DataRef;
import quicktime.std.movies.media.Media;
import quicktime.std.movies.media.TimeCodeMedia;
import quicktime.std.qtcomponents.TCTextOptions;
import quicktime.std.qtcomponents.TimeCodeDef;
import quicktime.std.qtcomponents.TimeCodeDescription;
import quicktime.std.qtcomponents.TimeCodeTime;
import quicktime.std.qtcomponents.TimeCoder;
import quicktime.util.QTHandle;
import synergyviewcore.Activator;

public class QuickTimeMedia extends AbstractMedia {
	private Movie movie;
	private Dimension movieDimension;
	private MovieController movieController;
	private int currentTimeInMilli;
	private float savedVolume = 0f;
	private PlayRate _currentPlayRate = PlayRate.X1;
	private boolean _isPlaying = false;
	private QTRateCallback _rateCallBack;
	private TimeBaseTimeCallBack _theMoviesTimeCallback;
	private final ILog logger;
	public QuickTimeMedia(URI mediaUrl, String name) {
		super(mediaUrl, name);
		logger = Activator.getDefault().getLog();
		try {
			DataRef urlMovie = new DataRef(new QTFile(new File(mediaUrl)));
			movie = Movie.fromDataRef(urlMovie, StdQTConstants.newMovieActive);
			movieController = new MovieController(movie);
			movieDimension = new Dimension(movie.getBounds().getWidth(), movie
					.getBounds().getHeight());
			movie.setTimeScale(1000);
			_rateCallBack = new QTRateCallback(movie);
			_theMoviesTimeCallback = new TimeBaseTimeCallBack(movie,
					StdQTConstants.triggerTimeEither); // this callback is
			// triggered at a
			// specific time
			// interval
			_theMoviesTimeCallback.callMeWhen();
			this.name = name;
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	public void dispose() {		
		clearupCallbackListenerObjects();
	}

	public byte[] getPngImageByteArray() {
		try {
			// stop movie to take picture
			boolean wasPlaying = false;
			if (movie.getRate( ) > 0) {
				movie.stop( );
				wasPlaying = true;
			}

			// take a pict
			Pict pict = movie.getPict (movie.getTime( ));

			// add 512-byte header that pict would have as file
			byte[  ] newPictBytes =
				new byte [pict.getSize( ) + 512];
			pict.copyToArray (0,
					newPictBytes,
					512,
					newPictBytes.length - 512);
			pict = new Pict (newPictBytes);
			GraphicsImporter gi = new GraphicsImporter (StdQTConstants.kQTFileTypePicture);
			GraphicsImporterDrawer gid = new GraphicsImporterDrawer (gi);

			// export it
			DataRef ref = new DataRef (pict,
					StdQTConstants.kDataRefQTFileTypeTag,
			"PICT");
			gi.setDataReference (ref);
			QDRect rect = gi.getSourceRect ( );
			Dimension dim = new Dimension (rect.getWidth( ),
					rect.getHeight( ));
			QTImageProducer ip = new QTImageProducer(gid, dim);
			
			// convert from MoviePlayer to java.awt.Image
			Image image = Toolkit.getDefaultToolkit().createImage(ip);
			final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
			final Graphics2D g2 = buffImg.createGraphics();  
			g2.drawImage(image, null, null);  
			g2.dispose(); 
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(buffImg, "png", baos);

			// restart movie
			if (wasPlaying)
				movie.start( );
			return baos.toByteArray();
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
		return null;
	}

	public void setMute(boolean mute) {
		try {
			if (mute) {
				if (movie.getVolume() != 0f) {
					savedVolume = movie.getVolume();
					movieController.setVolume(0f);
				}
			} else {
				if (movie.getVolume() == 0f) {
					movieController.setVolume(savedVolume);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTime() {
		return currentTimeInMilli;
	}

	public Dimension getSize() {
		return movieDimension;
	}

	public void setTime(int time) {
		try {
			this.firePropertyChange(IMedia.PROP_TIME, currentTimeInMilli,
					currentTimeInMilli = time);
			movie.setTimeValue(time);
		} catch (StdQTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	public int getTimeScale() {
		try {
			return movie.getTimeScale();
		} catch (StdQTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return 0;
		}
	}

	public String getFormattedTime() {
		return getStringTimeFormat(currentTimeInMilli);
	}

	private String getStringTimeFormat(int time) {
		try {
			if (time == 0)
				return "00:00:00,000";
			Calendar currentTime = Calendar.getInstance();
			currentTime.setTimeInMillis(time);
			NumberFormat formatter = new DecimalFormat("00");
			NumberFormat miFormatter = new DecimalFormat("000");
			return String.format("%s:%s:%s,%s", formatter.format(currentTime
					.get(Calendar.HOUR_OF_DAY) - 1), formatter
					.format(currentTime.get(Calendar.MINUTE)), formatter
					.format(currentTime.get(Calendar.SECOND)), miFormatter
					.format(currentTime.get(Calendar.MILLISECOND)));
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.WARNING,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return "00:00:00,000";
		}
	}

	public int getDuration() {
		try {
			return movie.getDuration();
		} catch (StdQTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return 0;
		}
	}

	public void setPlaying(boolean playingValue) {
		try {
			if (playingValue && !_isPlaying) {
				movie.setRate(1.0f);
			} else {
				if (_isPlaying) {
					stopMovie();
				}
			}
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	private void clearupCallbackListenerObjects() {
		if (_theMoviesTimeCallback != null) {
			try{
				_theMoviesTimeCallback.cancelAndCleanup();
				_theMoviesTimeCallback = null;
			}catch(Exception e){}
		}
		if (_rateCallBack != null)  {
			try{
				_rateCallBack.cancelAndCleanup();
				_rateCallBack = null;
			}catch(Exception e){}

		}

	}

	public Component getUIComponent() {
		try {
			return QTFactory.makeQTComponent(movie).asComponent();
		} catch (QTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return null;
		}
	}
	

	class QTRateCallback extends RateCallBack {
		public QTRateCallback(Movie m) throws QTException {
			super(m.getTimeBase(), 0.0f, StdQTConstants.triggerRateChange);
			callMeWhen();
		}

		public void execute() {

			if (rateWhenCalled == 0.0) {
				if (_isPlaying) {
					QuickTimeMedia.this.firePropertyChange(IMedia.PROP_PLAYING, _isPlaying,
							_isPlaying = false);
				}
			} else if (rateWhenCalled > 0.0) {
				if (!_isPlaying)
					QuickTimeMedia.this.firePropertyChange(IMedia.PROP_PLAYING, _isPlaying, _isPlaying = true);
			}
			// indicate that we want to be called again
			try {
				cancel();
				callMeWhen();
			} catch (QTException ex) {
				IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
				logger.log(status);
			}
		}
	}

	/**
	 * This class extends the TimeCallBack class to provide a method that is
	 * called when a specific interval of time elapses.
	 */
	private class TimeBaseTimeCallBack extends
	quicktime.std.clocks.TimeCallBack {
		int period;
		Movie movie;

		public TimeBaseTimeCallBack(Movie movie, int flags) throws QTException {
			super(movie.getTimeBase(), movie.getTimeScale(), 1, flags);
			period = 1;
			this.movie = movie;
		}

		public void execute() {
			try {
				QuickTimeMedia.this.firePropertyChange(IMedia.PROP_TIME,
						currentTimeInMilli, currentTimeInMilli = movie
						.getTime());
				cancel();
				if (_isPlaying && movie.isDone()) {
					QuickTimeMedia.this.setPlaying(false);
				}
				value += period;
				callMeWhen();
			} catch (StdQTException ex) {
				IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
				logger.log(status);
			}catch (SWTException exp){}
		}
	}

	public String getFormattedDuration() {
		return getStringTimeFormat(this.getDuration());
	}

	/**
	 * FIXME needs review
	 * 
	 */
	@SuppressWarnings("unused")
	public void addTimecodeToMovie() {
		try {
			Track myTrack = movie.getIndTrackType(1,
					StdQTConstants.timeCodeMediaType,
					StdQTConstants.movieTrackMediaType);
			// only allow one time code track in movie
			if (myTrack != null)
				return;

			// Get the (first) visual track; this track determines the width of
			// the new timecode track
			Track theVisualTrack = movie.getIndTrackType(1,
					StdQTConstants.visualMediaCharacteristic,
					StdQTConstants.movieTrackCharacteristic);

			QDDimension dim = null;
			// Get movie and track attributes
			int movieTimeScale = movie.getTimeScale();

			// Create the timecode track and media
			if (theVisualTrack == null) {
				QDRect r = movie.getBounds();
				dim = new QDDimension(r.getWidth(), r.getHeight());
			} else {
				Media theVisualTrackMedia = Media.fromTrack(theVisualTrack);
				dim = theVisualTrack.getSize();
			}

			Track theTCTrack = movie.newTrack((float) dim.getWidth(),
					(float) dim.getHeight(), 0);
			TimeCodeMedia theTCMedia = new TimeCodeMedia(theTCTrack,
					movieTimeScale);
			TimeCoder theTimeCoder = theTCMedia.getTimeCodeHandler();

			// Set up a TimeCodeDef
			TimeCodeDef myTCDef = new TimeCodeDef();
			// 30 frames a second time code reading
			int tcdFlags = StdQTConstants.tc24HourMax;
			myTCDef.setFlags(tcdFlags);
			myTCDef.setTimeScale(3000);
			myTCDef.setFrameDuration(100);
			myTCDef.setFramesPerSecond(30);
			/*
			 * For drop frame 29.97 fps tcdFlags |= StdQTConstants.tcDropFrame;
			 * myTCDef.setTimeScale (2997);
			 */
			// Start the timecode at 0:0:0:0
			TimeCodeTime myTCTime = new TimeCodeTime(0, 0, 0, 0);

			// Change the text options to Green on Black.

			String myTCString = theTimeCoder
			.timeCodeToString(myTCDef, myTCTime);
			TCTextOptions myTCTextOptions = theTimeCoder.getDisplayOptions();
			int textSize = myTCTextOptions.getTXSize();
			myTCTextOptions.setForeColor(QDColor.green);
			myTCTextOptions.setBackColor(QDColor.black);
			theTimeCoder.setDisplayOptions(myTCTextOptions);

			// Figure out the timecode track geometry
			QDDimension tcDim = theTCTrack.getSize();
			tcDim.setHeight(textSize + 2);
			theTCTrack.setSize(tcDim);
			if (dim.getHeight() > 0) {
				Matrix TCMatrix = theTCTrack.getMatrix();
				TCMatrix.translate(0, dim.getHeight());
				theTCTrack.setMatrix(TCMatrix);
			}

			// add a sample to the timecode track
			//
			// each sample in a timecode track provides timecode information for
			// a span of movie time;
			// here, we add a single sample that spans the entire movie duration

			// the sample data contains a frame number that identifies one or
			// more content frames
			// that use the timecode; this value (a long integer) identifies the
			// first frame that
			// uses the timecode. For our purposes this will probably always be
			// zero, but it can't
			// hurt to go the full 9.
			int frameNumber = theTimeCoder.toFrameNumber(myTCTime, myTCDef);
			int[] frameNumberAr = { frameNumber };
			QTHandle myFrameNumHandle = new QTHandle(4, false);
			myFrameNumHandle.copyFromArray(0, frameNumberAr, 0, 1);

			// create and configure a new timecode description
			TimeCodeDescription myTCDescription = new TimeCodeDescription();
			myTCDescription.setTimeCodeDef(myTCDef);

			// edit the track media
			theTCMedia.beginEdits();

			// since we created the track with the same timescale as the movie,
			// we don't need to convert the duration
			theTCMedia.addSample(myFrameNumHandle, 0, myFrameNumHandle
					.getSize(), movie.getDuration(), myTCDescription, 1, 0);
			theTCMedia.endEdits();

			theTCTrack.insertMedia(0, 0, movie.getDuration(), 1.0F);

			// this code saves the TimeCode to the movie
			/*
			 * OpenMovieFile outStream = OpenMovieFile.asWrite (qtf);
			 * theMovie.addResource (outStream, movieInDataForkResID,
			 * qtf.getName()); outStream.close();
			 */

			// Make the timecode visible
			int tcFlags = theTimeCoder.getFlags();
			tcFlags |= StdQTConstants.tcdfShowTimeCode;
			theTimeCoder.setFlags(tcFlags, StdQTConstants.tcdfShowTimeCode);

		} catch (QTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	public boolean isAudioAvailable() {
		try {
			Track audioTrack = movie.getIndTrackType(1,
					StdQTConstants.soundMediaType,
					StdQTConstants.movieTrackMediaType);
			if (audioTrack != null)
				return true;
			else
				return false;
		} catch (QTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
			return false;
		}

	}

	public boolean isMute() {
		try {
			return (movie.getVolume() == 0f) ? true : false;
		} catch (StdQTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
		return false;
	}

	public boolean isPlaying() {
		return _isPlaying;
	}

	public void stepFF() {
		try {
			Track visualTrack = movie.getIndTrackType(1,
					StdQTConstants.visualMediaCharacteristic,
					StdQTConstants.movieTrackCharacteristic);
			TimeInfo ti = visualTrack.getNextInterestingTime(
					StdQTConstants.nextTimeMediaSample, movie.getTime(), 1);
			TimeRecord t = new TimeRecord(movie.getTimeScale(), ti.time);
			movie.setTime(t);
			firePropertyChange(IMedia.PROP_TIME, currentTimeInMilli,
					currentTimeInMilli = movie.getTime());
		} catch (QTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}

	}

	public void stepRE() {
		try {
			Track visualTrack = movie.getIndTrackType(1,
					StdQTConstants.visualMediaCharacteristic,
					StdQTConstants.movieTrackCharacteristic);
			TimeInfo ti = visualTrack.getNextInterestingTime(
					StdQTConstants.nextTimeMediaSample, movie.getTime(), -1);
			TimeRecord t = new TimeRecord(movie.getTimeScale(), ti.time);
			movie.setTime(t);
			firePropertyChange(IMedia.PROP_TIME, currentTimeInMilli,
					currentTimeInMilli = movie.getTime());

		} catch (QTException ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}

	}

	public void setRate(PlayRate rate) {

		if (_isPlaying) {
			updateMoviePlayRate();
		}
		firePropertyChange(IMedia.PROP_RATE, _currentPlayRate, _currentPlayRate = rate);
	}

	private void updateMoviePlayRate() {
		try {
			switch (_currentPlayRate) {
			case HALF:
				movie.setRate(0.5f);
				break;
			case X1:
				movie.setRate(1f);
				break;
			case X2:
				movie.setRate(2f);
				break;
			case X3:
				movie.setRate(3f);
				break;
			case X4:
				movie.setRate(4f);
				break;
			}
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	private void stopMovie() {
		try {
			movie.setRate(0f);
		} catch (Exception ex) {
			IStatus status = new Status(IStatus.ERROR,Activator.PLUGIN_ID,ex.getMessage(), ex);
			logger.log(status);
		}
	}

	public PlayRate getRate() {
		return _currentPlayRate;
	}

}
