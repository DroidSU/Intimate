/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.morningstar.intimate.R;
import com.morningstar.intimate.pojos.realmpojos.Photos;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class PhotoDetailedActivity extends AppCompatActivity {

    @BindView(R.id.imageViewDetailedPhoto)
    ImageView imageView;
    @BindView(R.id.rootLayout)
    CoordinatorLayout coordinatorLayout;

    private Photos photo;
    private int photoid;
    private Realm realm;

    private boolean isFullScreen;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detailed);
        ButterKnife.bind(this);

        photoid = getIntent().getIntExtra(Photos.ID, 0);
        realm = Realm.getDefaultInstance();

        getPhotoFromRealm();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    isFullScreen = false;
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    isFullScreen = true;
                }
            }
        });
    }

    private void getPhotoFromRealm() {
        photo = realm.where(Photos.class).equalTo(Photos.ID, photoid).findFirst();
        byte[] decodedImage = new byte[0];
        if (photo != null) {
            decodedImage = Base64.decode(photo.getImageBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            imageView.setImageBitmap(decodedByte);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
