/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import com.morningstar.intimate.activities.MainActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

public class BiometricManager {

    private static CancellationSignal cancellationSignal;
    private static BiometricPrompt.AuthenticationCallback authenticationCallback;
    private static BiometricPrompt biometricPrompt;

    public static BiometricPrompt getBiometricPrompt(Context context) {
        if (BiometricManager.isSdkVersionSupportedForFingerprint() && BiometricManager.isHardwareSupportedForFingerprint(context)
                && BiometricManager.isFingerPrintPermissionGranted(context)) {
            if (BiometricManager.isBiometricPromptEnabled()) {
                if (BiometricManager.isFingerprintAvailable(context)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        biometricPrompt = new BiometricPrompt.Builder(context)
                                .setTitle("Intimate")
                                .setDescription("Your fingerprint will be used to authenticate into the app")
                                .setNegativeButton("Cancel", context.getMainExecutor(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .build();
                    }
                }
            } else {
                Toast.makeText(context, "Not possible", Toast.LENGTH_SHORT).show();
                biometricPrompt = null;
            }
        } else {
            Toast.makeText(context, "Not possible", Toast.LENGTH_SHORT).show();
            biometricPrompt = null;
        }

        return biometricPrompt;
    }

    public static BiometricPrompt.AuthenticationCallback getAuthenticationCallback(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            if (authenticationCallback == null) {
                return new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        UtilityManager.notifyUser(context, "Failed");
                        super.onAuthenticationError(errorCode, errString);
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                    }

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(ConstantManager.IS_BIOMETRIC_SET, true);
                        editor.apply();
                        context.startActivity(new Intent(context, MainActivity.class));
                        ((Activity) context).finish();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        UtilityManager.notifyUser(context, "Failed");
                        super.onAuthenticationFailed();
                    }
                };
            } else {
                return authenticationCallback;
            }
        } else
            return null;
    }

    public static CancellationSignal getCancellationSignal() {
        if (cancellationSignal == null) {
            cancellationSignal = new CancellationSignal();
            cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
                @Override
                public void onCancel() {

                }
            });
        }
        return cancellationSignal;
    }

    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }

    /*
        Check if android version is greater than Marshmellow
     */
    public static boolean isSdkVersionSupportedForFingerprint() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    //check if device has a fingerprint sensor
    public static boolean isHardwareSupportedForFingerprint(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.isHardwareDetected();
    }

    //check if user has a registered fingerprint
    public static boolean isFingerprintAvailable(Context context) {
        FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
        return fingerprintManager.hasEnrolledFingerprints();
    }

    //check if fingerprint permission has been given
    public static boolean isFingerPrintPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                    PackageManager.PERMISSION_GRANTED;
        } else
            return false;
    }
}
