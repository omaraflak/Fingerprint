# Fingerprint Dialog [ ![Download](https://api.bintray.com/packages/omaflak/maven/fingerprintdialog/images/download.svg) ](https://bintray.com/omaflak/maven/fingerprintdialog/_latestVersion)

**FingerprintDialog** is an Android library that simplifies the process of fingerprint authentications.

# Download

```groovy
implementation 'me.aflak.libraries:fingerprintdialog:2.5.0'
```

# Usecase n°1 : Without CryptoObject

You only want to check if the user's fingerprint is enrolled in the phone.

```java
FingerprintDialog.initialize(this)
    .title(R.string.title)
    .message(R.string.message)
    .callback(new FingerprintDialogCallback(...))
    .show();
```

[EXAMPLE](https://github.com/omaflak/FingerprintDialog-Library/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/FingerprintExample.java)
        
# Usecase n°2 : With CryptoObject

Check if the user's fingerprint is enrolled in the phone and detect if a new fingerprint was added since last time authentication was used.

```java
FingerprintDialog.initialize(this)
    .title(R.string.title)
    .message(R.string.message)
    .callback(new FingerprintDialogSecureCallback(...), "KeyName")
    .show();
```
        
[EXAMPLE](https://github.com/omaflak/FingerprintDialog-Library/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/FingerprintSecureExample.java)

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

# Usecase n°4 : Fingerprint View

The library also provides a Fingerprint object which is a view that you can display the way you want it.

```xml
<me.aflak.libraries.view.Fingerprint
    android:id="@+id/fingerprint"
    android:layout_width="200dp"
    android:layout_height="200dp"/>
```

```java
Fingerprint fingerprint = findViewById(R.id.fingerprint);
fingerprint.callback(new FingerprintCallback(...));
fingerprint.authenticate();
```

[EXAMPLE](https://github.com/omaflak/FingerprintDialog-Library/blob/master/app/src/main/java/me/aflak/fingerprintdialoglibrary/FingerprintViewExample.java)

# Customization

Several functions are available to customize your dialog.

```java
FingerprintDialog.initialize(this)
        .title(R.string.fingerprint_title)
        .message(R.string.fingerprint_message)
        .enterAnimation(DialogAnimation.Enter.RIGHT)
        .exitAnimation(DialogAnimation.Exit.RIGHT)
        .circleScanningColor(R.color.colorAccent)
        .callback(this)
        .show();
```

# Rendering

<p float="left">
    <img src="https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo1.gif" width="400" />
    <img src="https://github.com/omaflak/FingerprintDialog/blob/master/GIF/demo2.gif" width="400" />
</p>
