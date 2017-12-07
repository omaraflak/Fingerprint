# FingerprintDialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

**FingerprintDialog** is an Android library that simplifies the process of fingerprint authentications.

# Usecase n°1 : Without CryptoObject

You only want to check if the user's fingerprint is enrolled in the phone.

```java
FingerprintDialog.initialize(this)
    .title(R.string.title)
    .message(R.string.message)
    .callback(new FingerprintCallback(...))
    .show();
```

[EXAMPLE](https://github.com/omaflak/FingerprintDialog-Library/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/FingerprintExample.java)
        
# Usecase n°2 : With CryptoObject

Check if the user's fingerprint is enrolled in the phone and detect if a new fingerprint was added since last time authentication was used.

```java
FingerprintDialog.initialize(this)
    .title(R.string.title)
    .message(R.string.message)
    .callback(new FingerprintSecureCallback(...), "KeyName")
    .show();
```
        
[EXAMPLE](https://github.com/omaflak/FingerprintDialog-Library/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/FingerprintSecureExample1.java)

# Usecase n°3 : Secure a CryptoObject via authentication

The CryptoObject will be valid only if the user has authenticated via fingerprint.
You have to use `setUserAuthenticationRequired(true)` when creating the CryptoObject.

```java
FingerprintManager.CryptoObject cryptoObject;
// cryptoObject = ...

if(FingerprintDialog.isAvailable(this)) {
    FingerprintDialog.initialize(this)
            .title(R.string.fingerprint_title)
            .message(R.string.fingerprint_message)
            .callback(new FingerprintCallback(...))
            .cryptoObject(cryptoObject)
            .show();
}
```

# Gradle

```gradle
implementation 'me.aflak.libraries:fingerprintdialog:2.4.3'
```
    
# Rendering

![alt text](https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo.gif?raw=true)
