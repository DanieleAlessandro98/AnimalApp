package it.uniba.dib.sms222334.Activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;

import it.uniba.dib.sms222334.Fragmets.AnimalFragment;
import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Fragmets.SearchFragment;
import it.uniba.dib.sms222334.Fragmets.VisitFragment;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.R;

public class MainActivity extends AppCompatActivity {

    final static String TAG="MainActivity";

    private ActivityResultLauncher<Intent> authResultLauncher;

    public enum TabPosition{HOME,SEARCH,PROFILE}

    private TabPosition previousTab;

    BottomNavigationView bottomNavigationView;

    //PrivateDao pdao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        changeTab(TabPosition.HOME);

        bottomNavigationView=findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home:
                        changeTab(TabPosition.HOME);
                    break;

                case R.id.search:
                        changeTab(TabPosition.SEARCH);
                    break;

                case R.id.profile:
                    changeTab(TabPosition.PROFILE);
                    break;
            }

            return true;
        });


        this.authResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            result.getData().getIntExtra("user-role",0); //TODO save this on sharedPreferences
                            changeTab(TabPosition.PROFILE);
                        } else {
                            bottomNavigationView.setSelectedItemId(R.id.home);
                        }
                    }
                });

        /*
        Calendar calendar=Calendar.getInstance();
        calendar.set(2000,12,2);

        Private privat= Private.Builder.create("giuseppe","ciao123")
                .setEmail("giuseppeapsakpsa")
                .setPhoneNumber(123443)
                .setPassword("ciao123")
                .setDate(calendar.getTime())
                .setTaxIdCode("iodhdfoisef").build();

        //da eliminare
        pdao=new PrivateDao(this);
        //pdao.getPrivateByEmail("test@gmail.com");

        pdao.createPrivate(privat);*/

    }

    private boolean isLogged() {
        return true; //TODO insert here SharedPreferences' control to check if it's logged or not
    }

    private void changeTab(MainActivity.TabPosition tabType){
        Fragment fragment=null;
        int enterAnimation=0,exitAnimation=0;

        switch (tabType){
            case HOME:
                if(previousTab!=TabPosition.HOME) {
                    fragment=new HomeFragment(getLoggedTypeUser());
                    previousTab=TabPosition.HOME;
                    enterAnimation=R.anim.slide_right_in;
                    exitAnimation=R.anim.slide_right_out;
                }
                else{
                    return;
                }
                break;
            case SEARCH:
                if(previousTab!=TabPosition.SEARCH) {
                    if (previousTab == TabPosition.HOME) {
                        enterAnimation=R.anim.slide_left_in;
                        exitAnimation=R.anim.slide_left_out;
                    }
                    else {
                        enterAnimation=R.anim.slide_right_in;
                        exitAnimation=R.anim.slide_right_out;
                    }

                    fragment=new SearchFragment();
                    previousTab=TabPosition.SEARCH;
                }
                else{
                    return;
                }
                break;
            case PROFILE:
                if(previousTab!=TabPosition.PROFILE){
                    if(isLogged()){
                        fragment=new ProfileFragment(getLoggedUser());
                        //fragment=new AnimalFragment();
                        //Visit visit=Visit.Builder.create("Visita di controllo totò", Visit.visitType.CONTROL,new Date(10,3,2024)).build();
                        //fragment=new VisitFragment(visit, ProfileFragment.Type.VETERINARIAN);
                        previousTab=TabPosition.PROFILE;
                        enterAnimation=R.anim.slide_left_in;
                        exitAnimation=R.anim.slide_left_out;
                    }else{
                        forceLogin();
                        return;
                    }
                }
                else{
                    return;
                }
                break;
            default:
                changeTab(TabPosition.HOME);
                return;
        }

        FragmentManager fragmentManager=getSupportFragmentManager();

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        transaction.replace(R.id.frame_for_fragment,fragment).commit();
    }

    private Document getLoggedUser() {
        return Private.Builder.create("giuseppe","liace")
                .setEmail("g.liace1@studenti.uniba.it").build(); //TODO grab the user logged
    }

    private ProfileFragment.Type getLoggedTypeUser(){
        Document profile=getLoggedUser();

        if(profile instanceof Private){
            return ProfileFragment.Type.PRIVATE;

        } else if (profile instanceof Veterinarian) {
            return ProfileFragment.Type.VETERINARIAN;

        } else if (profile instanceof PublicAuthority) {
            return ProfileFragment.Type.PUBLIC_AUTHORITY;
        }
        else{
            throw new IllegalArgumentException("This type of User is not accepted here!");
        }
    }

    private void forceLogin(){
        Intent loginIntent= new Intent(this,LoginActivity.class);
        this.authResultLauncher.launch(loginIntent);
        overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
    }
}