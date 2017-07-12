package me.aflak.libraries;

/**
 * Created by Omar on 02/07/2017.
 */

public interface FingerprintSecureCallback {
    void onAuthenticationSuccess();
    void onAuthenticationCancel();
    void onNewFingerprintEnrolled(FingerprintToken token);
}
