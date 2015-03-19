@echo off
if [%1]==[] goto :eof
:loop
CALL "C:\Program Files (x86)\VideoLAN\VLC\vlc" -I dummy -vvv %1 --sout=#transcode{acodec="s16l"}:standard{access="file",mux="wav",dst="%~n1_converted.wav"} vlc://quit
shift
if not [%1]==[] goto loop






