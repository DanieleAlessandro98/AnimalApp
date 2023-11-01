package it.uniba.dib.sms222334.Models;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.R;

public class Photo extends Media implements Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(getPath());
        dest.writeParcelable(getTimestamp(),flags);
        dest.writeParcelable(getPhoto(),flags);
    }


    protected Photo(Parcel in) {
        super(in.readString(),in.readParcelable(Timestamp.class.getClassLoader()));

        this.photo=in.readParcelable(Bitmap.class.getClassLoader());
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
