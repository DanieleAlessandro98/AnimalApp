package it.uniba.dib.sms222334.Views.Carousel;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class MediaContainer extends LinearLayout {
    private float mScale = 1.0f;

    public MediaContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaContainer(Context context) {
        super(context);
    }

    public void setScaleBoth(float scale) {
        this.mScale = scale;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // The main mechanism to display mScale animation, you can customize it
        // as your needs
        int w = this.getWidth();
        int h = this.getHeight();
        canvas.scale(mScale, mScale, w / 2, h / 2);

        super.onDraw(canvas);
    }
}
