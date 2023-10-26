package it.uniba.dib.sms222334.Views.Carousel;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.util.JsonUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import io.grpc.internal.JsonUtil;
import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Models.Media;
import it.uniba.dib.sms222334.Models.Photo;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.Video;
import it.uniba.dib.sms222334.Presenters.AnimalPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.DateUtilities;

public class MediaPageFragment extends Fragment implements MediaDao.MediaDeleteListener {
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

        LinearLayout linearLayout;


        if(media instanceof Photo){
            linearLayout = (LinearLayout)
                    inflater.inflate(R.layout.carousel_photo_item, container, false);

            ImageView image=linearLayout.findViewById(R.id.image);

            Photo photo=((Photo)media);

            if(photo.getPhoto()!=null){
                image.setImageBitmap(photo.getPhoto());
            }
            else{
                photo.loadPhoto(new UserCallback.UserStateListener() {
                    @Override
                    public void notifyItemLoaded() {
                        image.setImageBitmap(photo.getPhoto());
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
            linearLayout = (LinearLayout)
                    inflater.inflate(R.layout.carousel_video_item, container, false);

            VideoView videoView=linearLayout.findViewById(R.id.video);

            Video video=((Video)media);

            if(video.getVideo()!=null){
                setVideoView(videoView,video);
            }
            else{
                video.setVideo(new UserCallback.UserStateListener() {
                    @Override
                    public void notifyItemLoaded() {
                        setVideoView(videoView,video);
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


        TextView textView = linearLayout.findViewById(R.id.time_ago);

        textView.setText(DateUtilities.getTimeAgoString(media.getTimestamp(),getContext()));

        MediaContainer root =linearLayout.findViewById(R.id.item_root);
        root.setScaleBoth(scale);

        MediaSettingsView mediaSettingsView=linearLayout.findViewById(R.id.media_settings);

        if(!preferences.getBoolean("is_my_animal",false))
            mediaSettingsView.disableDelete();

        mediaSettingsView.setDeleteAction(() -> {
            media.delete(this);

            return null;
        });

        mediaSettingsView.setMedia(media);


        return linearLayout;
    }

    private void setVideoView(VideoView videoView, Video video){
        videoView.setVideoURI(video.getVideo());

        MediaController mediaController= new MediaController(getContext());

        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        //videoView.start();
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
