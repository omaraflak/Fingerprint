package me.aflak.libraries;

/**
 * Created by Omar on 10/07/2017.
 */

public interface PasswordCallback {
    boolean onPasswordCheck(String password);
    void onPasswordCancel();
}
