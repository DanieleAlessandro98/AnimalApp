package it.uniba.dib.sms222334.Fragmets;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms222334.Activity.RegisterActivity;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Presenters.RegisterPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.Adapter.AnimalAppPageAdapter;

public class RegisterFragment extends Fragment{
    private final String TAG="RegisterFragment";
    public enum Type{PRIVATE,PUBLIC_AUTHORITY,VETERINARIAN}

    private int inflatedLayout;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private EditText dateEditText;
    private EditText taxIDCodeEditText;

    private EditText companyNameEditText;
    private EditText siteEditText;
    private UserRole role;
    private RegisterActivity registerActivity;
    private RegisterPresenter registerPresenter;
    public RegisterFragment() {

    }

    public static RegisterFragment newInstance(Type profileType) {
        RegisterFragment myFragment = new RegisterFragment();

        Bundle args = new Bundle();
        args.putInt("profile_type", profileType.ordinal());
        myFragment.setArguments(args);

        return myFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.registerActivity= (RegisterActivity) getActivity();
        registerPresenter=new RegisterPresenter(this);
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        switch (Type.values()[getArguments().getInt("profile_type")]){
            case PRIVATE:
                inflatedLayout=R.layout.private_register;
                break;
            case PUBLIC_AUTHORITY:
                inflatedLayout=R.layout.public_authority_register;
                break;
            case VETERINARIAN:
                inflatedLayout=R.layout.veterinarian_register;
                break;
            default:
                throw new IllegalArgumentException("Invalid registerType");
        }

        final View layout= inflater.inflate(inflatedLayout,container,false);


        //Check per vedere se si sta regisrtrando un privato in modo da inserire la data in fase di regsitrazione con il date picker
        if(inflatedLayout==R.layout.private_register) {
            ImageButton datePickerButton = layout.findViewById(R.id.date_picker_button);
            TextView dateTextView = layout.findViewById(R.id.date_text_view);
            datePickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();

                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    dateTextView.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                                }
                            }, year, month, day);

                    datePickerDialog.show();
                }
            });
        }
            //On click chiamo Register USer che prenderà i dati in input e li manda al presenter
            Button registerButton = layout.findViewById(R.id.loginButton);
            registerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerUser();
                }
            });


        Spinner prefixSpinner= layout.findViewById(R.id.prefix_spinner);
        ArrayAdapter<CharSequence> prefixAdapter= ArrayAdapter.createFromResource(getContext(),R.array.phone_prefixes,
                android.R.layout.simple_list_item_1);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixSpinner.setAdapter(prefixAdapter);

        return layout;
    }

    private void registerUser() {
        View layout = getView();
        // Recupera i dati inseriti dall'utente dal layout corrente (in base al valore di inflatedLayout)
        switch (inflatedLayout) {
            case R.layout.private_register:
                EditText nameEditText = layout.findViewById(R.id.nameEditText);
                EditText surnameEditText = layout.findViewById(R.id.surnameEditText);
                TextView dateTextView = layout.findViewById(R.id.date_text_view);
                EditText taxIdEditText = layout.findViewById(R.id.tax_id_EditText);
                Spinner prefixSpinner = layout.findViewById(R.id.prefix_spinner);
                EditText phoneNumberEditText = layout.findViewById(R.id.phoneNumberEditText);
                EditText emailEditText = layout.findViewById(R.id.emailEditText);
                EditText passwordEditText = layout.findViewById(R.id.passwordEditText);

                // Recupera i valori inseriti dall'utente
                String name = nameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();
                String birthDateStr = dateTextView.getText().toString();//INIZIO Data di nascita Check
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date birthDate = null;
                try {
                    birthDate = dateFormat.parse(birthDateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }//FINE Data di nascita Check
                String taxIDCode = taxIdEditText.getText().toString();
                String prefix = prefixSpinner.getSelectedItem().toString();//Telefono
                Long phone = Long.valueOf(prefix + phoneNumberEditText.getText().toString());//Telefono
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Manda i dati al presenter
                role = UserRole.PRIVATE;
                registerPresenter.checkPrivateRegistration(name, surname, email, password, phone, birthDate, taxIDCode);
                break;
            case R.layout.public_authority_register:
                EditText companyNameEditText = layout.findViewById(R.id.nameEditText);
                EditText siteEditText = layout.findViewById(R.id.surnameEditText);
                prefixSpinner = layout.findViewById(R.id.prefix_spinner);
                phoneNumberEditText = layout.findViewById(R.id.phoneNumberEditText);
                emailEditText = layout.findViewById(R.id.emailEditText);
                passwordEditText = layout.findViewById(R.id.passwordEditText);


                // Recupera i dati dal layout public_authority_register
                String companyName = companyNameEditText.getText().toString();
                String site = siteEditText.getText().toString();
                String prefixA = prefixSpinner.getSelectedItem().toString();//Telefono
                Long phoneA = Long.valueOf(prefixA + phoneNumberEditText.getText().toString());//Telefono
                String emailA = emailEditText.getText().toString();
                String passwordA = passwordEditText.getText().toString();


                // Manda i dati al presenter
                role = UserRole.PUBLIC_AUTHORITY;
                registerPresenter.checkAuthorityRegistration(companyName, emailA, passwordA, phoneA, site);
                break;
            case R.layout.veterinarian_register:
                EditText companyNameEditTextB = layout.findViewById(R.id.nameEditText);
                EditText siteEditTextB = layout.findViewById(R.id.surnameEditText);
                prefixSpinner = layout.findViewById(R.id.prefix_spinner);
                phoneNumberEditText = layout.findViewById(R.id.phoneNumberEditText);
                emailEditText = layout.findViewById(R.id.emailEditText);
                passwordEditText = layout.findViewById(R.id.passwordEditText);


                // Recupera i dati dal layout veterinarian_register
                String companyNameB = companyNameEditTextB.getText().toString();
                String siteB = siteEditTextB.getText().toString();
                String prefixB = prefixSpinner.getSelectedItem().toString();//Telefono
                Long phoneB = Long.valueOf(prefixB + phoneNumberEditText.getText().toString());//Telefono
                String emailB = emailEditText.getText().toString();
                String passwordB = passwordEditText.getText().toString();

                // Esegui la registrazione dell'utente
                role = UserRole.VETERINARIAN;
                registerPresenter.checkVeterinarianRegistration(companyNameB, emailB, passwordB, phoneB, siteB);
                break;
        }
    }
    //Da Implementare
    public void onRegisterSuccess() {
        registerActivity.registerSuccesfull(role);
    }
    public void onRegisterFail() {
        registerActivity.onRegisterFail(role);
    }

}