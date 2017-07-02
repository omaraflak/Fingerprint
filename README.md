# Fingerprint Dialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

Short Android library that allows you to create fingerprint dialogs for authentications.

# Use

    FingerprintDialog dialog = new FingerprintDialog(this);
    dialog.show("Sign In", "Confirm fingerprint to continue", new FingerprintCallback() {
        @Override
        public void onFingerprintSuccess() {
          // fingerprint recognized
        }

        @Override
        public void onFingerprintFailure() {
          // fingerprint not recognized
        }
    });

![alt text](https://oc.aflak.me/index.php/s/Y1MQO19nLolUNp0/download)
