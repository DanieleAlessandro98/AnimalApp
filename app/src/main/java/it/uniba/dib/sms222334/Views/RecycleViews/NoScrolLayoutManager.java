package it.uniba.dib.sms222334.Views.RecycleViews;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoScrolLayoutManager extends RecyclerView.LayoutManager {
    private String TAG="NoScrollLayoutManager";
    private Rect layoutInfo[];

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT
                ,RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    /*it's call to print childitem of recyclerView
     * Initialize Rect Array with the bounds of the views updating bottomLimit everytime
     * a view is added to layout
     * */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, @NonNull RecyclerView.State state){
        final int itemCount=state.getItemCount();

        if(itemCount!=0){
            final View firstView=recycler.getViewForPosition(0);
            measureChildWithMargins(firstView,0,0);
            int height=getDecoratedMeasuredHeight(firstView);
            int width=getDecoratedMeasuredWidth(firstView);

            this.layoutInfo=new Rect[itemCount];

            for(int i=0;i<itemCount;i++){
                this.layoutInfo[i]=new Rect(0,height*i,width,height*(i+1));
            }
        }
        else{
            this.layoutInfo=new Rect[0];
        }

        fillChild(recycler);
    }

    /*Refresh the recycleView moving all the view to the scrapHeap and reattaching it
    with the updates
     * */
    private void fillChild(RecyclerView.Recycler recycler) {
        detachAndScrapAttachedViews(recycler);

        for(int i=0;i<this.layoutInfo.length;i++){
            View view=recycler.getViewForPosition(i);
            //reattached the view ONLY if it's totally visible or partially

            if(isViewPartiallyVisible(view,true,false)
                    || isViewPartiallyVisible(view,false,false)){

                addView(view);
                measureChildWithMargins(view,0,0);
                layoutDecorated(view,layoutInfo[i].left
                        ,layoutInfo[i].top
                        ,layoutInfo[i].right
                        ,layoutInfo[i].bottom);

            }
        }
    }

    @Override
    public void onItemsAdded(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsAdded(recyclerView, positionStart, itemCount);

    }

    @Override
    public void onItemsRemoved(@NonNull RecyclerView recyclerView, int positionStart, int itemCount) {
        super.onItemsRemoved(recyclerView, positionStart, itemCount);
    }

    //activated verticalScroll
    @Override
    public boolean canScrollVertically() {
        return false;
    }
}
