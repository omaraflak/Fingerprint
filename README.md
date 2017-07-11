# Fingerprint Dialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

Android library that allows you to create fingerprint dialogs for authentications.
You can either choose to use a **CryptoObject** or not.

# Use

The **FingerprintDialog** library provides a simple way to manage the dialog :

    FingerprintDialog.initialize(Context, "ArbitraryKey")
        .enterAnimation(DialogAnimation.ENTER_FROM_RIGHT)
        .exitAnimation(DialogAnimation.EXIT_TO_RIGHT)
        .callback(new FingerprintCallback(...)) // if you pass a FingerprintCallback object, the CryptoObject won't be used. If you pass a FingerprintSecureCallback object, it will.
        .title(R.string.title)
        .message(R.string.message)
        .show();
    
**[EXAMPLES HERE](https://github.com/omaflak/FingerprintDialog-Library/tree/master/app/src/main/java/me/aflak/fingerprintdialoglibrary)**

# Gradle

    implementation 'me.aflak.libraries:fingerprintdialog:X.X'
    
# Rendering

![alt text](https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo.gif?raw=true)
