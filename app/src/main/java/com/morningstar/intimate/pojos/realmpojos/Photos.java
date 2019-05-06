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

    public static final String oldUri = "photoOldUriAsString";
    public static final String newUri = "photoNewUriAsString";
    public static final String imageString = "imageBase64";
    public static final String ID = "photoId";

    @PrimaryKey
    private String photoId;

    private String photoOldUriAsString;
    private String photoName;
    private String photoNewUriAsString;
    private String imageBase64;

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    public String getPhotoNewUriAsString() {
        return photoNewUriAsString;
    }

    public void setPhotoNewUriAsString(String photoNewUriAsString) {
        this.photoNewUriAsString = photoNewUriAsString;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getPhotoOldUriAsString() {
        return photoOldUriAsString;
    }

    public void setPhotoOldUriAsString(String photoOldUriAsString) {
        this.photoOldUriAsString = photoOldUriAsString;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }
}
