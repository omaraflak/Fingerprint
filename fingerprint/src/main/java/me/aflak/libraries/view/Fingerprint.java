package me.aflak.libraries.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import me.aflak.libraries.R;
import me.aflak.libraries.callback.FailAuthCounterCallback;
import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.callback.FingerprintSecureCallback;
import me.aflak.libraries.utils.CipherHelper;
import me.aflak.libraries.utils.FingerprintToken;

/**
 * Created by Omar on 08/01/2018.
 */

public class Fingerprint extends RelativeLayout {
    private final static String TAG = "FingerprintView";

    private View fingerprintImageView;
    private View circleView;

    private FingerprintManager fingerprintManager;
    private CancellationSignal cancellationSignal;
    private FingerprintCallback fingerprintCallback;
    private FingerprintSecureCallback fingerprintSecureCallback;
    private FailAuthCounterCallback counterCallback;
    private FingerprintManager.CryptoObject cryptoObject;
    private CipherHelper cipherHelper;
    private Handler handler;

    private int fingerprintScanning, fingerprintSuccess, fingerprintError;
    private int circleScanning, circleSuccess, circleError;

    private int limit, tryCounter;
    private int delayAfterError;
    private int size;

    public final static int DEFAULT_DELAY_AFTER_ERROR = 1200;
    public final static int DEFAULT_CIRCLE_SIZE = 50;
    public final static int DEFAULT_FINGERPRINT_SIZE = 30;
    public final static float SCALE = (float) DEFAULT_FINGERPRINT_SIZE/DEFAULT_CIRCLE_SIZE;

    public Fingerprint(Context context) {
        super(context);
    }

    public Fingerprint(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs, 0, 0);
        initView(context);
    }

    public Fingerprint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs, defStyleAttr, 0);
        initView(context);
    }

    public Fingerprint(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttributes(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    @SuppressLint("ResourceType")
    private void initAttributes(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes){
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Fingerprint, defStyleAttr, defStyleRes);
        try {
            fingerprintScanning = a.getResourceId(R.styleable.Fingerprint_fingerprintScanningColor, R.color.fingerprint_scanning);
            fingerprintSuccess = a.getResourceId(R.styleable.Fingerprint_fingerprintSuccessColor, R.color.fingerprint_success);
            fingerprintError = a.getResourceId(R.styleable.Fingerprint_fingerprintErrorColor, R.color.fingerprint_error);

            circleScanning = a.getResourceId(R.styleable.Fingerprint_circleScanningColor, R.color.circle_scanning);
            circleSuccess = a.getResourceId(R.styleable.Fingerprint_circleSuccessColor, R.color.circle_success);
            circleError = a.getResourceId(R.styleable.Fingerprint_circleErrorColor, R.color.circle_error);
        } finally {
            a.recycle();
        }

        int[] systemAttrs = {android.R.attr.layout_width, android.R.attr.layout_height};
        TypedArray ta = context.obtainStyledAttributes(attrs, systemAttrs);
        try {
            int width = ta.getLayoutDimension(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            int height = ta.getLayoutDimension(1, ViewGroup.LayoutParams.WRAP_CONTENT);
            if(width==-2 || height==-2) {
                size = dipToPixels(DEFAULT_CIRCLE_SIZE);
            } else{
                size = width;
            }
        } finally {
            ta.recycle();
        }
    }

    private void initView(Context context){
        this.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        this.cipherHelper = null;
        this.handler = new Handler();
        this.fingerprintCallback = null;
        this.fingerprintSecureCallback = null;
        this.counterCallback = null;
        this.cryptoObject = null;
        this.tryCounter = 0;
        this.delayAfterError = DEFAULT_DELAY_AFTER_ERROR;

        int fingerprintSize = (int) (size*SCALE);
        int circleSize = size;

        fingerprintImageView = new AppCompatImageView(context);
        fingerprintImageView.setLayoutParams(new RelativeLayout.LayoutParams(fingerprintSize, fingerprintSize));
        fingerprintImageView.setBackgroundResource(R.drawable.fingerprint);
        ((RelativeLayout.LayoutParams)fingerprintImageView.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        circleView = new View(context);
        circleView.setLayoutParams(new RelativeLayout.LayoutParams(circleSize, circleSize));
        circleView.setBackgroundResource(R.drawable.circle);
        ((RelativeLayout.LayoutParams)circleView.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        addView(circleView);
        addView(fingerprintImageView);

        setStatus(R.drawable.fingerprint, fingerprintScanning, circleScanning);
    }

    private int dipToPixels(int dipValue) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    private void setStatus(int drawableId, int drawableColorId, int circleColorId){
        Context context = getContext();
        fingerprintImageView.setBackgroundResource(drawableId);
        fingerprintImageView.setBackgroundTintList(ColorStateList.valueOf(context.getColor(drawableColorId)));
        circleView.setBackgroundTintList(ColorStateList.valueOf(context.getColor(circleColorId)));
    }

    private Runnable returnToScanning = new Runnable() {
        @Override
        public void run() {
            setStatus(R.drawable.fingerprint, fingerprintScanning, circleScanning);
        }
    };

    private Runnable checkForLimit = new Runnable() {
        @Override
        public void run() {
            if(counterCallback!=null && ++tryCounter==limit){
                counterCallback.onTryLimitReached(Fingerprint.this);
            }
        }
    };

    /**
     * Set fingerprint callback.
     * @param fingerprintCallback callback
     * @return the Fingerprint object itself
     */
    public Fingerprint callback(FingerprintCallback fingerprintCallback){
        this.fingerprintCallback = fingerprintCallback;
        return this;
    }

    /**
     * Set fingerprint secure callback.
     * @param fingerprintSecureCallback secure callback
     * @param KEY_NAME key that will be used for the cipher
     * @return the Fingerprint object itself
     */
    public Fingerprint callback(FingerprintSecureCallback fingerprintSecureCallback, String KEY_NAME){
        this.fingerprintSecureCallback = fingerprintSecureCallback;
        this.cipherHelper = new CipherHelper(KEY_NAME);
        return this;
    }

    /**
     * Set a CryptoObject which is going to be unlocked by the fingerprint.
     * @param cryptoObject CryptoObject to be unlocked
     * @return the Fingerprint object itself
     */
    public Fingerprint cryptoObject(FingerprintManager.CryptoObject cryptoObject){
        this.cryptoObject = cryptoObject;
        return this;
    }

    /**
     * Set the fingerprint icon color in scanning state.
     * @param fingerprintScanning color id
     * @return the Fingerprint object itself
     */
    public Fingerprint fingerprintScanningColor(int fingerprintScanning) {
        this.fingerprintScanning = fingerprintScanning;
        this.fingerprintImageView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(fingerprintScanning)));
        return this;
    }

    /**
     * Set the fingerprint icon color in success state.
     * @param fingerprintSuccess color id
     * @return the Fingerprint object itself
     */
    public Fingerprint fingerprintSuccessColor(int fingerprintSuccess) {
        this.fingerprintSuccess = fingerprintSuccess;
        this.fingerprintImageView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(fingerprintSuccess)));
        return this;
    }

    /**
     * Set the fingerprint icon color in error state.
     * @param fingerprintError color id
     * @return the Fingerprint object itself
     */
    public Fingerprint fingerprintErrorColor(int fingerprintError) {
        this.fingerprintError = fingerprintError;
        this.fingerprintImageView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(fingerprintError)));
        return this;
    }

    /**
     * Set the fingerprint circular background color in scanning state.
     * @param circleScanning color id
     * @return the Fingerprint object itself
     */
    public Fingerprint circleScanningColor(int circleScanning) {
        this.circleScanning = circleScanning;
        this.circleView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(circleScanning)));
        return this;
    }

    /**
     * Set the fingerprint circular background color in success state.
     * @param circleSuccess color id
     * @return the Fingerprint object itself
     */
    public Fingerprint circleSuccessColor(int circleSuccess) {
        this.circleSuccess = circleSuccess;
        this.circleView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(circleSuccess)));
        return this;
    }

    /**
     * Set the fingerprint circular background color in error state.
     * @param circleError color id
     * @return the Fingerprint object itself
     */
    public Fingerprint circleErrorColor(int circleError) {
        this.circleError = circleError;
        this.circleView.setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(circleError)));
        return this;
    }

    /**
     * Set a failing authentication limit. Android will block automatically the fingerprint scanner when 5 attempts will have failed.
     * @param limit the number of fails accepted
     * @param counterCallback a callback triggered when limit is reached
     * @return the Fingerprint object itself
     */
    public Fingerprint tryLimit(int limit, FailAuthCounterCallback counterCallback){
        this.limit = limit;
        this.counterCallback = counterCallback;
        return this;
    }

    /**
     * Set delay before scanning again after a failed authentication.
     * @param delayAfterError the delay in milliseconds
     * @return the Fingerprint object itself
     */
    public Fingerprint delayAfterError(int delayAfterError){
        this.delayAfterError = delayAfterError;
        return this;
    }

    /**
     * Check if fingerprint authentication is supported by the device and if a fingerprint is enrolled in the device.
     * @param context an activity context
     * @return a boolean value
     */
    public static boolean isAvailable(Context context){
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
        if(fingerprintManager!=null){
            return (fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints());
        }
        return false;
    }

    /**
     * start fingerprint scan
     */
    public void authenticate(){
        if(fingerprintSecureCallback!=null){
            if(cryptoObject!=null){
                throw new RuntimeException("If you specify a CryptoObject you have to use FingerprintCallback");
            }
            cryptoObject = cipherHelper.getEncryptionCryptoObject();
            if(cryptoObject==null) {
                fingerprintSecureCallback.onNewFingerprintEnrolled(new FingerprintToken(cipherHelper));
            }
        }
        else if(fingerprintCallback==null){
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
                    if(fingerprintSecureCallback!=null){
                        fingerprintSecureCallback.onAuthenticationError(errorCode, errString.toString());
                    } else{
                        fingerprintCallback.onAuthenticationError(errorCode, errString.toString());
                    }
                }

                @Override
                public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                    super.onAuthenticationHelp(helpCode, helpString);
                    setStatus(R.drawable.fingerprint_error, fingerprintError, circleError);
                    handler.postDelayed(returnToScanning, delayAfterError);
                    if(fingerprintSecureCallback!=null){
                        fingerprintSecureCallback.onAuthenticationError(helpCode, helpString.toString());
                    } else{
                        fingerprintCallback.onAuthenticationError(helpCode, helpString.toString());
                    }
                }

                @Override
                public void onAuthenticationSucceeded(final FingerprintManager.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    handler.removeCallbacks(returnToScanning);
                    setStatus(R.drawable.fingerprint_success, fingerprintSuccess, circleSuccess);
                    if(fingerprintSecureCallback!=null){
                        fingerprintSecureCallback.onAuthenticationSucceeded();
                    }
                    else{
                        fingerprintCallback.onAuthenticationSucceeded();
                    }
                    tryCounter = 0;
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    setStatus(R.drawable.fingerprint_error, fingerprintError, circleError);
                    handler.postDelayed(returnToScanning, delayAfterError);
                    handler.postDelayed(checkForLimit, delayAfterError);
                    if(fingerprintSecureCallback!=null){
                        fingerprintSecureCallback.onAuthenticationFailed();
                    }
                    else{
                        fingerprintCallback.onAuthenticationFailed();
                    }
                }
            }, null);
        }
        else{
            Log.e(TAG, "Fingerprint scanner not detected or no fingerprint enrolled. Use FingerprintView#isAvailable(Context) before.");
        }
    }

    /**
     * cancel fingerprint scan
     */
    public void cancel(){
        cancellationSignal.cancel();
        returnToScanning.run();
    }
}