package it.uniba.dib.sms222334.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Fragmets.SearchFragment;
import it.uniba.dib.sms222334.Models.FireBaseCall.PrivateDao;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.R;

public class MainActivity extends AppCompatActivity {

    final static String TAG="MainActivity";

    BottomNavigationView bottomNavigationView;

    PrivateDao pdao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        replaceFragment(new HomeFragment());

        bottomNavigationView=findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.search:
                    replaceFragment(new SearchFragment());
                    break;

                case R.id.profile:
                    replaceFragment(new ProfileFragment());
                    break;
            }

            return true;
        });


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

        pdao.createPrivate(privat);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_for_fragment,fragment).commit();
    }
}