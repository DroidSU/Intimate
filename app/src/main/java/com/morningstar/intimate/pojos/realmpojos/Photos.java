/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.pojos.realmpojos;

import androidx.annotation.Keep;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Keep
public class Photos extends RealmObject {

    @PrimaryKey
    private long photoId;

    private String photoUriAsString;
    private String photoName;

    public long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public String getPhotoUriAsString() {
        return photoUriAsString;
    }

    public void setPhotoUriAsString(String photoUriAsString) {
        this.photoUriAsString = photoUriAsString;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
