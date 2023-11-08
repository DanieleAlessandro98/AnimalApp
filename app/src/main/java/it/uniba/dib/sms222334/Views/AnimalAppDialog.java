package it.uniba.dib.sms222334.Views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.R;

public class AnimalAppDialog extends Dialog implements AnimalCallbacks.inputValidate{

    public void setBannerText(String message) {
        ((TextView) findViewById(R.id.dialog_banner_text)).setText(message);
    }

    public enum DialogType{WARNING,CRITICAL,INFO};

    private AnimalCallbacks.inputValidate inputCallback;

    public void setInputCallback(AnimalCallbacks.inputValidate inputCallback) {
        this.inputCallback = inputCallback;
    }


    public void setConfirmAction(View.OnClickListener action){
        findViewById(R.id.delete_button).setOnClickListener(action);
    }

    public void setUndoAction(View.OnClickListener action){
        findViewById(R.id.undo_button).setOnClickListener(action);
    }

    public void setContentView(String message,DialogType dialogType){
        super.setContentView(R.layout.dialog);

        ((TextView) findViewById(R.id.dialog_text)).setText(message);


        RelativeLayout topBanner= findViewById(R.id.top_banner);
        Button deleteButton = findViewById(R.id.delete_button);
        TextView bannerText= findViewById(R.id.dialog_banner_text);

        switch (dialogType){
            case INFO:
                topBanner.getBackground().setColorFilter(getContext().getColor(R.color.main_green), PorterDuff.Mode.SRC_ATOP);
                deleteButton.getBackground().setColorFilter(getContext().getColor(R.color.main_green), PorterDuff.Mode.SRC_ATOP);
                bannerText.setText(getContext().getText(R.string.info));
                break;
            case WARNING:
                topBanner.getBackground().setColorFilter(getContext().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                deleteButton.getBackground().setColorFilter(getContext().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                bannerText.setText(getContext().getText(R.string.warning));
                break;
            case CRITICAL:
                topBanner.getBackground().setColorFilter(getContext().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                deleteButton.getBackground().setColorFilter(getContext().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                bannerText.setText(getContext().getText(R.string.confirm_delete));
                break;
        }

    }

    public AnimalAppDialog(@NonNull Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void hideButtons() {
        findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
        findViewById(R.id.undo_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void InvalidName() {
        if(this.inputCallback!=null){
            inputCallback.InvalidName();
        }
    }

    @Override
    public void InvalidBirthDate() {
        if(this.inputCallback!=null) {
            inputCallback.InvalidBirthDate();
        }
    }

    @Override
    public void InvalidMicrochip() {
        if(this.inputCallback!=null) {
            inputCallback.InvalidMicrochip();
        }
    }

    @Override
    public void MicrochipAlreadyUsed() {
        if(this.inputCallback!=null) {
            inputCallback.MicrochipAlreadyUsed();
        }
    }

    public static void launchConfirmDialog(Callable<Void> confirmAction,Context context){
        final AnimalAppDialog deleteDialog=new AnimalAppDialog(context);

        deleteDialog.setContentView(context.getString(R.string.this_element_will_be), AnimalAppDialog.DialogType.CRITICAL);

        deleteDialog.setConfirmAction(t -> {
            try {
                confirmAction.call();
                deleteDialog.cancel();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        deleteDialog.setUndoAction(t -> deleteDialog.cancel());

        deleteDialog.show();
    }

    public static void launchQRCodeDialog(Context context, Bitmap bitmap){
        final AnimalAppDialog codeDialog=new AnimalAppDialog(context);

        codeDialog.setContentView(R.layout.qr_code_dialog);

        RelativeLayout topBanner= codeDialog.findViewById(R.id.top_banner);
        Button closeButton=codeDialog.findViewById(R.id.close_button);
        ImageView qrCode=codeDialog.findViewById(R.id.qr_code);

        topBanner.getBackground().setColorFilter(context.getColor(R.color.main_green), PorterDuff.Mode.SRC_ATOP);
        closeButton.getBackground().setColorFilter(context.getColor(R.color.main_green), PorterDuff.Mode.SRC_ATOP);
        qrCode.setImageBitmap(bitmap);

        closeButton.setOnClickListener(v -> codeDialog.cancel());

        codeDialog.show();
    }
}
