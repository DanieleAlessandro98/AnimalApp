package it.uniba.dib.sms222334.Views;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;

public class AnimalAppDialog extends Dialog implements AnimalCallbacks.inputValidate{

    private AnimalCallbacks.inputValidate inputCallback;

    public void setInputCallback(AnimalCallbacks.inputValidate inputCallback) {
        this.inputCallback = inputCallback;
    }

    public AnimalAppDialog(@NonNull Context context) {
        super(context);
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
