package it.uniba.dib.sms222334.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import it.uniba.dib.sms222334.R;

public class AnimalAppCheckButton extends androidx.appcompat.widget.AppCompatButton implements View.OnClickListener{
    private static final String TAG="AnimalAppCheckButton";

    private static final int[] STATE_IS_CHECKED= {R.attr.isChecked};

    private boolean isChecked=false;

    public AnimalAppCheckButton(Context context) {
        super(context);
    }



    public AnimalAppCheckButton(Context context, AttributeSet attrs) {
            super(context, attrs);

            this.setOnClickListener(this);

            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.AnimalAppCheckButton, 0, 0);

            try {
                 isChecked = a.getBoolean(R.styleable.AnimalAppCheckButton_isChecked, false);
            } finally {
                 a.recycle();
            }
    }

    public boolean getChecked() {
        return this.isChecked;
    }

    public void setIsChecked(boolean checked){
        this.isChecked=checked;
    }




    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace+1);

        if(this.isChecked){
            mergeDrawableStates(drawableState, STATE_IS_CHECKED);
        }

        return drawableState;
    }

    @Override
    public void onClick(View v) {
        setIsChecked(!getChecked());
    }
}