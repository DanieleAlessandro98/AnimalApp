package it.uniba.dib.sms222334.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private TabPosition previousTab;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        initView();
        initListeners();
        initRegisterActivity();

        changeTab(TabPosition.HOME);
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
                            result.getData().getSerializableExtra("user-role"); //TODO save this on sharedPreferences
                            changeTab(TabPosition.PROFILE);
                        } else {
                            bottomNavigationView.setSelectedItemId(R.id.home);
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
                    fragment= new HomeFragment(this);
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
                        fragment=new ProfileFragment();
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

    public void forceLogin(){
        Intent loginIntent= new Intent(this,LoginActivity.class);
        this.authResultLauncher.launch(loginIntent);
        overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
    }
}