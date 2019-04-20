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
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.morningstar.intimate.R;
import com.morningstar.intimate.managers.UtilityManager;
import com.morningstar.intimate.pojos.realmpojos.Photos;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String[] MENU_ITEMS_NAMES = {"New Photo", "New Video"};
    private static final int[] MENU_ITEMS_COLORS = {R.color.colorPrimary, R.color.secondaryDarkColor};
    private static final int[] ITEM_DRAWABLES = {R.drawable.ic_add_photos_black_24dp, R.drawable.ic_video_black_24dp};

    @BindView(R.id.mainActivityToolbar)
    Toolbar toolbar;
    @BindView(R.id.arcMenu)
    ArcMenu arcMenu;
    @BindView(R.id.textViewPhotoTotalCount)
    TextView textViewTotalPhotos;

    private PermissionListener permissionListener;
    private Realm realm;
    private RealmResults<Photos> photosRealmResults;
    private long photoPrimaryKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initialiseArcMenu();
        initArcMenu(arcMenu);
        realm = Realm.getDefaultInstance();

        displayTotalObjects();
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
                .setEmptySelectionText("Select None")
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
                                    photoPrimaryKey += 1;
                                    Photos photos = realm.createObject(Photos.class, photoPrimaryKey);
                                    photos.setPhotoUriAsString(UtilityManager.convertUriToString(uri));
                                }
                                displayTotalObjects();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (realm != null && !realm.isClosed())
            realm.close();
        super.onDestroy();
    }
}
