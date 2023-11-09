package it.uniba.dib.sms222334.Views.Carousel;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Models.Media;
import it.uniba.dib.sms222334.Models.Photo;
import it.uniba.dib.sms222334.Models.Video;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.DateUtilities;

public class MediaPageFragment extends Fragment implements MediaDao.MediaDeleteListener{
    private static final String TAG="MediaPageFragment";

    public SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    public static MediaPageFragment newInstance(Context context,boolean isMyAnimal, Media media, float scale) throws IllegalAccessException, java.lang.InstantiationException {
        MediaPageFragment newInstance=new MediaPageFragment();

        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        newInstance.editor= preferences.edit();
        newInstance.editor.putFloat("scale", scale);
        newInstance.editor.putBoolean("is_my_animal",isMyAnimal);
        newInstance.editor.putString("mediaData", new Gson().toJson(media));
        newInstance.editor.commit();

        return newInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String mediaString=preferences.getString("mediaData","");

        boolean is_my_animal=preferences.getBoolean("is_my_animal",false);

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(mediaString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Media media;

        try {
            if (jsonObject.getString("path").contains("videos")) {
                media=new Gson().fromJson(mediaString, Video.class);
            }
            else{
                media=new Gson().fromJson(mediaString, Photo.class);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        float scale = preferences.getFloat("scale",1.0f);

        RelativeLayout relativeLayout;


        if(media instanceof Photo){
            relativeLayout = (RelativeLayout)
                    inflater.inflate(R.layout.carousel_photo_item, container, false);

            ImageView image=relativeLayout.findViewById(R.id.image);

            ProgressBar progressBar=relativeLayout.findViewById(R.id.downloadProgressBar);

            Photo photo=((Photo)media);

            if(photo.getPhoto()!=null){
                image.setImageBitmap(photo.getPhoto());
            }
            else{
                progressBar.setVisibility(View.VISIBLE);

                photo.loadPhoto(new UserCallback.UserStateListener() {
                    @Override
                    public void notifyItemLoaded() {
                        image.setImageBitmap(photo.getPhoto());
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void notifyItemUpdated(int position) {

                    }

                    @Override
                    public void notifyItemRemoved(int position) {

                    }
                });
            }
        }
        else{
            relativeLayout = (RelativeLayout)
                    inflater.inflate(R.layout.carousel_video_item, container, false);

            TextureView videoTextureView = relativeLayout.findViewById(R.id.video);

            ProgressBar progressBar=relativeLayout.findViewById(R.id.downloadProgressBar);

            Video video=((Video)media);

            setVideoView(videoTextureView,video,progressBar);

        }


        TextView textView = relativeLayout.findViewById(R.id.time_ago);

        textView.setText(DateUtilities.getTimeAgoString(media.getTimestamp(),getContext()));

        MediaContainer root =relativeLayout.findViewById(R.id.item_root);
        root.setScaleBoth(scale);

        MediaSettingsView mediaSettingsView=relativeLayout.findViewById(R.id.media_settings);

        if(!is_my_animal)
            mediaSettingsView.disableDelete();

        mediaSettingsView.setDeleteAction(() -> {
            media.delete(this);

            return null;
        });

        mediaSettingsView.setMedia(media);


        return relativeLayout;
    }

    private void setVideoView(TextureView videoTextureView, Video video,ProgressBar progressBar){
        AtomicReference<MediaPlayer> mediaPlayer = new AtomicReference<>(new MediaPlayer());

        AtomicReference<CountDownLatch> latch=new AtomicReference<>();

        TextureView.SurfaceTextureListener listener=new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                Thread videoThread = new Thread(() -> {
                    Surface s = new Surface(surface);

                    latch.set(new CountDownLatch(1));

                    try {
                        mediaPlayer.set(new MediaPlayer());
                        mediaPlayer.get().setDataSource(getContext(),video.getVideo());
                        mediaPlayer.get().setSurface(s);
                        mediaPlayer.get().prepare();
                        mediaPlayer.get().setLooping(true);
                        mediaPlayer.get().start();
                        progressBar.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        latch.get().countDown();
                    }
                });



                if(video.getVideo()!=null){ //il video è stato già scaricato
                    videoThread.start();
                }
                else{ //il video non è stato ancora scaricato
                    video.setVideo(new UserCallback.UserStateListener() {
                        @Override
                        public void notifyItemLoaded() {
                            videoThread.start();
                        }

                        @Override
                        public void notifyItemUpdated(int position) {

                        }

                        @Override
                        public void notifyItemRemoved(int position) {

                        }
                    });
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                // Nessuna azione richiesta
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                try {
                    if(latch.get()!=null)
                        latch.get().await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                progressBar.setVisibility(View.VISIBLE);
                mediaPlayer.get().stop();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {
                // Nessuna azione richiesta
            }
        };



        videoTextureView.setSurfaceTextureListener(listener);

    }

    MediaDao.MediaDeleteListener listener;

    public void setMediaDeleteListener(MediaDao.MediaDeleteListener listener){
        this.listener=listener;
    }

    @Override
    public void mediaDeletedSuccessfully() {
        if(listener!=null)
            this.listener.mediaDeletedSuccessfully();
    }

    @Override
    public void mediaDeletedFailed(Exception exception) {
        if(listener!=null)
            this.listener.mediaDeletedFailed(exception);
    }
}
