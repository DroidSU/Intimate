/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.helpers;

import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.P)
public class FingerprintAuthHelper extends BiometricPrompt.AuthenticationCallback {

    private FingerprintAuthHelper authListener;

    public FingerprintAuthHelper(FingerprintAuthHelper authListener) {
        this.authListener = authListener;
    }

    //interface for the listener
    interface FingerPrintHelperListener {
    }
}
