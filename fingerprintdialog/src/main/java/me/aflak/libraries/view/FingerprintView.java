package me.aflak.libraries.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import me.aflak.libraries.R;
import me.aflak.libraries.callback.FailAuthCounterCallback;
import me.aflak.libraries.callback.FingerprintViewCallback;
import me.aflak.libraries.callback.FingerprintViewSecureCallback;
import me.aflak.libraries.dialog.CipherHelper;
import me.aflak.libraries.dialog.FingerprintToken;

/**
 * Created by Omar on 08/01/2018.
 */

public class FingerprintView extends FrameLayout {
    private final static String TAG = "FingerprintView";

    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;
    private FingerprintViewCallback fingerprintViewCallback;
    private FingerprintViewSecureCallback fingerprintViewSecureCallback;
    private FailAuthCounterCallback counterCallback;
    private FingerprintManager.CryptoObject cryptoObject;
    private CipherHelper cipherHelper;
    private Handler handler;

    int fingerprintScanning, fingerprintSuccess, fingerprintError;
    int circleScanning, circleSuccess, circleError;

    private long delayAfterError;
    private int limit, tryCounter;

    public FingerprintView(Context context) {
        super(context);
    }

    public FingerprintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs, 0, 0);
        initView(context);
    }

    public FingerprintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs, defStyleAttr, 0);
        initView(context);
    }

    public FingerprintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FingerprintView, defStyleAttr, defStyleRes);
        try {
            fingerprintScanning = a.getColor(R.styleable.FingerprintView_fingerprintScanningColor, ContextCompat.getColor(context, R.color.fingerprint_scanning));
            fingerprintSuccess = a.getColor(R.styleable.FingerprintView_fingerprintSuccessColor, ContextCompat.getColor(context, R.color.fingerprint_success));
            fingerprintError = a.getColor(R.styleable.FingerprintView_fingerprintErrorColor, ContextCompat.getColor(context, R.color.fingerprint_error));

            circleScanning = a.getColor(R.styleable.FingerprintView_circleScanningColor, ContextCompat.getColor(context, R.color.circle_scanning));
            circleSuccess = a.getColor(R.styleable.FingerprintView_circleSuccessColor, ContextCompat.getColor(context, R.color.circle_success));
            circleError = a.getColor(R.styleable.FingerprintView_circleErrorColor, ContextCompat.getColor(context, R.color.circle_error));
        } finally {
            a.recycle();
        }
    }

    private View setStatus(int drawableId, int drawableColorId, int circleColorId){
        View view = inflate(getContext(), R.layout.fingerprint_view, null);
        ImageView fingerprint = view.findViewById(R.id.fingerprint_view_fingerprint);
        View circle = view.findViewById(R.id.fingerprint_view_circle);

        fingerprint.setImageResource(drawableId);
        fingerprint.setColorFilter(ContextCompat.getColor(getContext(), drawableColorId), android.graphics.PorterDuff.Mode.MULTIPLY);
        circle.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(circleColorId)));
        return view;
    }

    private void initView(Context context){
        this.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        this.cipherHelper = null;
        this.handler = new Handler();
        this.fingerprintViewCallback = null;
        this.fingerprintViewSecureCallback = null;
        this.counterCallback = null;
        this.cryptoObject = null;
        this.delayAfterError = 1200;
        this.tryCounter = 0;

        addView(setStatus(R.drawable.fingerprint, fingerprintScanning, circleScanning));
    }

    public FingerprintView callback(FingerprintViewCallback fingerprintViewCallback){
        this.fingerprintViewCallback = fingerprintViewCallback;
        return this;
    }

    public FingerprintView callback(FingerprintViewSecureCallback fingerprintViewSecureCallback, String KEY_NAME){
        this.fingerprintViewSecureCallback = fingerprintViewSecureCallback;
        this.cipherHelper = new CipherHelper(KEY_NAME);
        return this;
    }

    public FingerprintView cryptoObject(FingerprintManager.CryptoObject cryptoObject){
        this.cryptoObject = cryptoObject;
        return this;
    }

    public FingerprintView fingerprintScanningColor(int fingerprintScanning) {
        this.fingerprintScanning = fingerprintScanning;
        return this;
    }

    public FingerprintView fingerprintSuccessColor(int fingerprintSuccess) {
        this.fingerprintSuccess = fingerprintSuccess;
        return this;
    }

    public FingerprintView fingerprintErrorColor(int fingerprintError) {
        this.fingerprintError = fingerprintError;
        return this;
    }

    public FingerprintView circleScanningColor(int circleScanning) {
        this.circleScanning = circleScanning;
        return this;
    }

    public FingerprintView circleSuccessColor(int circleSuccess) {
        this.circleSuccess = circleSuccess;
        return this;
    }

    public FingerprintView circleErrorColor(int circleError) {
        this.circleError = circleError;
        return this;
    }

    public FingerprintView delayAfterError(long delayAfterError){
        this.delayAfterError = delayAfterError;
        return this;
    }

    public FingerprintView tryLimit(int limit, FailAuthCounterCallback counterCallback){
        this.limit = limit;
        this.counterCallback = counterCallback;
        return this;
    }

    public void authenticate(){
        if(fingerprintViewSecureCallback!=null){
            if(cryptoObject!=null){
                throw new RuntimeException("If you specify a CryptoObject you have to use FingerprintCallback");
            }
            cryptoObject = cipherHelper.getEncryptionCryptoObject();
            if(cryptoObject==null) {
                fingerprintViewSecureCallback.onNewFingerprintEnrolled(new FingerprintToken(cipherHelper));
            }
        }
        else if(fingerprintViewCallback==null){
            throw new RuntimeException("You must specify a callback.");
        }

        cancellationSignal = new CancellationSignal();
        if(fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
            fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    setStatus(R.drawable.fingerprint_error, fingerprintError, circleError);
                    handler.postDelayed(returnToScanning, delayAfterError);
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    setStatus(R.drawable.fingerprint_error, fingerprintError, circleError);
                    handler.postDelayed(returnToScanning, delayAfterError);
                }

                @Override
                public void onAuthenticationSucceeded(final FingerprintManager.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    handler.removeCallbacks(returnToScanning);
                    setStatus(R.drawable.fingerprint_success, fingerprintSuccess, circleSuccess);
                    if(fingerprintViewSecureCallback!=null){
                        fingerprintViewSecureCallback.onAuthenticationSuccess();
                    }
                    else{
                        fingerprintViewCallback.onAuthenticationSuccess();
                    }
                    tryCounter = 0;
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    setStatus(R.drawable.fingerprint_error, fingerprintError, circleError);
                    handler.postDelayed(returnToScanning, delayAfterError);
                    tryCounter++;
                    if(counterCallback!=null && tryCounter==limit){
                        counterCallback.onTryLimitReached();
                    }
                }
            }, null);
        }
        else{
            Log.e(TAG, "Fingerprint scanner not detected or no fingerprint enrolled. Use FingerprintDialog#isAvailable(Context) before.");
        }
    }

    public void cancel(){
        cancellationSignal.cancel();
        returnToScanning.run();
    }

    private Runnable returnToScanning = new Runnable() {
        @Override
        public void run() {
            setStatus(R.drawable.fingerprint, fingerprintScanning, circleScanning);
        }
    };
}
