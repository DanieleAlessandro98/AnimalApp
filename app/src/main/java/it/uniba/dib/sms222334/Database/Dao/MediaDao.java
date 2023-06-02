package it.uniba.dib.sms222334.Database.Dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import it.uniba.dib.sms222334.Utils.Media;

public class MediaDao {
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public void downloadPhoto(String imagePath, final PhotoDownloadListener listener) {
        StorageReference imageRef = storage.getReference().child(imagePath);

        imageRef.getBytes(Media.IMAGE_DOWNLOADED_SIZE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = Media.getBitmapFromBytes(bytes);
            listener.onPhotoDownloaded(bitmap);
        }).addOnFailureListener(exception -> {
            listener.onPhotoDownloadFailed(exception);
        });
    }

    public void uploadPhoto(Bitmap bitmap, String fileName, final PhotoUploadListener listener) {
        StorageReference storageRef = storage.getReference()
                .child(Media.PROFILE_PHOTO_PATH)
                .child(fileName);

        UploadTask uploadTask = storageRef.putBytes(Media.getBytesFromBitmap(bitmap));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            listener.onPhotoUploaded();
        }).addOnFailureListener(e -> {
            listener.onPhotoUploadFailed(e);
        });
    }

    public interface PhotoDownloadListener {
        void onPhotoDownloaded(Bitmap bitmap);
        void onPhotoDownloadFailed(Exception exception);
    }

    public interface PhotoUploadListener {
        void onPhotoUploaded();
        void onPhotoUploadFailed(Exception exception);
    }
}
