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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.morningstar.intimate.R;
import com.morningstar.intimate.helpers.FileServiceHelper;
import com.morningstar.intimate.managers.ConstantManager;
import com.morningstar.intimate.managers.PrimaryKeyManager;
import com.morningstar.intimate.managers.UtilityManager;
import com.morningstar.intimate.pojos.eventpojos.RefreshRealmEvent;
import com.morningstar.intimate.pojos.realmpojos.Photos;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
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
    @BindView(R.id.textViewVideoTotalCount)
    TextView textViewTotalVideos;
    @BindView(R.id.containerVideos)
    LinearLayout linearLayoutVideoContainer;
    @BindView(R.id.mainActivityAdview)
    AdView adView;

    private PermissionListener permissionListener;
    private Realm realm;
    private RealmResults<Photos> photosRealmResults;
    private long totalPhotoCount;
    private String photoPrimaryKey;
    private File imageFile;
    private File appFolder;
    private String newUri;
    private List<String> newUriList;

    private InterstitialAd interstitialAd;

    private StorageReference storageReference;
    private SharedPreferences sharedPreferences;

    private boolean adBeforePhotoShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        EventBus.getDefault().register(this);
        initialiseArcMenu();
        initArcMenu(arcMenu);
        realm = Realm.getDefaultInstance();

        sharedPreferences = getSharedPreferences(ConstantManager.SHARED_PREF_FILE_NAME, MODE_PRIVATE);
        newUriList = new ArrayList<>();

        createAppFolder();
//        displayTotalObjects();

        loadAd();
        adBeforePhotoShown = false;
    }

    @OnClick(R.id.containerVideos)
    public void openVideoGallery() {
        if (interstitialAd.isLoaded())
            interstitialAd.show();
        else {
            Log.i(TAG, "Ad not loaded");
        }
    }

    private void loadAd() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.demo_interstitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);
        adView.loadAd(adRequest);

        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void createAppFolder() {
        appFolder = new File(Environment.getExternalStorageDirectory() + "/.intimate", "photos");
        if (!appFolder.exists())
            appFolder.mkdirs();
    }

    @OnClick(R.id.containerPhotos)
    public void openPhotoGallery() {
        if (adBeforePhotoShown)
            startActivity(new Intent(MainActivity.this, ViewPhotosActivity.class));
        else {
            if (interstitialAd.isLoaded())
                interstitialAd.show();
            else {
                startActivity(new Intent(MainActivity.this, ViewPhotosActivity.class));
            }
            adBeforePhotoShown = true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void displayTotalObjects() {
        photosRealmResults = realm.where(Photos.class).findAll();
        if (photosRealmResults != null) {
            textViewTotalPhotos.setText(photosRealmResults.size() + " Photos");
            totalPhotoCount = photosRealmResults.size();
        } else {
            textViewTotalPhotos.setText("0 Photos");
            totalPhotoCount = 0;
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
                .setCompleteButtonText("Import")
                .setEmptySelectionText("None Selected")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                for (Uri uri : uriList) {
                                    imageFile = new File(uri.getPath());
                                    totalPhotoCount += 1;
                                    photoPrimaryKey = PrimaryKeyManager.getPrimaryKeyForNewPhoto(totalPhotoCount);
                                    //create object in realm
                                    Photos photos = realm.createObject(Photos.class, photoPrimaryKey);
                                    //store the old uri in case to be restored later
                                    photos.setPhotoOldUriAsString(UtilityManager.convertUriToString(uri));
                                    //get base64 encoded string of image
                                    String encodedImage = UtilityManager.convertImageToBase64(imageFile.getPath());
                                    photos.setImageBase64(encodedImage);
                                    //get the new uri of the moved file
                                    newUri = FileServiceHelper.copyFileFromUri(uri, appFolder, photoPrimaryKey);
                                    newUriList.add(newUri);
                                    //store the new uri
                                    photos.setPhotoNewUriAsString(newUri);
                                    //delete original file and refresh gallery
                                    FileServiceHelper.deleteFile(MainActivity.this, uri);
                                    FileServiceHelper.refreshGallery(MainActivity.this, uri);
                                }
                                displayTotalObjects();
                            }
                        });

//                        uploadToFirebase(newUriList);
                    }
                });
    }

    private void uploadToFirebase(List<String> uriList) {
        for (String uriString : uriList) {
            Uri uri = UtilityManager.convertStringToUri(uriString);
            File file = new File(uri.getPath());
            String fileName = file.getName();
            String email = sharedPreferences.getString(ConstantManager.USER_EMAIL, "example");
            StorageReference userRef = storageReference.child("images/" + email + "/" + fileName);

            userRef.putFile(uri)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (!task.isSuccessful())
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        if (realm != null && !realm.isClosed())
            realm.close();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!interstitialAd.isLoaded())
            loadAd();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRealm(RefreshRealmEvent refreshRealmEvent) {
        displayTotalObjects();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayTotalObjects();
    }
}
