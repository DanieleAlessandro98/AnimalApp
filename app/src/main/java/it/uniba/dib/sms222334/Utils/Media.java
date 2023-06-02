package it.uniba.dib.sms222334.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Media {
    public final static String PROFILE_PHOTO_PATH = "images/profiles/users/";
    public final static int IMAGE_DOWNLOADED_SIZE = 1024 * 1024 * 1024;
    public final static String PROFILE_PHOTO_EXTENSION = ".jpg";

    public static byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        return bytes.toByteArray();
    }

    public static Bitmap getBitmapFromBytes(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap getBitmapFromUri(Uri uri, Context context) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();

        ParcelFileDescriptor parcelFileDescriptor = null;

        try {
            parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

            return BitmapFactory.decodeFileDescriptor(fileDescriptor);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (parcelFileDescriptor != null) {
                try {
                    parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

}
