@echo off
if [%1]==[] goto :eof
:loop
CALL "C:\Program Files\VideoLAN\VLC\vlc" -I dummy -vvv %1 --sout=#transcode{vcodec="mp4v",acodec=mp4a}:standard{access="file",mux="mp4",dst="%~n1_converted.mp4"} vlc://quit
shift
if not [%1]==[] goto loop






