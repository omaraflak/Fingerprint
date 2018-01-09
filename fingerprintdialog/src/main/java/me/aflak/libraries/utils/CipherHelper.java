package me.aflak.libraries.utils;

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
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Omar on 04/08/2017.
 */

public class CipherHelper {
    private KeyStore keyStore;
    private Cipher cipher;
    private SecretKey cipherKey;
    private KeyGenerator cipherKeyGenerator;

    private boolean keyStoreLoaded;
    private boolean cipherKeyGenCreated;
    private boolean cipherCreated;

    private final String keyName;
    private final String provider = "AndroidKeyStore";

    public CipherHelper(String keyName){
        this.keyName = keyName;
        this.keyStoreLoaded = false;
        this.cipherKeyGenCreated = false;
        this.cipherCreated = false;
    }

    private void loadKeyStore(){
        if(keyStoreLoaded){
            return;
        }

        reloadKeyStore();
    }

    private void reloadKeyStore(){
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
            cipherKey = (SecretKey) keyStore.getKey(keyName, null);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new RuntimeException("Failed to get key", e);
        }
        return cipherKey != null;
    }

    private void createCipherKeyGenerator(){
        if(cipherKeyGenCreated){
            return;
        }
        try {
            cipherKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, provider);
            cipherKeyGenerator.init(new KeyGenParameterSpec.Builder(keyName,KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            cipherKeyGenCreated = true;
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
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
    }

    private boolean initEncryptionCipher() {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, cipherKey);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private boolean initDecryptionCipher(byte[] ivBytes){
        try {
            cipher.init(Cipher.DECRYPT_MODE, cipherKey, new IvParameterSpec(ivBytes));
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void generateNewKey(){
        createCipherKeyGenerator();
        cipherKey = cipherKeyGenerator.generateKey();
        reloadKeyStore();
    }

    public FingerprintManager.CryptoObject getEncryptionCryptoObject(){
        loadKeyStore();
        if(!hasKey()){
            generateNewKey();
        }

        createCipher();
        if (initEncryptionCipher()) {
            return new FingerprintManager.CryptoObject(cipher);
        } else {
            return null;
        }
    }

    public FingerprintManager.CryptoObject getDecryptionCryptoObject(byte[] ivBytes){
        loadKeyStore();
        if(!hasKey()){
            generateNewKey();
        }

        createCipher();
        if (initDecryptionCipher(ivBytes)) {
            return new FingerprintManager.CryptoObject(cipher);
        } else {
            return null;
        }
    }
}
