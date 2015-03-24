/**
 *  File: ISubtitleControl.java
 *  Copyright (c) 2010
 *  phyokyaw
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package synergyviewcore.annotations.ui;


/**
 * The Interface IAnnotationMediaControl.
 *
 * @author phyokyaw
 */
public interface IAnnotationMediaControl {
	
	/**
	 * Sets the playing.
	 *
	 * @param playValue the new playing
	 */
	void setPlaying(boolean playValue);
	
	/**
	 * Checks if is playing.
	 *
	 * @return true, if is playing
	 */
	boolean isPlaying();
	
	/**
	 * Step forward.
	 */
	void stepForward();
	
	/**
	 * Step rewind.
	 */
	void stepRewind();
	
	/**
	 * Start caption mark.
	 */
	void startCaptionMark();
	
	/**
	 * Stop caption mark.
	 */
	void stopCaptionMark();
	
	/**
	 * Sets the row selection.
	 *
	 * @param number the new row selection
	 */
	void setRowSelection(int number);
	
	/**
	 * Sets the mute.
	 *
	 * @param mediaClipIntervalImpl the media clip interval impl
	 * @param muteValue the mute value
	 */
	void setMute(MediaClipIntervalImpl mediaClipIntervalImpl, boolean muteValue);
    
    /**
     * Checks if is mute.
     *
     * @param mediaClipIntervalImpl the media clip interval impl
     * @return true, if is mute
     */
    boolean isMute(MediaClipIntervalImpl mediaClipIntervalImpl);
	
}
