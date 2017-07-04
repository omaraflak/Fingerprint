# Fingerprint Dialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

Short Android library that allows you to create fingerprint dialogs for authentications.

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

    FingerprintDialog dialog = new FingerprintDialog(this);
    dialog.show("Sign In", "Confirm fingerprint to continue", new FingerprintCallback() {
        @Override
        public void onFingerprintSuccess() {
          // fingerprint recognized
        }

        @Override
        public void onFingerprintCancel() {
          // user pressed cancel button
        }
    });
    
See **[MainActivity.java](https://github.com/omaflak/FingerprintDialog/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/MainActivity.java)**

# Customization

- **setSuccessColor**
- **setErrorColor**
- **setAnimation**
- **setCanceledOnTouchOutside**

![alt text](https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo.gif?raw=true)
