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

    FingerprintDialog dialog = new FingerprintDialog(Context);
    dialog.showSecure(R.string.title, R.string.message, new FingerprintSecureCallback() {
        @Override
        public void onAuthenticated() {
            // Fingerprint recognized
        }
    
        @Override
        public void onCancelled() {
            // User pressed cancel button
        }
    
        @Override
        public void onNewFingerprintEnrolled() {
            // A new fingerprint was added
            // should prompt a password
            // if (password correct) {
            //      dialog.generateNewKey()
            //      dialog.showSecure(...)
            // }
        }
    });
    
See **[MainActivity.java](https://github.com/omaflak/FingerprintDialog/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/MainActivity.java)**

# Methods

- **show**
- **showSecure**
- **hasEnrolledFingerprints**
- **isHardwareDetected**
- **generateNewKey**
- **setSuccessColor**
- **setErrorColor**
- **setAnimation**
- **setCanceledOnTouchOutside**

![alt text](https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo.gif?raw=true)
