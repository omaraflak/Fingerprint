package me.aflak.libraries;


import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by Omar on 07/07/2017.
 */

public interface KeyStoreHelperCallback {
    void onNewFingerprintEnrolled();
    void onCryptoObjectRetrieved(FingerprintManager.CryptoObject cryptoObject);
}
