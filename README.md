# Unseen IRC
A simple IRC app for Android
Because it is so simple, I will also try to keep it in line with the Material design philosophy

Based on [DesignSupportExample]

TODO:

(enhancement) Integrate keystore to securely hold username/password for an IRC item (https://github.com/nelenkov/android-keystore)

(bug) Figure out AppBarLayout and check to see if it is needed

(enhancement) Fix up how each item in IrcListFragment is laid out to match design specifications

(bug) Find out how to "fix" or wait for a fix for orientation change when using ActivityTestRule

(bug) Find out how to "fix" or wait for a fix for orientation change when using ActivityTestRule when the screen is off

(bug) Find out how to "fix" or wait for a fix for asking permission for External Write in Marshmallow for testing

(check) Check to see if debug/AndroidManifest.xml is working correctly on non-Marshmallow devices

(enhancement) Find a better fix for Navigation Header instead of displaying Accounts (which require get_accounts permission)

(enhancement) floating action button to show templates/preset for certain popular irc channels

(need) form for creating irc channels for the application

(enhancement) have better animation for landscape when showing an irc/creating 1 irc

(enhancement) irc settings fragment, showing info for the selected irc

(enhancement) make it so you can have tts/alert/vibrate/nothing for each message/whisper/nudge

(enhancement) when changing orientation, have the option to anchor top/center/bottom of the text

[DesignSupportExample]: https://github.com/blackcj/DesignSupportExample