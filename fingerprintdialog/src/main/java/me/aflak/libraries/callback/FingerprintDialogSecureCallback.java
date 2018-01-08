package me.aflak.libraries.callback;

import me.aflak.libraries.dialog.FingerprintToken;

/**
 * Created by Omar on 08/01/2018.
 */

public interface FingerprintDialogSecureCallback {
    void onAuthenticationSucceeded();
    void onAuthenticationCancel();
    void onNewFingerprintEnrolled(FingerprintToken token);
}
