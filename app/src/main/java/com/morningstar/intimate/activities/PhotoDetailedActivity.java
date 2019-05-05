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
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.morningstar.intimate.R;
import com.morningstar.intimate.helpers.FileServiceHelper;
import com.morningstar.intimate.managers.UtilityManager;
import com.morningstar.intimate.pojos.realmpojos.Photos;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class PhotoDetailedActivity extends AppCompatActivity {

    @BindView(R.id.imageViewDetailedPhoto)
    ImageView imageView;
    @BindView(R.id.rootLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.detailedPhotoToolbar)
    Toolbar toolbar;

    private Photos photo;
    private int photoid;
    private Realm realm;

    private boolean isFullScreen;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photo_detailed);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Photo");
        getSupportActionBar().hide();

        photoid = getIntent().getIntExtra(Photos.ID, 0);
        realm = Realm.getDefaultInstance();
        isFullScreen = true;

        getPhotoFromRealm();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFullScreen) {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getSupportActionBar().show();
                    isFullScreen = false;
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    getSupportActionBar().hide();
                    isFullScreen = true;
                }
            }
        });
    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detailed_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            Toast.makeText(this, "Wait", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.restore) {
            //get the original and the current uri
            Uri currentUri = UtilityManager.convertStringToUri(photo.getPhotoNewUriAsString());
            Uri originalUri = UtilityManager.convertStringToUri(photo.getPhotoOldUriAsString());
            //create the new file
            FileServiceHelper.createFile(originalUri.getPath());
            //move current file to restored file path
            String restoredUri = FileServiceHelper.restoreFileToUri(currentUri, originalUri);
            //remove object from db
            deleteObjectFromRealm();
            //delete file from current path
            FileServiceHelper.deleteFile(PhotoDetailedActivity.this, currentUri);
            //refresh the gallery
            FileServiceHelper.refreshGallery(this, UtilityManager.convertStringToUri(restoredUri));
            Toast.makeText(this, "done!", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return true;
    }

    private void deleteObjectFromRealm() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<Photos> photosRealmResults = realm.where(Photos.class).equalTo(Photos.ID, photoid).findAll();
                photosRealmResults.deleteAllFromRealm();
            }
        });
    }
}
