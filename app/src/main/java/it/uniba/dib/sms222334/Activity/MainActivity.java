package it.uniba.dib.sms222334.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Fragmets.SearchFragment;
import it.uniba.dib.sms222334.Database.Dao.PrivateDao;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.R;

public class MainActivity extends AppCompatActivity {

    final static String TAG="MainActivity";

    BottomNavigationView bottomNavigationView;

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

        /*
        Calendar calendar=Calendar.getInstance();
        calendar.set(2000,12,2);

        Private privat= Private.Builder.create("giuseppe","ciao123")
                .setEmail("giuseppeapsakpsa")
                .setPhoneNumber(123443)
                .setPassword("ciao123")
                .setDate(calendar.getTime())
                .setTaxIdCode("iodhdfoisef").build();

        //pdao.createPrivate(privat);
*/


        PrivateDao pdao = new PrivateDao();
        GetPrivateByEmailResult listener = new GetPrivateByEmailResult() {
            @Override
            public void onPrivateRetrieved(Private resultPrivate) {
                String log = "";
                log += resultPrivate.getName() + " ";
                log += resultPrivate.getSurname() + " ";
                log += resultPrivate.getEmail() + " ";
                log += resultPrivate.getPassword() + " ";
                log += resultPrivate.getPhoneNumber() + " ";
                log += resultPrivate.getDate() + " ";
                log += resultPrivate.getRole() + " ";
                log += resultPrivate.getTax_id_code() + " ";
                log += resultPrivate.getPhoto() + " ";

                Log.d("resultPrivateTest", log);
            }

            @Override
            public void onPrivateNotFound() {
                Log.d(TAG, "non esiste");
            }

            @Override
            public void onPrivateQueryError(Exception e) {
                Log.w(TAG, "errore query.");
            }
        };

        pdao.getPrivateByEmail("test_owner@gmail.com", listener);
    }

    // TODO:
        // Questa interfaccia la spostiamo da qua. dove la mettiamo però? Nel dao? classe a parte? nel presenter? ecc
    public interface GetPrivateByEmailResult {
        void onPrivateRetrieved(Private resultPrivate);
        void onPrivateNotFound();
        void onPrivateQueryError(Exception e);
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_for_fragment,fragment).commit();
    }
}