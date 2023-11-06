package it.uniba.dib.sms222334.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Fragmets.SearchFragment;
import it.uniba.dib.sms222334.Fragmets.VisitFragment;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Fragmets.AnimalFragment;
import it.uniba.dib.sms222334.Models.Authentication;

import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;

public class MainActivity extends AppCompatActivity {

    final static String TAG="MainActivity";

    private ActivityResultLauncher<Intent> authResultLauncher;
    public enum TabPosition{HOME,SEARCH,PROFILE}
    private TabPosition previousTab,attempingTab;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        if(savedInstanceState!=null)
            this.previousTab= TabPosition.values()[savedInstanceState.getInt("tab_position")];

        initView();
        initListeners();
        initRegisterActivity();

        if(getSupportFragmentManager().findFragmentByTag("tab_fragment")==null)
            changeTab(TabPosition.HOME);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("tab_position",this.previousTab.ordinal());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
    }

    private void initView() {
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
    }

    private void initListeners() {
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
    }

    private void initRegisterActivity() {
        this.authResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            changeTab(attempingTab);
                            //TODO save user on sharedPreferences
                        } else {
                            changeTab(TabPosition.HOME);
                        }
                    }
                });
    }

    private boolean isLogged() {
        return SessionManager.getInstance().isLogged(); //TODO insert here SharedPreferences' control to check if it's logged or not
    }

    public void changeTab(MainActivity.TabPosition tabType){
        Fragment fragment=null;
        int enterAnimation=0,exitAnimation=0;

        switch (tabType){
            case HOME:
                if(previousTab!=TabPosition.HOME) {
                    fragment= new HomeFragment();
                    attempingTab=TabPosition.HOME;
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
                    attempingTab=TabPosition.SEARCH;
                    previousTab=TabPosition.SEARCH;
                }
                else{
                    return;
                }
                break;
            case PROFILE:
                if(previousTab!=TabPosition.PROFILE){
                    if(isLogged()){
                        fragment=ProfileFragment.newInstance(SessionManager.getInstance().getCurrentUser(),this);
                        previousTab=TabPosition.PROFILE;
                        enterAnimation=R.anim.slide_left_in;
                        exitAnimation=R.anim.slide_left_out;
                    }else{
                        attempingTab=TabPosition.PROFILE;
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

        //TODO cause crash when pass from profile tab with nested fragment to another tab
        //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        transaction.replace(R.id.frame_for_fragment,fragment,"tab_fragment").commit();
    }

    public void forceLogin(){
        Intent loginIntent= new Intent(this,LoginActivity.class);
        this.authResultLauncher.launch(loginIntent);
        overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
    }
}