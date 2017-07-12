package me.aflak.libraries;

import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.spec.ECGenParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Omar on 07/07/2017.
 */

public class CryptoObjectHelper {
    private KeyStore keyStore;

    private Cipher cipher;
    private SecretKey cipherKey;
    private KeyGenerator cipherKeyGenerator;

    private Signature signature;
    private KeyPair signatureKey;
    private KeyPairGenerator signatureKeyGenerator;

    private CryptoObjectHelperCallback callback;

    private boolean keyStoreLoaded;

    private boolean cipherKeyGenCreated;
    private boolean cipherCreated;
    private boolean signatureKeyGenCreated;
    private boolean signatureCreated;

    private Type type;
    private int mode;

    private final String keyName;
    private final String provider = "AndroidKeyStore";

    public enum Type{
        CIPHER,
        SIGNATURE
    }

    public CryptoObjectHelper(String keyName){
        this.keyName = keyName;
        this.keyStoreLoaded = false;
        this.cipherKeyGenCreated = false;
        this.cipherCreated = false;
        this.signatureKeyGenCreated = false;
        this.signatureCreated = false;
        this.mode = -1;
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
            switch (type) {
                case CIPHER:
                    cipherKey = (SecretKey) keyStore.getKey(keyName, null);
                    return cipherKey != null;
                case SIGNATURE:
                    Certificate certificate = keyStore.getCertificate(keyName);
                    if(certificate==null){
                        return false;
                    }
                    PublicKey publicKey = certificate.getPublicKey();
                    PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, null);
                    signatureKey = new KeyPair(publicKey, privateKey);
                    return true; //(publicKey != null && privateKey != null);
            }
        } catch (UnrecoverableKeyException e){
            return false;
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get key", e);
        }
        throw new RuntimeException("type not found");
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

    private boolean initCipher() {
        try {
            switch (mode) {
                case Cipher.ENCRYPT_MODE:
                    cipher.init(mode, cipherKey);
                    return true;
                case Cipher.DECRYPT_MODE:
                    byte[] ivBytes = new byte[16];
                    new SecureRandom().nextBytes(ivBytes);
                    cipher.init(mode, cipherKey, new IvParameterSpec(ivBytes));
                    return true;
            }
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
        throw new RuntimeException("mode not found");
    }

    private void createSignatureKeyGenerator(){
        if(signatureKeyGenCreated){
            return;
        }
        try {
            signatureKeyGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, provider);
            signatureKeyGenerator.initialize(new KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_SIGN)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                    .setUserAuthenticationRequired(true)
                    .build());

            signatureKeyGenCreated = true;
        } catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException("Failed to create key generator", e);
        }
    }

    private void createSignature(){
        if(signatureCreated){
            return;
        }

        try {
            signature = Signature.getInstance("SHA256withECDSA");
            signatureCreated = true;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get an instance of Signature", e);
        }
    }

    private boolean initSignature() {
        try {
            switch (mode) {
                case KeyProperties.PURPOSE_SIGN:
                    signature.initSign(signatureKey.getPrivate());
                    return true;
                case KeyProperties.PURPOSE_VERIFY:
                    signature.initVerify(signatureKey.getPublic());
                    return true;
            }
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to init Signature", e);
        }
        throw new RuntimeException("mode not found");
    }

    private void create(){
        switch (type) {
            case CIPHER:
                createCipher();
                break;
            case SIGNATURE:
                createSignature();
                break;
        }
    }

    private boolean init(){
        switch (type) {
            case CIPHER:
                return initCipher();
            case SIGNATURE:
                return initSignature();
        }
        throw new RuntimeException("type not found");
    }

    private FingerprintManager.CryptoObject getObject(){
        switch (type) {
            case CIPHER:
                return new FingerprintManager.CryptoObject(cipher);
            case SIGNATURE:
                return new FingerprintManager.CryptoObject(signature);
        }
        throw new RuntimeException("type not found");
    }

    public void generateNewKey(){
        if(mode==-1){
            return;
        }

        switch (type) {
            case CIPHER:
                createCipherKeyGenerator();
                cipherKey = cipherKeyGenerator.generateKey();
                break;
            case SIGNATURE:
                createSignatureKeyGenerator();
                signatureKey = signatureKeyGenerator.generateKeyPair();
                break;
        }
        reloadKeyStore();
    }

    public void recall(){
        getCryptoObject(type, mode, callback);
    }

    public void getCryptoObject(Type type, int mode, CryptoObjectHelperCallback callback){
        this.type = type;
        this.mode = mode;
        this.callback = callback;

        loadKeyStore();
        if(!hasKey()){
            generateNewKey();
        }

        create();
        if (init()) {
            if (callback != null) {
                callback.onCryptoObjectRetrieved(getObject());
            }
        } else {
            if (callback != null) {
                callback.onNewFingerprintEnrolled();
            }
        }
    }
}