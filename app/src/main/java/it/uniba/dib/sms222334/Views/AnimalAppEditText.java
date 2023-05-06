package it.uniba.dib.sms222334.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import it.uniba.dib.sms222334.R;

public class AnimalAppEditText extends androidx.appcompat.widget.AppCompatEditText {
    private static final String TAG="AnimalAppEditText";

    public AnimalAppEditText(Context context) {
        super(context);
    }

    private Integer validateInput;

    public AnimalAppEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AnimalAppEditText,
                0, 0);

        try {
            validateInput = a.getInteger(R.styleable.AnimalAppEditText_validate_input, 0);
        } finally {
            a.recycle();
        }
    }

    public void setInputValidate(Integer inputValidate) {
        
        invalidate();
        requestLayout();
    }
}
