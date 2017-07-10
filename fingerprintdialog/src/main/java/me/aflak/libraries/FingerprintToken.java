package me.aflak.libraries;

/**
 * Created by Omar on 10/07/2017.
 */

public class FingerprintToken {
    private KeyStoreHelper keyStoreHelper;
    private FingerprintDialog fingerprintDialog;

    public FingerprintToken(KeyStoreHelper keyStoreHelper, FingerprintDialog fingerprintDialog) {
        this.keyStoreHelper = keyStoreHelper;
        this.fingerprintDialog = fingerprintDialog;
    }

    public void continueAuthentication(){
        if(keyStoreHelper!=null && fingerprintDialog!=null) {
            keyStoreHelper.generateNewKey();
            fingerprintDialog.show();
        }
    }
}
