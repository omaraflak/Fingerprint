# Fingerprint Dialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

Android library that allows you to create fingerprint dialogs for authentications.
You can either choose to use a **CryptoObject** or not.

# Dependencie

Gradle :

    implementation 'me.aflak.libraries:fingerprintdialog:X.X'

Maven :

    <dependency>
      <groupId>me.aflak.libraries</groupId>
      <artifactId>fingerprintdialog</artifactId>
      <version>X.X</version>
      <type>pom</type>
    </dependency>

# Use

The **FingerprintDialog** library provides a simple way to manage the dialog :

    FingerprintDialog.initialize(this, "ArbitraryKey")
            .enterAnimation(FingerprintDialog.ENTER_FROM_RIGHT)
            .exitAnimation(FingerprintDialog.EXIT_TO_RIGHT)
            .callback(new FingerprintSecureCallback() {
                @Override
                public void onAuthenticated() {
                    // Fingerprint recognized
                }

                @Override
                public void onCancelled() {
                    // User pressed cancel button
                }

                @Override
                public void onNewFingerprintEnrolled(KeyStoreHelper helper) {
                    // A new fingerprint was added
                    // should prompt a password to verify identity
                    // if (password correct) {
                    //      helper.generateNewKey();
                    //      // show fingerprint dialog again
                    // }
                }
            }) // if you pass a FingerprintCallback object, the CryptoObject won't be used. If you pass a FingerprintSecureCallback object, it will.
            .title(R.string.title)
            .message(R.string.message)
            .show();
    
See **[MainActivity.java](https://github.com/omaflak/FingerprintDialog/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/MainActivity.java)**

# Rendering

![alt text](https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo.gif?raw=true)
