package it.uniba.dib.sms222334.Views.Carousel;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import java.util.LinkedList;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.MediaDao;
import it.uniba.dib.sms222334.Fragmets.AnimalFragment;
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
    private int pages;

    Animal animal;

    LinkedList<Media> mediaList;

    AnimalFragment fragment;

    Context context;

    public CarouselPageAdapter(AnimalFragment fragment, Animal animal) {
        super(fragment);


        context=fragment.getContext();

        this.fragment=fragment;

        this.animal=animal;

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
            return new AddMediaPageFragment(this);
        }

        // make the first mViewPager bigger than others
        if (position == FIRST_PAGE)
            mScale = BIG_SCALE;
        else
            mScale = SMALL_SCALE;

        try {
           MediaPageFragment fragment=MediaPageFragment.newInstance(context,IS_MY_ANIMAL,this.mediaList.get(position), mScale);

           fragment.setMediaDeleteListener(new MediaDao.MediaDeleteListener() {
               @Override
               public void mediaDeletedSuccessfully() {
                   removeMedia(mediaList.get(position));

                   new AnimalDao().editAnimal(animal
                           , SessionManager.getInstance().getCurrentUser().getEmail()
                           ,null,false);
               }

               @Override
               public void mediaDeletedFailed(Exception exception) {

               }
           });

           return fragment;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    private void notifyMediaEdits(){
        fragment.refresh(this.animal);
    }

    public void addMedia(Media media){
        if(media instanceof Photo)
            this.animal.addImage((Photo) media);
        else if(media instanceof Video)
            this.animal.addVideo((Video) media);

        notifyMediaEdits();
    }

    public void removeMedia(Media media){
        if(media instanceof Photo)
            this.animal.getPhotos().remove(media);
        else if(media instanceof Video)
            this.animal.getVideos().remove(media);

        notifyMediaEdits();
    }

    @Override
    public int getItemCount() {
        return pages;
    }

    private void orderMediaByTimeStamp(){
        SortingAlgo.quickSort(mediaList,0,mediaList.size()-1);
    }
}