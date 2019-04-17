/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.Toast;

import com.morningstar.intimate.activities.MainActivity;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
        context.startActivity(new Intent(context, MainActivity.class));
        ((Activity) context).finish();
        super.onAuthenticationSucceeded(result);
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, "failed", Toast.LENGTH_SHORT).show();
        super.onAuthenticationFailed();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        super.onAuthenticationError(errorCode, errString);
    }
}
