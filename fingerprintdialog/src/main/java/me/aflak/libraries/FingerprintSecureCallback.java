package me.aflak.libraries;

import android.hardware.fingerprint.FingerprintManager;

/**
 * Created by Omar on 02/07/2017.
 */

public interface FingerprintSecureCallback {
    void onAuthenticationSuccess(FingerprintManager.CryptoObject cryptoObject);
    void onAuthenticationCancel();
    void onNewFingerprintEnrolled(FingerprintToken token);
}
