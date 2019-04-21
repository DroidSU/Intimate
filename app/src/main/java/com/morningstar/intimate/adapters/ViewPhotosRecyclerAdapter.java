/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.morningstar.intimate.R;
import com.morningstar.intimate.pojos.realmpojos.Photos;

import io.realm.RealmResults;

public class ViewPhotosRecyclerAdapter extends RecyclerView.Adapter<ViewPhotosRecyclerAdapter.ViewPhotosViewHolder> {

    private Context context;
    private View view;
    private RealmResults<Photos> photosRealmResults;

    public ViewPhotosRecyclerAdapter(Context context, RealmResults<Photos> photosRealmResults) {
        this.context = context;
        this.photosRealmResults = photosRealmResults;
    }

    @NonNull
    @Override
    public ViewPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.photo_recycler_item, parent, false);
        return new ViewPhotosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewPhotosViewHolder holder, int position) {
        if (photosRealmResults.get(position) != null) {
            byte[] decodedImage = Base64.decode(photosRealmResults.get(position).getImageBase64(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            holder.imageView.setImageBitmap(decodedByte);
        }
//        if (photosRealmResults.get(position) != null) {
//            Picasso.get().load(photosRealmResults.get(position).getPhotoNewUriAsString()).into(holder.imageView);
//        }
    }

    @Override
    public int getItemCount() {
        return photosRealmResults.size();
    }

    class ViewPhotosViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewPhotosViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.photo_item);
        }
    }
}
