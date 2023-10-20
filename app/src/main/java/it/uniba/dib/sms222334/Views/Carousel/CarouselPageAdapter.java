package it.uniba.dib.sms222334.Views.Carousel;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.uniba.dib.sms222334.Models.*;
import it.uniba.dib.sms222334.Presenters.AnimalPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.SortingAlgo;

public class CarouselPageAdapter extends FragmentStateAdapter implements ViewPager2.PageTransformer {

    private final static String TAG="CarouselPageAdapter";
    public final static float BIG_SCALE = 1.0f;
    public final static float SMALL_SCALE = 0.7f;
    public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;

    public final static int FIRST_PAGE=0;
    private float mScale;

    private final boolean IS_MY_ANIMAL;
    private final int pages;

    LinkedList<Media> mediaList;

    Context context;

    public CarouselPageAdapter(Fragment fragment, Animal animal) {
        super(fragment);


        context=fragment.getContext();

        IS_MY_ANIMAL =AnimalPresenter.checkAnimalProperty(animal);

        this.pages=animal.getPhotos().size()+animal.getVideos().size()+
                ((IS_MY_ANIMAL)?1:0);

        mediaList=new LinkedList<>();

        mediaList.addAll(animal.getVideos());
        mediaList.addAll(animal.getPhotos());

        orderMediaByTimeStamp();
    }

    @Override
    public void transformPage(View page, float position) {
        MediaContainer myLinearLayout = page.findViewById(R.id.item_root);
        float scale = BIG_SCALE;
        if (position > 0) {
            scale = scale - position * DIFF_SCALE;
        } else {
            scale = scale + position * DIFF_SCALE;
        }
        if (scale < 0) scale = 0;

        if(myLinearLayout!=null)
            myLinearLayout.setScaleBoth(scale);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position){

        if(IS_MY_ANIMAL && (position == pages-1)) {
            try {
                return AddMediaPageFragment.newInstance();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            }
        }

        // make the first mViewPager bigger than others
        if (position == FIRST_PAGE)
            mScale = BIG_SCALE;
        else
            mScale = SMALL_SCALE;

        try {
            return MediaPageFragment.newInstance(context,position,mediaList.get(position), mScale);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return pages;
    }

    private void orderMediaByTimeStamp(){
        SortingAlgo.quickSort(mediaList,0,mediaList.size()-1);
    }
}