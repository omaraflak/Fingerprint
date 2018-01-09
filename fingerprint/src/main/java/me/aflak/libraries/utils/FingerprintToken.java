package me.aflak.libraries.utils;

/**
 * Created by Omar on 10/07/2017.
 */

public class FingerprintToken {
    private CipherHelper cipherHelper;

    public FingerprintToken(CipherHelper cipherHelper) {
        this.cipherHelper = cipherHelper;
    }

    public void validate(){
        if(cipherHelper !=null) {
            cipherHelper.generateNewKey();
        }
    }
}
