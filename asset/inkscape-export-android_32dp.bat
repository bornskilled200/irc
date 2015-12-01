mkdir 	"%~n1\drawable-mdpi" ^
		"%~n1\drawable-hdpi" ^
		"%~n1\drawable-xhdpi" ^
		"%~n1\drawable-xxhdpi" ^
		"%~n1\drawable-xxxhdpi"
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-mdpi\%~n1.png" -w 32 -h 32 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-hdpi\%~n1.png" -w 48 -h 48 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-xhdpi\%~n1.png" -w 64 -h 64 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-xxhdpi\%~n1.png" -w 96 -h 96 "%~1" 
"C:\Program Files\Inkscape\inkscape.com" -z -e "%~n1\drawable-xxxhdpi\%~n1.png" -w 128 -h 128 "%~1"