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
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.spec.ECGenParameterSpec;

/**
 * Created by Omar on 04/08/2017.
 */

public class SignatureHelper {
    private KeyStore keyStore;

    private Signature signature;
    private KeyPair signatureKey;
    private KeyPairGenerator signatureKeyGenerator;

    private boolean keyStoreLoaded;
    private boolean signatureKeyGenCreated;
    private boolean signatureCreated;
    private boolean lastCallSigning;

    private final String keyName;
    private final String provider = "AndroidKeyStore";

    public SignatureHelper(String keyName){
        this.keyName = keyName;
        this.keyStoreLoaded = false;
        this.signatureKeyGenCreated = false;
        this.signatureCreated = false;
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
            Certificate certificate = keyStore.getCertificate(keyName);
            if(certificate==null){
                return false;
            }
            PublicKey publicKey = certificate.getPublicKey();
            PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyName, null);
            signatureKey = new KeyPair(publicKey, privateKey);
            return true;
        } catch (UnrecoverableKeyException e){
            return false;
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get key", e);
        }
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

    private boolean initSigningSignature() {
        try {
            signature.initSign(signatureKey.getPrivate());
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to init Signature", e);
        }
    }

    private boolean initVerifyingSignature() {
        try {
            signature.initVerify(signatureKey.getPublic());
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Failed to init Signature", e);
        }
    }

    public void generateNewKey(){
        createSignatureKeyGenerator();
        signatureKey = signatureKeyGenerator.generateKeyPair();
        reloadKeyStore();
    }

    public void recall(){
        if(lastCallSigning){
            getSigningCryptoObject();
        }
        else{
            getVerifyingCryptoObject();
        }
    }

    public FingerprintManager.CryptoObject getSigningCryptoObject(){
        this.lastCallSigning = true;

        loadKeyStore();
        if(!hasKey()){
            generateNewKey();
        }

        createSignature();
        if (initSigningSignature()) {
            return new FingerprintManager.CryptoObject(signature);
        } else {
            return null;
        }
    }

    public FingerprintManager.CryptoObject getVerifyingCryptoObject(){
        this.lastCallSigning = false;

        loadKeyStore();
        if(!hasKey()){
            generateNewKey();
        }

        createSignature();
        if (initVerifyingSignature()) {
            return new FingerprintManager.CryptoObject(signature);
        } else {
            return null;
        }
    }
}
