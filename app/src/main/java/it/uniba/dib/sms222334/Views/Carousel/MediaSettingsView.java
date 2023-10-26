package it.uniba.dib.sms222334.Views.Carousel;

import android.animation.ObjectAnimator;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import it.uniba.dib.sms222334.Models.Media;
import it.uniba.dib.sms222334.Models.Photo;
import it.uniba.dib.sms222334.Models.Video;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;

public class MediaSettingsView extends RelativeLayout {
    ImageButton settings,download,delete;

    private boolean CAN_DELETE;

    private boolean isOpen;

    Callable<Void> deleteAction;

    Media media;

    public MediaSettingsView(Context context) {
        super(context);

        init();
    }

    public MediaSettingsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setDeleteAction(Callable<Void> callable){
        this.deleteAction=callable;
    }

    public void setMedia(Media media){
        this.media=media;
    }

    private void init(){
        RelativeLayout layout=(RelativeLayout) LayoutInflater.from(getContext())
                .inflate(R.layout.media_setting_view,null);

        settings=layout.findViewById(R.id.settings);
        download=layout.findViewById(R.id.download);
        delete=layout.findViewById(R.id.delete);

        this.CAN_DELETE=true;

        this.isOpen=false;

        settings.setOnClickListener(v -> {
            if(!this.isOpen)
                openSettings();
            else
                closeSettings();
        });

        delete.setOnClickListener(v -> {
            if(this.deleteAction!=null)
                AnimalAppDialog.launchConfirmDialog(this.deleteAction,getContext());
        });

        download.setOnClickListener(v -> {
            if(this.media!=null){
                if(this.media instanceof Video){
                    downloadVideo((Video) this.media);
                } else if (this.media instanceof Photo) {
                    try {
                        downloadPhoto((Photo) this.media);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        addView(layout);


    }

    public void disableDelete(){
        delete.setVisibility(GONE);
        this.CAN_DELETE=false;
    }

    public void enableDelete(){
        delete.setVisibility(VISIBLE);
        this.CAN_DELETE=true;
    }
    private void openSettings(){

        float translation = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 40,
                getContext().getResources().getDisplayMetrics() );

        float translationXDownload = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 15,
                getContext().getResources().getDisplayMetrics() );

        ObjectAnimator settingsAnimator = ObjectAnimator.ofFloat(settings, "rotation", 0.0f,180.0f);
        settingsAnimator.setDuration(400);
        settingsAnimator.start();

        ObjectAnimator downloadYAnimator = ObjectAnimator.ofFloat(download, "translationY", -translation);
        downloadYAnimator.setDuration(300);
        ObjectAnimator downloadXAnimator = ObjectAnimator.ofFloat(download, "translationX", translationXDownload);
        downloadXAnimator.setDuration(400);
        downloadXAnimator.start();
        downloadYAnimator.start();

        if(this.CAN_DELETE){
            float translationYDelete = TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 15,
                    getContext().getResources().getDisplayMetrics() );

            ObjectAnimator deleteYAnimator = ObjectAnimator.ofFloat(delete, "translationY", -translationYDelete);
            deleteYAnimator.setDuration(400);
            ObjectAnimator deleteXAnimator = ObjectAnimator.ofFloat(delete, "translationX", translation);
            deleteXAnimator.setDuration(300);

            deleteXAnimator.setStartDelay(100);
            deleteYAnimator.setStartDelay(100);
            deleteXAnimator.start();
            deleteYAnimator.start();
        }


        this.isOpen=true;
    }

    private void closeSettings(){

        ObjectAnimator settingsAnimator = ObjectAnimator.ofFloat(settings, "rotation", 180.0f,0.0f);
        settingsAnimator.setDuration(400);
        settingsAnimator.start();

        ObjectAnimator downloadYAnimator = ObjectAnimator.ofFloat(download, "translationY", 5);
        downloadYAnimator.setDuration(400);
        ObjectAnimator downloadXAnimator = ObjectAnimator.ofFloat(download, "translationX", 5);
        downloadXAnimator.setDuration(300);

        downloadXAnimator.setStartDelay(100);
        downloadYAnimator.setStartDelay(100);
        downloadXAnimator.start();
        downloadYAnimator.start();

        if(this.CAN_DELETE){
            ObjectAnimator deleteYAnimator = ObjectAnimator.ofFloat(delete, "translationY", 5);
            deleteYAnimator.setDuration(300);
            ObjectAnimator deleteXAnimator = ObjectAnimator.ofFloat(delete, "translationX", 5);
            deleteXAnimator.setDuration(400);
            deleteXAnimator.start();
            deleteYAnimator.start();
        }

        this.isOpen=false;
    }

    private void downloadPhoto(Photo photo) throws IOException{
        File sourceFile = new File(getContext().getCacheDir(), "1698258476.jpg");
        File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), Timestamp.now().getSeconds()+".jpg");

        FileOutputStream fos = new FileOutputStream(sourceFile);

        photo.getPhoto().compress(Bitmap.CompressFormat.JPEG, 100, fos);


        InputStream in = Files.newInputStream(sourceFile.toPath());
        OutputStream out = Files.newOutputStream(destinationFile.toPath());

        byte[] buf = new byte[1024];
        int len;

        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();

        Toast.makeText(getContext(), "Download photo...", Toast.LENGTH_LONG).show();
    }

    private void downloadVideo(Video video){
        DownloadManager.Request request = new DownloadManager.Request(video.getVideo());

        String destination = Environment.DIRECTORY_DCIM + "/"+ Timestamp.now().getSeconds() +".mp4";
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, destination);

        DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        downloadManager.enqueue(request);

        Toast.makeText(getContext(), "Download video...", Toast.LENGTH_LONG).show();
    }

}
