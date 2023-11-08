package it.uniba.dib.sms222334.Activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Fragmets.SearchFragment;
import it.uniba.dib.sms222334.Fragmets.VisitFragment;
import it.uniba.dib.sms222334.Models.Animal;
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

    final private String FRAGMENT_TAG="tab_fragment";
    private ActivityResultLauncher<Intent> authResultLauncher;
    public enum TabPosition{HOME,SEARCH,PROFILE}
    private TabPosition previousTab;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        Intent intent = getIntent();
        Uri data = intent.getData();

        if (data != null) {
            String animalId = data.getQueryParameter("id");

            new AnimalDao().getAnimalByReference(AnimalDao.collectionAnimal.document(animalId), new DatabaseCallbackResult<Animal>() {
                @Override
                public void onDataRetrieved(Animal result) {
                    //TODO implementare salvataggio login

                    if(isLogged()){
                        openAnimalPage(result);
                    }
                    else{
                        Bundle bundle= new Bundle();

                        bundle.putParcelable("animal",result);

                        forceLogin(bundle);
                    }

                }

                @Override
                public void onDataRetrieved(ArrayList<Animal> results) {

                }

                @Override
                public void onDataNotFound() {

                }

                @Override
                public void onDataQueryError(Exception e) {

                }
            });
        }

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
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Bundle bundle=result.getData().getExtras();

                        if(bundle!=null){
                            if(bundle.get("animal")!=null){
                                openAnimalPage((Animal)bundle.get("animal"));
                            }

                            if (bundle.get("tab")!=null) {
                                changeTab(TabPosition.values()[(int)bundle.get("tab")]);
                            }

                            /*if (bundle.get("profile")!=null) {
                                openProfile((User)bundle.get("profile"));
                            }*/

                        }
                        //TODO save user on sharedPreferences
                    } else {
                        changeTab(TabPosition.HOME);
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
                        fragment=ProfileFragment.newInstance(SessionManager.getInstance().getCurrentUser());
                        previousTab=TabPosition.PROFILE;
                        enterAnimation=R.anim.slide_left_in;
                        exitAnimation=R.anim.slide_left_out;
                    }else{
                        Bundle bundle=new Bundle();

                        bundle.putInt("tab",TabPosition.PROFILE.ordinal());

                        forceLogin(bundle);
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

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        transaction.replace(R.id.frame_for_fragment,fragment,FRAGMENT_TAG).commit();
    }

    public void forceLogin(@Nullable Bundle extras){
        Intent loginIntent= new Intent(this,LoginActivity.class);

        if(extras!=null){
            loginIntent.putExtras(extras);
        }

        this.authResultLauncher.launch(loginIntent);
        overridePendingTransition(R.anim.slide_up_in,R.anim.slide_up_out);
    }

    public void openAnimalPage(Animal animal){
        FragmentManager fragmentManager=getSupportFragmentManager();

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.frame_for_fragment, AnimalFragment.newInstance(animal),"animalPage").commit();
    }

    private void openProfile(User profile){
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack("itemPage");
        transaction.replace(R.id.frame_for_fragment, ProfileFragment.newInstance(profile)).commit();
    }
}