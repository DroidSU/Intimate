/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("default.realm").build();
        Realm.setDefaultConfiguration(config);
        MobileAds.initialize(this, getResources().getString(R.string.admob_id));
    }
}
