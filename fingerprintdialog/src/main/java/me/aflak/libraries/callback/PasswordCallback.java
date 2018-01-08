package me.aflak.libraries.callback;

/**
 * Created by Omar on 10/07/2017.
 */

public interface PasswordCallback {
    boolean onPasswordCheck(String password);
    void onPasswordCancel();
}