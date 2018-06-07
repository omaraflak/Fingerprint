package me.aflak.libraries.callback;

import me.aflak.libraries.view.Fingerprint;

/**
 * Created by Omar on 10/07/2017.
 */

public interface FailAuthCounterCallback {
    void onTryLimitReached(Fingerprint fingerprint);
}
