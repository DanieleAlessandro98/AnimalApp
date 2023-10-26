package it.uniba.dib.sms222334.Models;

import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;

public class Video extends Media{
    private final static String TAG="video";

    Uri video;
    public Video(String path, Timestamp timestamp) {
        super(path,timestamp);

        this.video=null;
    }

    public Uri getVideo() {
        return video;
    }

    public void setVideo(UserCallback.UserStateListener listener) {
        new MediaDao().getVideoUri(super.getPath(), new MediaDao.VideoUriListener() {
            @Override
            public void onUriFounded(Uri uri) {
                video=uri;
                listener.notifyItemLoaded();
            }

            @Override
            public void onUriNotFounded(Exception exception) {
                Log.d(TAG,"Errore nel caricamento del video: "+exception.getMessage());
            }
        });
    }
}
