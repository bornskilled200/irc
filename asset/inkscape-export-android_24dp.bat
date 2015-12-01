mkdir 	"%~n1\mipmap-mdpi" ^
		"%~n1\mipmap-hdpi" ^
		"%~n1\mipmap-xhdpi" ^
		"%~n1\mipmap-xxhdpi" ^
		"%~n1\mipmap-xxxhdpi"
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-mdpi\%~n1.png" -w 24 -h 24 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-hdpi\%~n1.png" -w 36 -h 36 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-xhdpi\%~n1.png" -w 48 -h 48 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-xxhdpi\%~n1.png" -w 72 -h 72 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-xxxhdpi\%~n1.png" -w 96 -h 96 "%~1"