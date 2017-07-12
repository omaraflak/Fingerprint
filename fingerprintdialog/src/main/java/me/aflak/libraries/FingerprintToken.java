package me.aflak.libraries;

/**
 * Created by Omar on 10/07/2017.
 */

public class FingerprintToken {
    private CryptoObjectHelper cryptoObjectHelper;
    private FingerprintDialog fingerprintDialog;

    public FingerprintToken(CryptoObjectHelper cryptoObjectHelper, FingerprintDialog fingerprintDialog) {
        this.cryptoObjectHelper = cryptoObjectHelper;
        this.fingerprintDialog = fingerprintDialog;
    }

    public void continueAuthentication(){
        if(cryptoObjectHelper !=null && fingerprintDialog!=null) {
            cryptoObjectHelper.generateNewKey();
            fingerprintDialog.show();
        }
    }
}
