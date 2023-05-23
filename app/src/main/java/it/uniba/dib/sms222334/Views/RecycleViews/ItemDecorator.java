package it.uniba.dib.sms222334.Views.RecycleViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class ItemDecorator extends RecyclerView.ItemDecoration {
    public static final int DEFAULT_LINE_WIDTH=5;
    private int lineSize;

    public ItemDecorator(int lineSize) {
        this.lineSize = lineSize;
    }
    public ItemDecorator() {
        this.lineSize = DEFAULT_LINE_WIDTH;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, this.lineSize);
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(canvas, parent, state);


    }
}
