package it.uniba.dib.sms222334.Fragmets;

import android.app.DatePickerDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms222334.Activity.RegisterActivity;
import it.uniba.dib.sms222334.Database.Dao.Authentication.AuthenticationDao;
import it.uniba.dib.sms222334.Presenters.RegisterPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;

public class RegisterFragment extends Fragment{
    private final String TAG="RegisterFragment";

    public enum Type{PRIVATE,PUBLIC_AUTHORITY,VETERINARIAN}

    private int inflatedLayout;
    private EditText nameEditText;
    private EditText surnameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText phoneNumberEditText;
    private TextView dateTextView;
    private EditText taxIDCodeEditText;
    private EditText companyNameEditText;

    private AutoCompleteTextView locationEditText;
    private ImageButton searchLocationButton;

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

        locationEditText = layout.findViewById(R.id.location_edit_text);
        searchLocationButton = layout.findViewById(R.id.search_location_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line);
        locationEditText.setAdapter(adapter);

        searchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String locationName = locationEditText.getText().toString();

                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    List<Address> addresses = geocoder.getFromLocationName(locationName, 5);

                    adapter.clear();
                    for (Address address : addresses) {
                        String addressText = address.getAddressLine(0);
                        adapter.add(addressText);
                    }

                    adapter.notifyDataSetChanged();

                    locationEditText.setText(locationName);
                    locationEditText.setSelection(locationName.length());
                    locationEditText.showDropDown();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return layout;
    }

    private void registerUser() {
        View layout = getView();
        String email;
        String companyName;
        String location;
        String prefix;
        Long phone;

        // Recupera i dati inseriti dall'utente dal layout corrente (in base al valore di inflatedLayout)
        switch (inflatedLayout) {
            case R.layout.private_register:
                nameEditText = layout.findViewById(R.id.nameEditText);
                surnameEditText = layout.findViewById(R.id.surnameEditText);
                dateTextView = layout.findViewById(R.id.date_text_view);
                taxIDCodeEditText = layout.findViewById(R.id.tax_id_EditText);
                Spinner prefixSpinner = layout.findViewById(R.id.prefix_spinner);
                phoneNumberEditText = layout.findViewById(R.id.phoneNumberEditText);
                emailEditText = layout.findViewById(R.id.emailEditText);
                passwordEditText = layout.findViewById(R.id.passwordEditText);

                // Recupera i valori inseriti dall'utente
                String name = nameEditText.getText().toString();
                String surname = surnameEditText.getText().toString();
                String birthDateStr = dateTextView.getText().toString();//INIZIO Data di nascita Check
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                String taxIDCode = taxIDCodeEditText.getText().toString();
                prefix = prefixSpinner.getSelectedItem().toString();//Telefono
                phone = Long.valueOf(prefix + phoneNumberEditText.getText().toString());//Telefono
                location = locationEditText.getText().toString();
                email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Manda i dati al presenter
                role = UserRole.PRIVATE;
                registerPresenter.checkEmail(email, new AuthenticationDao.FindSameEmail() {
                    @Override
                    public void emailfind(boolean result) {
                        if (result==true){
                            showUsedEmail();
                        }else{
                            Date birthDate = null;
                            try {
                                birthDate = dateFormat.parse(birthDateStr);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }//FINE Data di nascita Check
                            registerPresenter.checkPrivateRegistration(name, surname, email, password, phone, birthDate, taxIDCode, location);
                        }
                    }
                });
                break;

            case R.layout.public_authority_register:
                companyNameEditText = layout.findViewById(R.id.nameEditText);
                prefixSpinner = layout.findViewById(R.id.prefix_spinner);
                phoneNumberEditText = layout.findViewById(R.id.phoneNumberEditText);
                emailEditText = layout.findViewById(R.id.emailEditText);
                passwordEditText = layout.findViewById(R.id.passwordEditText);

                // Recupera i dati dal layout public_authority_register
                companyName = companyNameEditText.getText().toString();
                location = locationEditText.getText().toString();
                prefix = prefixSpinner.getSelectedItem().toString();//Telefono
                phone = Long.valueOf(prefix + phoneNumberEditText.getText().toString());//Telefono
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                // Manda i dati al presenter
                role = UserRole.PUBLIC_AUTHORITY;
                registerPresenter.checkEmail(email, new AuthenticationDao.FindSameEmail() {
                    @Override
                    public void emailfind(boolean result) {
                        if (result==true){
                            showUsedEmail();
                        }else{
                            registerPresenter.checkAuthorityRegistration(companyName, email, password, phone, location);
                        }
                    }
                });
                break;

            case R.layout.veterinarian_register:
                companyNameEditText = layout.findViewById(R.id.nameEditText);
                prefixSpinner = layout.findViewById(R.id.prefix_spinner);
                phoneNumberEditText = layout.findViewById(R.id.phoneNumberEditText);
                emailEditText = layout.findViewById(R.id.emailEditText);
                passwordEditText = layout.findViewById(R.id.passwordEditText);

                // Recupera i dati dal layout veterinarian_register
                companyName = companyNameEditText.getText().toString();
                location = locationEditText.getText().toString();
                prefix = prefixSpinner.getSelectedItem().toString();//Telefono
                phone = Long.valueOf(prefix + phoneNumberEditText.getText().toString());//Telefono
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();

                // Esegui la registrazione dell'utente
                role = UserRole.VETERINARIAN;
                registerPresenter.checkEmail(email, new AuthenticationDao.FindSameEmail() {
                    @Override
                    public void emailfind(boolean result) {
                        if (result==true){
                            showUsedEmail();
                        }else{
                            registerPresenter.checkVeterinarianRegistration(companyName, email, password, phone, location);
                        }
                    }
                });
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

    public void showInvalidPassword() {
        passwordEditText.setError(this.getString(R.string.invalid_user_password));
    }
    public void showInvalidEmail() {
        emailEditText.setError(this.getString(R.string.invalid_user_email));
    }
    public void showUsedEmail() {
        emailEditText.setError(this.getString(R.string.already_used_email));
    }
    public void showInvalidSurname() {
        surnameEditText.setError(this.getString(R.string.invalid_user_surname));
    }
    public void showInvalidName() {
        nameEditText.setError(this.getString(R.string.invalid_user_name));
    }
    public void showInvalidDateBirth() {
        nameEditText.setError(this.getString(R.string.invalid_user_birthdate));
    }
    public void showInvalidPhone() {
        phoneNumberEditText.setError(this.getString(R.string.invalid_user_phonenumber));
    }
    public void showInvalidCompanyName() {
        companyNameEditText.setError(this.getString(R.string.invalid_user_companyname));
    }

    public void showInvalidLocation() {
        locationEditText.setError(this.getString(R.string.invalid_user_location));
    }
}