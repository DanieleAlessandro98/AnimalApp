package it.uniba.dib.sms222334.Views;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import it.uniba.dib.sms222334.R;

public class AnimalAppEditText extends androidx.appcompat.widget.AppCompatEditText {
    public enum ValidateInput {
        NO_INPUT,
        VALID_INPUT,
        WARNING_INPUT,
        INVALID_INPUT
    }

    private static final int[] NO_INPUT={R.attr.no_input};
    private static final int[] INVALID_INPUT={R.attr.invalid_input};
    private static final int[] VALID_INPUT={R.attr.valid_input};
    private static final int[] WARNING_INPUT={R.attr.warning_input};

    private ValidateInput validateInput;

    private static final String TAG="AnimalAppEditText";

    public AnimalAppEditText(Context context) {
        super(context);
    }

    public AnimalAppEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        boolean noInput = attrs.getAttributeBooleanValue(null, "no_input", true);
        boolean validInput = attrs.getAttributeBooleanValue(null, "valid_input", false);
        boolean invalidInput = attrs.getAttributeBooleanValue(null, "invalid_input", false);
        boolean warningInput = attrs.getAttributeBooleanValue(null, "warning_input", false);

        if(noInput && !validInput && !invalidInput && !warningInput)
            validateInput=ValidateInput.NO_INPUT;
        else if(!noInput && validInput && !invalidInput && !warningInput)
            validateInput=ValidateInput.VALID_INPUT;
        else if(!noInput && !validInput && invalidInput && !warningInput)
            validateInput=ValidateInput.INVALID_INPUT;
        else if(!noInput && !validInput && !invalidInput && warningInput)
            validateInput=ValidateInput.WARNING_INPUT;
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        this.setInputValidate(ValidateInput.NO_INPUT);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        this.setInputValidate(ValidateInput.NO_INPUT);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 4);

        if(validateInput!=null){
            switch (validateInput){
                case NO_INPUT:
                    mergeDrawableStates(drawableState, NO_INPUT);
                    break;
                case VALID_INPUT:
                    mergeDrawableStates(drawableState, VALID_INPUT);
                    break;
                case INVALID_INPUT:
                    mergeDrawableStates(drawableState, INVALID_INPUT);
                    break;
                case WARNING_INPUT:
                    mergeDrawableStates(drawableState, WARNING_INPUT);
                    break;
            }
        }
        else {
            mergeDrawableStates(drawableState, NO_INPUT);
        }


        return drawableState;
    }

    public void setInputValidate(ValidateInput inputValidate) {
        if(this.validateInput==inputValidate)
            return;

        this.validateInput=inputValidate;
        refreshDrawableState();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(this.validateInput==ValidateInput.WARNING_INPUT){
            Drawable warningDrawable = getResources().getDrawable(R.drawable.baseline_warning_24,null);

            int yellowColor = ContextCompat.getColor(getContext(), R.color.yellow);

            // Imposta il colore giallo sul Drawable utilizzando BlendModeColorFilter
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                BlendMode blendMode = BlendMode.SRC_ATOP;
                BlendModeColorFilter colorFilter = new BlendModeColorFilter(yellowColor, blendMode);
                warningDrawable.setColorFilter(colorFilter);
            }
            else{
                warningDrawable.setColorFilter(yellowColor, PorterDuff.Mode.SRC_ATOP); //deprecato
            }


            int additionalPadding=30;

            //imposta la posizione dell'immagine
            int drawableWidth = warningDrawable.getIntrinsicWidth();
            int drawableHeight = warningDrawable.getIntrinsicHeight();
            int left = getWidth() - getPaddingRight() - drawableWidth-additionalPadding;
            int top = (getHeight() - drawableHeight) / 2;
            int right = left + drawableWidth;
            int bottom = top + drawableHeight;
            warningDrawable.setBounds(left, top, right, bottom);


            warningDrawable.draw(canvas);
        }

    }
}
