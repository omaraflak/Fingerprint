package me.aflak.libraries;

/**
 * Created by Omar on 10/07/2017.
 */

public class FingerprintToken {
    private CipherHelper cipherHelper;
    private FingerprintDialog fingerprintDialog;

    public FingerprintToken(CipherHelper cipherHelper, FingerprintDialog fingerprintDialog) {
        this.cipherHelper = cipherHelper;
        this.fingerprintDialog = fingerprintDialog;
    }

    public void continueAuthentication(){
        if(cipherHelper !=null && fingerprintDialog!=null) {
            cipherHelper.generateNewKey();
            fingerprintDialog.show();
        }
    }
}
