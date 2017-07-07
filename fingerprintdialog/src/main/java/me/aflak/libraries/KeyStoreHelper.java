package me.aflak.libraries;

import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by Omar on 07/07/2017.
 */

public class KeyStoreHelper {
    private KeyStore keyStore;
    private SecretKey secretKey;
    private KeyGenerator keyGenerator;
    private Cipher cipher;

    private boolean keyStoreLoaded;
    private boolean keyGenCreated;
    private boolean cipherCreated;

    private final String keyName;
    private final String provider = "AndroidKeyStore";

    public KeyStoreHelper(String keyName){
        this.keyName = keyName;
        this.keyStoreLoaded = false;
        this.keyGenCreated = false;
        this.cipherCreated = false;
    }

    private void loadKeyStore(){
        if(keyStoreLoaded){
            return;
        }

        try {
            keyStore = KeyStore.getInstance(provider);
            keyStore.load(null);
            keyStoreLoaded = true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get keystore", e);
        }
    }

    private boolean hasKey(){
        try {
            secretKey = (SecretKey) keyStore.getKey(keyName, null);
            return secretKey!=null;
        } catch (UnrecoverableKeyException e){
            return false;
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get key", e);
        }
    }

    private void createGenerator(){
        if(keyGenCreated){
            return;
        }
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, provider);
            keyGenerator.init(new KeyGenParameterSpec.Builder(keyName,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenCreated = true;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to create key generator", e);
        }
    }

    private void createCipher(){
        if(cipherCreated){
            return;
        }

        try {
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipherCreated = true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
    }

    private boolean initCipher() {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void generateNewKey(){
        if(keyGenerator==null){
            createGenerator();
        }
        secretKey = keyGenerator.generateKey();
    }

    public void getCryptoObject(KeyStoreHelperCallback callback){
        loadKeyStore();
        if(!hasKey()){
            generateNewKey();
        }

        createCipher();
        if(initCipher()){
            if(callback!=null){
                callback.onCryptoObjectRetrieved(new FingerprintManager.CryptoObject(cipher));
            }
        }
        else{
            if(callback!=null){
                callback.onNewFingerprintEnrolled();
            }
        }
    }
}
