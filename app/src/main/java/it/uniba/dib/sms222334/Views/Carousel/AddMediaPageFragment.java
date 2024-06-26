package it.uniba.dib.sms222334.Views.Carousel;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.IOException;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Photo;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Video;
import it.uniba.dib.sms222334.R;

public class AddMediaPageFragment extends Fragment {

    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    private ActivityResultLauncher<Intent> videoPickerResultLauncher;

    CarouselPageAdapter adapter;

    public AddMediaPageFragment(){

    }

    public AddMediaPageFragment(CarouselPageAdapter adapter){
        this.adapter=adapter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RelativeLayout relativeLayout = (RelativeLayout)
                inflater.inflate(R.layout.carousel_add_media_item, container, false);

        ImageButton addPhotoButton= relativeLayout.findViewById(R.id.add_photo_button);

        addPhotoButton.setOnClickListener(v -> {
            Intent photoIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerResultLauncher.launch(photoIntent);
        });

        ImageButton addVideoButton= relativeLayout.findViewById(R.id.add_video_button);

        addVideoButton.setOnClickListener(v -> {
            Intent videoIntent = new Intent(Intent.ACTION_PICK);
            videoIntent.setType("video/*");
            videoPickerResultLauncher.launch(videoIntent);
        });

        ProgressBar progressBar=relativeLayout.findViewById(R.id.uploadProgressBar);



        this.photoPickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData()!=null) {
                        try {
                            Bitmap selectedImage=MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), result.getData().getData());

                            String newPhotoName=adapter.animal.getFirebaseID()+"_"+(adapter.mediaList.size()+Timestamp.now().hashCode()+".jpg");
                            new MediaDao().uploadPhoto(selectedImage, Animal.PHOTO_PATH, newPhotoName, new MediaDao.PhotoUploadListener() {
                                @Override
                                public void onPhotoUploaded() {
                                    Photo photo=new Photo(Animal.PHOTO_PATH+newPhotoName, Timestamp.now());

                                    adapter.addMedia(photo);

                                    new AnimalDao().editAnimal(adapter.animal
                                            , SessionManager.getInstance().getCurrentUser().getEmail()
                                            ,null,false);
                                }

                                @Override
                                public void onPhotoUploadProgress(UploadTask.TaskSnapshot snapshot) {
                                    long totalByteCount = snapshot.getTotalByteCount();
                                    long bytesTransferred = snapshot.getBytesTransferred();
                                    int progress = (int) ((bytesTransferred / (float) totalByteCount) * 100);
                                    progressBar.setProgress(progress);
                                }

                                @Override
                                public void onPhotoUploadFailed(Exception exception) {

                                }
                            });
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });



        this.videoPickerResultLauncher= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri videoUri = result.getData().getData();

                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(getContext(), videoUri);
                        String durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long duration = Long.parseLong(durationString) / 1000;

                        if(duration > 20 ){
                            Toast.makeText(getContext(), getContext().getString(R.string.invalid_video), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        progressBar.setVisibility(View.VISIBLE);

                        String newVideoName=adapter.animal.getFirebaseID()+"_"+(adapter.mediaList.size()+Timestamp.now().hashCode()+".mp4");

                        new MediaDao().uploadVideo(videoUri, newVideoName, new MediaDao.VideoUploadListener() {
                            @Override
                            public void onVideoUploaded() {
                                progressBar.setVisibility(View.INVISIBLE);
                                Video video= new Video(Animal.VIDEO_PATH +newVideoName,Timestamp.now());

                                adapter.addMedia(video);

                                new AnimalDao().editAnimal(adapter.animal
                                        , SessionManager.getInstance().getCurrentUser().getEmail()
                                        ,null,false);
                            }

                            @Override
                            public void onVideoUploadProgress(UploadTask.TaskSnapshot snapshot){
                                long totalByteCount = snapshot.getTotalByteCount();
                                long bytesTransferred = snapshot.getBytesTransferred();
                                int progress = (int) ((bytesTransferred / (float) totalByteCount) * 100);
                                progressBar.setProgress(progress);
                            }

                            @Override
                            public void onVideoUploadFailed(Exception exception) {

                            }
                        });
                    }
                });

        return relativeLayout;
    }
}
