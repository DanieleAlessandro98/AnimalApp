package it.uniba.dib.sms222334.Models;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;

public class Video extends Media implements Parcelable{
    private final static String TAG="video";

    private MediaPlayer mediaPlayer;

    Uri video;
    public Video(String path, Timestamp timestamp) {
        super(path,timestamp);

        this.video=null;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public Uri getVideo() {
        return video;
    }

    public void setVideo( UserCallback.UserStateListener listener) {
        new MediaDao().getVideoUri(super.getPath(), new MediaDao.VideoUriListener() {
            @Override
            public void onUriFounded(Uri uri){
                video=uri;
                listener.notifyItemLoaded();
            }

            @Override
            public void onUriNotFounded(Exception exception) {
                Log.d(TAG,"Errore nel caricamento del video: "+exception.getMessage());
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
        dest.writeParcelable(getVideo(),flags);
    }

    protected Video(Parcel in) {
        super(in.readString(),in.readParcelable(Timestamp.class.getClassLoader()));

        this.video=in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
