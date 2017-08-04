# Fingerprint Dialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

**FingerprintDialog** is an Android library that allows you to create fingerprint dialogs for authentications.
You can either choose to use a **CryptoObject** or not.

# Usecase n°1 : No CryptoObject

You only want to check if the user's fingerprint is enrolled in the phone.

    FingerprintDialog.initialize(Context)
        .title(R.string.title)
        .message(R.string.message)
        .callback(new FingerprintCallback(...))
        .show();
        
# Usecase n°2 : With CryptoObject

Check if the user's fingerprint is enrolled in the phone and detect if a new fingerprint was added since last time authentication was used.

    FingerprintDialog.initialize(Context)
        .title(R.string.title)
        .message(R.string.message)
        .callback(new FingerprintSecureCallback(...), "KeyName")
        .show();
        
# Usecase n°3 : Secure a CryptoObject via authentication

The CryptoObject will be valid only if the user has authenticated via fingerprint. This is one way to ensure that it is the correct user that is performing an operation (via Signature for example).

    CipherHelper helper = new CipherHelper("KeyName");
    FingerprintManager.CryptoObject cryptoObject = helper.getEncryptionCryptoObject();
    if(cryptoObject==null){
        // /!\ A new fingerprint was added /!\
        //
        // Prompt a password to verify identity, then :
        // if (password correct) {
        //      helper.generateNewKey();
        // }
        //
        // OR
        //
        // Use PasswordDialog to simplify the process
        
        PasswordDialog.initialize(this, helper)
                .title(R.string.password_title)
                .message(R.string.password_message)
                .callback(this)
                .passwordType(PasswordDialog.PASSWORD_TYPE_TEXT)
                .show();
    }
    else{
        if(FingerprintDialog.isAvailable(Context)) {
            FingerprintDialog.initialize(Context)
                    .title(R.string.fingerprint_title)
                    .message(R.string.fingerprint_message)
                    .callback(new FingerprintCallback(...))
                    .cryptoObject(cryptoObject)
                    .show();
        }   
    }
    
**[EXAMPLES HERE](https://github.com/omaflak/FingerprintDialog-Library/tree/master/app/src/main/java/me/aflak/fingerprintdialoglibrary)**

# Gradle

    implementation 'me.aflak.libraries:fingerprintdialog:X.X'
    
# Rendering

![alt text](https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo.gif?raw=true)
