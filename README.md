# Fingerprint Dialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

Android library that allows you to create fingerprint dialogs for authentications.
You can either choose to use a **CryptoObject** or not.

# Use

The **FingerprintDialog** library provides a simple way to manage the dialog :

    FingerprintDialog.initialize(Context, "ArbitraryKeyName")
        .enterAnimation(DialogAnimation.Enter.RIGHT)
        .exitAnimation(DialogAnimation.Exit.RIGHT)
        .callback(new FingerprintCallback(...))
        .title(R.string.title)
        .message(R.string.message)
        .show();
    
**[EXAMPLES HERE](https://github.com/omaflak/FingerprintDialog-Library/tree/master/app/src/main/java/me/aflak/fingerprintdialoglibrary)**

# Gradle

    implementation 'me.aflak.libraries:fingerprintdialog:X.X'
    
# Rendering

![alt text](https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo.gif?raw=true)
