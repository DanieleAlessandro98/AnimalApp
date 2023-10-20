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
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.util.JsonUtils;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import io.grpc.internal.JsonUtil;
import it.uniba.dib.sms222334.Models.Media;
import it.uniba.dib.sms222334.Models.Photo;
import it.uniba.dib.sms222334.Models.Video;
import it.uniba.dib.sms222334.R;

public class MediaPageFragment extends Fragment {
    private static final String TAG="MediaPageFragment";

    public SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    public static Fragment newInstance(Context context, int position, Media media, float scale) throws IllegalAccessException, java.lang.InstantiationException {
        MediaPageFragment newInstance=new MediaPageFragment();

        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        newInstance.editor= preferences.edit();
        newInstance.editor.putInt("position", position);
        newInstance.editor.putFloat("scale", scale);
        newInstance.editor.putString("mediaData", new Gson().toJson(media));
        newInstance.editor.commit();

        return newInstance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String mediaString=preferences.getString("mediaData","");


        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(mediaString);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Media media;

        if (jsonObject.has("video")) {
            media=new Gson().fromJson(mediaString, Video.class);
        }
        else{
            media=new Gson().fromJson(mediaString, Photo.class);
        }



        int position = preferences.getInt("position",-1);
        float scale = preferences.getFloat("scale",1.0f);

        LinearLayout linearLayout = null;


        if(media instanceof Photo){
            linearLayout = (LinearLayout)
                    inflater.inflate(R.layout.carousel_photo_item, container, false);
        }
        else{
            linearLayout = (LinearLayout)
                    inflater.inflate(R.layout.carousel_video_item, container, false);

            VideoView video=linearLayout.findViewById(R.id.video);

            Uri videoUri = Uri.parse(((Video)media).getVideo());

            video.setVideoURI(videoUri);

            MediaController mediaController= new MediaController(getContext());

            video.setMediaController(mediaController);
            mediaController.setAnchorView(video);

            video.start();

        }



        if(linearLayout!=null){
            TextView textView = linearLayout.findViewById(R.id.time_ago);

            Calendar calendar=Calendar.getInstance();

            calendar.setTime(media.getTimestamp().toDate());

            textView.setText(calendar.get(Calendar.YEAR)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH));

            MediaContainer root =linearLayout.findViewById(R.id.item_root);
            root.setScaleBoth(scale);
        }


        return linearLayout;
    }
}
