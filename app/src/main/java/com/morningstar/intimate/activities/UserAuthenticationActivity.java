/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.morningstar.intimate.R;
import com.morningstar.intimate.helpers.FingerprintHandler;
import com.morningstar.intimate.managers.BiometricManager;
import com.morningstar.intimate.managers.ConstantManager;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserAuthenticationActivity extends AppCompatActivity {

    private static final String TAG = "UserAuthActivity";

    @BindView(R.id.setUpFingerprint)
    TextView textViewSetFingerprint;
    @BindView(R.id.setUpPinCode)
    TextView textViewSetPin;

    private CancellationSignal cancellationSignal;
    private BiometricPrompt.AuthenticationCallback authenticationCallback;
    private BiometricPrompt biometricPrompt;
    private KeyStore keyStore;
    private Cipher cipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_authentication);
        ButterKnife.bind(this);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.setUpFingerprint)
    public void setUpFingerprint() {
        biometricPrompt = BiometricManager.getBiometricPrompt(this);

        if (biometricPrompt != null) {
            if (BiometricManager.getAuthenticationCallback(this) != null) {
                cancellationSignal = BiometricManager.getCancellationSignal();
                cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(UserAuthenticationActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                authenticationCallback = BiometricManager.getAuthenticationCallback(this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    biometricPrompt.authenticate(cancellationSignal, getMainExecutor(), authenticationCallback);
                }
            }
        } else {
            showDialogWhenPromptDisabled();
        }
    }

    @OnClick(R.id.setUpPinCode)
    public void setUpPinCode() {
        startActivity(new Intent(UserAuthenticationActivity.this, PinAuthActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showDialogWhenPromptDisabled() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if (fingerprintManager.isHardwareDetected()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                if (keyguardManager.isKeyguardSecure()) {
                    generateKey();

                    if (cipherInit()) {
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
                        fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(ConstantManager.FINGERPRINT_KEY,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        try {
            keyStore.load(null);
            if (keyGenerator != null) {
                keyGenerator.init(new
                        KeyGenParameterSpec.Builder(ConstantManager.FINGERPRINT_KEY,
                        KeyProperties.PURPOSE_ENCRYPT |
                                KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
            }
            if (keyGenerator != null) {
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
    }
}
