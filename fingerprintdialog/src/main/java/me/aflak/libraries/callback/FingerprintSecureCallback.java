package me.aflak.libraries.callback;

import me.aflak.libraries.dialog.FingerprintToken;

/**
 * Created by Omar on 02/07/2017.
 */

public interface FingerprintSecureCallback {
    void onAuthenticationSuccess();
    void onAuthenticationCancel();
    void onNewFingerprintEnrolled(FingerprintToken token);
}
