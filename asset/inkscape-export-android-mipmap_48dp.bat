mkdir 	"%~n1\mipmap-mdpi" ^
		"%~n1\mipmap-hdpi" ^
		"%~n1\mipmap-xhdpi" ^
		"%~n1\mipmap-xxhdpi" ^
		"%~n1\mipmap-xxxhdpi"
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\mipmap-mdpi\%~n1.png" -w 48 -h 48 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\mipmap-hdpi\%~n1.png" -w 72 -h 72 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\mipmap-xhdpi\%~n1.png" -w 96 -h 96 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\mipmap-xxhdpi\%~n1.png" -w 144 -h 144 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\mipmap-xxxhdpi\%~n1.png" -w 192 -h 192 "%~1"