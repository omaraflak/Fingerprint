package me.aflak.libraries;

/**
 * Created by Omar on 02/07/2017.
 */

public interface FingerprintSecureCallback extends FingerprintCallback{
    void onFingerprintSuccess();
    void onFingerprintCancel();
    void onNewFingerprintEnrolled();
}
