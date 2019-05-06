/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.morningstar.intimate.R;
import com.morningstar.intimate.adapters.ViewPhotosRecyclerAdapter;
import com.morningstar.intimate.pojos.eventpojos.RefreshRealmEvent;
import com.morningstar.intimate.pojos.realmpojos.Photos;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class ViewPhotosActivity extends AppCompatActivity {

    @BindView(R.id.simpleRecyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.simpleNoItemTextView)
    TextView textView;
    @BindView(R.id.viewPhotosToolbar)
    Toolbar toolbar;

    private Realm realm;
    private ViewPhotosRecyclerAdapter viewPhotosRecyclerAdapter;
    private RealmResults<Photos> photosRealmResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photos);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        realm = Realm.getDefaultInstance();

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Photo Vault");

        getPhotos();
        setUpRecyclerView();
    }

    private void getPhotos() {
        photosRealmResults = realm.where(Photos.class).findAll();
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(ViewPhotosActivity.this, 3));
        if (photosRealmResults != null && photosRealmResults.size() != 0) {
            viewPhotosRecyclerAdapter = new ViewPhotosRecyclerAdapter(this, photosRealmResults);
            recyclerView.setAdapter(viewPhotosRecyclerAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewPhotosActivity.this, MainActivity.class));
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRealm(RefreshRealmEvent refreshRealmEvent) {
        getPhotos();
        setUpRecyclerView();
    }
}
