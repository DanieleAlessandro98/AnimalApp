package it.uniba.dib.sms222334.Views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.R;

public class AnimalAppDialog extends Dialog implements AnimalCallbacks.inputValidate{

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
}
