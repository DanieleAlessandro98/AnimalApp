package it.uniba.dib.sms222334.Database.Dao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.annotation.Nullable;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Utils.Media;

public class MediaDao {
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    public void uploadPhoto(Bitmap bitmap, String path, String fileName, final PhotoUploadListener listener) {
        StorageReference storageRef = storage.getReference()
                .child(path)
                .child(fileName);

        UploadTask uploadTask = storageRef.putBytes(Media.getBytesFromBitmap(bitmap));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            listener.onPhotoUploaded();
        }).addOnFailureListener(e -> {
            listener.onPhotoUploadFailed(e);
        });
    }

    public void uploadVideo(Uri storageUri, String fileName, final VideoUploadListener listener){
        StorageReference videoRef = storage.getReference().child(Animal.VIDEO_PATH + fileName);

        videoRef.putFile(storageUri)
                .addOnProgressListener(listener::onVideoUploadProgress)
                .addOnSuccessListener(taskSnapshot -> {
                    listener.onVideoUploaded();
                })
                .addOnFailureListener(listener::onVideoUploadFailed);
    }

    public void downloadPhoto(String imagePath, final PhotoDownloadListener listener) {
        StorageReference imageRef = storage.getReference().child(imagePath);

        imageRef.getBytes(Media.IMAGE_DOWNLOADED_SIZE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = Media.getBitmapFromBytes(bytes);
            listener.onPhotoDownloaded(bitmap);
        }).addOnFailureListener(listener::onPhotoDownloadFailed);
    }

    public void deleteMedia(String mediaPath, MediaDeleteListener listener){
        StorageReference photoRef = storage.getReference().child(mediaPath);

        photoRef.delete()
                .addOnSuccessListener(command -> listener.mediaDeletedSuccessfully())
                .addOnFailureListener(listener::mediaDeletedFailed);
    }

    public void getVideoUri(String videoPath, final VideoUriListener listener){
        StorageReference videoRef = storage.getReference().child(videoPath);

        videoRef.getDownloadUrl()
                .addOnSuccessListener(listener::onUriFounded)
                .addOnFailureListener(listener::onUriNotFounded);
    }

    public interface PhotoDownloadListener {
        void onPhotoDownloaded(Bitmap bitmap);
        void onPhotoDownloadFailed(Exception exception);

    }
    public interface VideoUriListener {
        void onUriFounded(Uri uri);
        void onUriNotFounded(Exception exception);
    }

    public interface MediaDeleteListener {
        void mediaDeletedSuccessfully();
        void mediaDeletedFailed(Exception exception);
    }

    public interface PhotoUploadListener {
        void onPhotoUploaded();
        void onPhotoUploadFailed(Exception exception);
    }

    public interface VideoUploadListener {
        void onVideoUploaded();

        void onVideoUploadProgress(UploadTask.TaskSnapshot snapshot);
        void onVideoUploadFailed(Exception exception);
    }
}
