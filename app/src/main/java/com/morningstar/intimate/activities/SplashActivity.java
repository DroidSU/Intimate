/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.morningstar.intimate.R;
import com.morningstar.intimate.managers.ConstantManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
                boolean isAuth = sharedPreferences.getBoolean(ConstantManager.IS_AUTHENTICATED, false);
                boolean isPinSet = sharedPreferences.getBoolean(ConstantManager.IS_PIN_SET, false);
                boolean isBioSet = sharedPreferences.getBoolean(ConstantManager.IS_BIOMETRIC_SET, false);
                if (isAuth) {
                    if (isPinSet || isBioSet) {
                        if (isBioSet) {
                            startActivity(new Intent(SplashActivity.this, FingerprintAuthActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, PinAuthActivity.class));
                            finish();
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, UserAuthenticationActivity.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 100);
    }
}
