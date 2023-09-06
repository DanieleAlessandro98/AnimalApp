package it.uniba.dib.sms222334.Fragmets;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;

public class AnimalFragment extends Fragment {

    final static String TAG="AnimalFragment";

    Button editButton,backButton;
    TabLayout tabLayout;

    ProfileFragment.Type profileType;

    public static Animal animal;    //modifica 13 giu

    private ProfileFragment.Tab previousTab;

    public AnimalFragment(){

    }

    public static AnimalFragment newInstance(Animal animal) {
        AnimalFragment myFragment = new AnimalFragment();

        Bundle args = new Bundle();

        args.putParcelable("animal", animal);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout= inflater.inflate(R.layout.animal_fragment,container,false);


        this.profileType= ProfileFragment.Type.ANIMAL;
        this.animal=getArguments().getParcelable("animal");
        this.previousTab=new ProfileFragment.Tab();


        editButton=layout.findViewById(R.id.edit_button);

        editButton.setOnClickListener(v -> launchEditDialog());

        backButton=layout.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());



        tabLayout=layout.findViewById(R.id.tab_layout);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        changeTab(ProfileFragment.TabPosition.RELATION,true);
                        break;

                    case 1:
                        changeTab(ProfileFragment.TabPosition.HEALTH,true);
                        break;

                    case 2:
                        changeTab(ProfileFragment.TabPosition.FOOD,true);
                        break;

                    case 3:
                        changeTab(ProfileFragment.TabPosition.VISIT,true);
                        break;

                    case 4:
                        changeTab(ProfileFragment.TabPosition.EXPENSE,true);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        changeTab(ProfileFragment.TabPosition.RELATION,false);

        return layout;
    }

    private void changeTab(ProfileFragment.TabPosition tabType,Boolean withAnimation){
        Fragment fragment=null;
        int enterAnimation=0,exitAnimation=0;

        switch (tabType){
            case RELATION:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.RELATION) {
                    previousTab.tabPosition= ProfileFragment.TabPosition.RELATION;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                    enterAnimation=withAnimation?R.anim.slide_right_in:0;
                    exitAnimation=withAnimation?R.anim.slide_right_out:0;
                }
                else{
                    return;
                }
                break;
            case HEALTH:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.HEALTH) {
                    if (previousTab.tabPosition == ProfileFragment.TabPosition.RELATION) {
                        enterAnimation=withAnimation?R.anim.slide_left_in:0;
                        exitAnimation=withAnimation?R.anim.slide_left_out:0;
                    }
                    else {
                        enterAnimation=withAnimation?R.anim.slide_right_in:0;
                        exitAnimation=withAnimation?R.anim.slide_right_out:0;
                    }

                    previousTab.tabPosition= ProfileFragment.TabPosition.HEALTH;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                }
                else{
                    return;
                }
                break;
            case FOOD:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.FOOD) {
                    if ((previousTab.tabPosition == ProfileFragment.TabPosition.RELATION) || (previousTab.tabPosition == ProfileFragment.TabPosition.HEALTH)) {
                        enterAnimation=withAnimation?R.anim.slide_left_in:0;
                        exitAnimation=withAnimation?R.anim.slide_left_out:0;
                    }
                    else {
                        enterAnimation=withAnimation?R.anim.slide_right_in:0;
                        exitAnimation=withAnimation?R.anim.slide_right_out:0;
                    }

                    previousTab.tabPosition= ProfileFragment.TabPosition.FOOD;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                }
                else{
                    return;
                }
                break;
            case VISIT:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.VISIT) {
                    if (previousTab.tabPosition != ProfileFragment.TabPosition.EXPENSE) {
                        enterAnimation=withAnimation?R.anim.slide_left_in:0;
                        exitAnimation=withAnimation?R.anim.slide_left_out:0;
                    }
                    else {
                        enterAnimation=withAnimation?R.anim.slide_right_in:0;
                        exitAnimation=withAnimation?R.anim.slide_right_out:0;
                    }


                    previousTab.tabPosition= ProfileFragment.TabPosition.VISIT;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                }
                else {
                    return;
                }
                break;
            case EXPENSE:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.EXPENSE) {
                    previousTab.tabPosition= ProfileFragment.TabPosition.EXPENSE;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                    enterAnimation=withAnimation?R.anim.slide_left_in:0;
                    exitAnimation=withAnimation?R.anim.slide_left_out:0;

                }
                else{
                    return;
                }
                break;
            default:
                changeTab(ProfileFragment.TabPosition.RELATION,true);
                return;
        }

        FragmentManager fragmentManager=getParentFragmentManager();

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        transaction.replace(R.id.recycle_container,fragment).commit();
    }

    private void launchEditDialog() {

        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        editDialog.setContentView(R.layout.edit_animal);



        Button backButton= editDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> editDialog.cancel());

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
