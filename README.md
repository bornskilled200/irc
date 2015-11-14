# Unseen IRC
A simple IRC app for Android
Because it is so simple, I will also try to keep it in line with the Material design philosophy

Based on [DesignSupportExample]

TODO:
Integrate keystore to securely hold username/password for an IRC item (https://github.com/nelenkov/android-keystore)
Figure out AppBarLayout and check to see if it is needed
Fix up how each item in IrcListFragment is laid out to match design specifications
Find out how to "fix" or wait for a fix for orientation change when using ActivityTestRule
Find out how to "fix" or wait for a fix for asking permission for External Write in Marshmallow for testing
Check to see if debug/AndroidManifest.xml is working correctly on non-Marshmallow devices
Find a better fix for Navigation Header instead of displaying Accounts (which require get_accounts permission)


[DesignSupportExample]: https://github.com/blackcj/DesignSupportExample