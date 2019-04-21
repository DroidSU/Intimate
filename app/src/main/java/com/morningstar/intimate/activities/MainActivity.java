/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.morningstar.intimate.R;
import com.morningstar.intimate.managers.ConstantManager;
import com.morningstar.intimate.managers.UtilityManager;
import com.morningstar.intimate.pojos.realmpojos.Photos;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private static final String[] MENU_ITEMS_NAMES = {"New Photo", "New Video"};
    private static final int[] MENU_ITEMS_COLORS = {R.color.colorPrimary, R.color.secondaryDarkColor};
    private static final int[] ITEM_DRAWABLES = {R.drawable.ic_add_photos_black_24dp, R.drawable.ic_video_black_24dp};

    @BindView(R.id.mainActivityToolbar)
    Toolbar toolbar;
    @BindView(R.id.arcMenu)
    ArcMenu arcMenu;
    @BindView(R.id.textViewPhotoTotalCount)
    TextView textViewTotalPhotos;
    @BindView(R.id.containerPhotos)
    LinearLayout linearLayoutPhotoContainer;

    private PermissionListener permissionListener;
    private Realm realm;
    private RealmResults<Photos> photosRealmResults;
    private long photoPrimaryKey;
    private File imageFile;
    private File appFolder;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initialiseArcMenu();
        initArcMenu(arcMenu);
        realm = Realm.getDefaultInstance();

        sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);

        createAppFolder();
        displayTotalObjects();
    }

    private void createAppFolder() {
        appFolder = new File(Environment.getExternalStorageDirectory() + "/Intimate", "Photos");
        if (!appFolder.exists())
            appFolder.mkdirs();
    }

    @OnClick(R.id.containerPhotos)
    public void openPhotoGallery() {
        startActivity(new Intent(MainActivity.this, ViewPhotosActivity.class));
    }

    @SuppressLint("SetTextI18n")
    private void displayTotalObjects() {
        photosRealmResults = realm.where(Photos.class).findAll();
        if (photosRealmResults != null) {
            textViewTotalPhotos.setText(photosRealmResults.size() + " Photos");
            photoPrimaryKey = photosRealmResults.size();
        } else {
            textViewTotalPhotos.setText("0 Photos");
            photoPrimaryKey = 0;
        }
    }

    private void initialiseArcMenu() {
        arcMenu.setToolTipTextSize(14);
        arcMenu.setMinRadius(60);
        arcMenu.setArc(175, 255);
        arcMenu.setToolTipSide(ArcMenu.TOOLTIP_LEFT);
        arcMenu.setToolTipTextColor(Color.WHITE);
        arcMenu.setToolTipBackColor(Color.parseColor("#88000000"));
        arcMenu.setToolTipCorner(4f);  //set tooltip corner
        arcMenu.setToolTipPadding(4f);  //set tooltip padding
        arcMenu.setColorNormal(getResources().getColor(R.color.md_green_700));
        arcMenu.showTooltip(true);
        arcMenu.setDuration(200);
    }

    private void initArcMenu(final ArcMenu menu) {
        for (int i = 0; i < MainActivity.ITEM_DRAWABLES.length; i++) {
            FloatingActionButton item = new FloatingActionButton(this);  //Use internal fab as a child
            item.setSize(FloatingActionButton.SIZE_MINI);  //set minimum size for fab 42dp
            item.setShadow(true); //enable to draw shadow
            item.setBackgroundColor(getResources().getColor(MENU_ITEMS_COLORS[i]));
            item.setIcon(MainActivity.ITEM_DRAWABLES[i]); //add icon for fab
            menu.setChildSize(item.getIntrinsicHeight()); // fit menu child size exactly same as fab

            final int position = i;
            menu.addItem(item, MainActivity.MENU_ITEMS_NAMES[i], new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkForPermissions();
                }
            });
        }
    }

    private void checkForPermissions() {
        TedPermission.with(this)
                .setPermissionListener(getPermissionListener())
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }

    public PermissionListener getPermissionListener() {
        permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                getSelectedImages();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };
        return permissionListener;
    }

    private void getSelectedImages() {
        TedBottomPicker.with(MainActivity.this)
                .setPeekHeight(1600)
                .showTitle(false)
                .setCompleteButtonText("Done")
                .setEmptySelectionText("None Selected")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setTitle("Please wait");
                                progressDialog.setCancelable(false);
                                progressDialog.show();
                                for (Uri uri : uriList) {
                                    imageFile = new File(uri.getPath());
                                    photoPrimaryKey += 1;
                                    Photos photos = realm.createObject(Photos.class, photoPrimaryKey);
                                    photos.setPhotoOldUriAsString(UtilityManager.convertUriToString(uri));
                                    String encodedImage = convertImageToBase64();
                                    photos.setImageBase64(encodedImage);
                                    String newUri = copyFileFromUri(uri);
                                    photos.setPhotoNewUriAsString(newUri);
//                                    deleteOriginalFile(uri);
                                }
                                displayTotalObjects();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
    }

    private String convertImageToBase64() {
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void deleteOriginalFile(Uri uri) {
        File deleteFile = new File(uri.getPath());
        if (deleteFile.exists())
            if (deleteFile.delete())
                Toast.makeText(this, "File deleted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Could not delete", Toast.LENGTH_SHORT).show();

        refreshGallery();
    }

    private void refreshGallery() {
        MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
            public void onScanCompleted(String path, Uri uri) {
                Log.i(TAG, "Scanned " + path + ":");
                Log.i(TAG, "-> uri=" + uri);
            }
        });
    }

    private String copyFileFromUri(Uri uri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            ContentResolver contentResolver = getContentResolver();
            inputStream = contentResolver.openInputStream(uri);
            outputStream = new FileOutputStream(appFolder + "image_" + photoPrimaryKey + ".jpg");
            byte[] buffer = new byte[2000];
            int bytesRead = 0;
            if (inputStream != null) {
                while ((bytesRead == inputStream.read(buffer, 0, buffer.length))) {
                    outputStream.write(buffer, 0, buffer.length);
                }
            }
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        Uri uri1 = Uri.fromFile(new File(appFolder + "image_" + photoPrimaryKey + ".jpg"));
        return UtilityManager.convertUriToString(uri1);
    }

    @Override
    protected void onDestroy() {
        if (realm != null && !realm.isClosed())
            realm.close();
        super.onDestroy();
    }
}
