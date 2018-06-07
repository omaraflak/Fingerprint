package me.aflak.libraries.callback;

import me.aflak.libraries.dialog.FingerprintDialog;

public interface FailAuthCounterDialogCallback {
    void onTryLimitReached(FingerprintDialog fingerprintDialog);
}
