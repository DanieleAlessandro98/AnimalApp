package it.uniba.dib.sms222334.Models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.firebase.Timestamp;

import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.R;

public class Photo extends Media {

    Bitmap photo;

    public Photo(String path, Timestamp timestamp) {
        super(path, timestamp);

        photo = null;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void loadPhoto(UserCallback.UserStateListener listener) {
        new MediaDao().downloadPhoto(super.getPath(), new MediaDao.PhotoDownloadListener() {
            @Override
            public void onPhotoDownloaded(Bitmap bitmap) {
                photo = bitmap;
                listener.notifyItemLoaded();
            }

            @Override
            public void onPhotoDownloadFailed(Exception exception) {

            }
        });
    }
}
