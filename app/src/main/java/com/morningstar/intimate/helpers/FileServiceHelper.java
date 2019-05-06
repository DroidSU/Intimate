/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.morningstar.intimate.managers.UtilityManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileServiceHelper {

    private static final String TAG = "FileServiceHelper";

    public static void deleteFile(Context context, Uri uri) {
        File deleteFile = new File(uri.getPath());
        if (deleteFile.exists())
            if (!deleteFile.delete())
                UtilityManager.notifyUser(context, "Original file could not be deleted");
    }

    public static void refreshGallery(Context context, Uri uri) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    public static String copyFileFromUri(Uri uri, File appFolder, String photoPrimaryKey) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(new File(uri.getPath()));
            outputStream = new FileOutputStream(appFolder + "/image_" + photoPrimaryKey);

            FileChannel inputChannel = inputStream.getChannel();
            FileChannel outChannel = outputStream.getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outChannel);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        Uri uri1 = Uri.fromFile(new File(appFolder + "/image_" + photoPrimaryKey));
        return UtilityManager.convertUriToString(uri1);
    }

    public static String restoreFileToUri(Uri fromUri, Uri destUri) {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(new File(fromUri.getPath()));
            outputStream = new FileOutputStream(new File(destUri.getPath()));

            FileChannel inputChannel = inputStream.getChannel();
            FileChannel outChannel = outputStream.getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outChannel);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

        Uri uri1 = Uri.fromFile(new File(destUri.getPath()));
        return UtilityManager.convertUriToString(uri1);
    }

    public static File createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
