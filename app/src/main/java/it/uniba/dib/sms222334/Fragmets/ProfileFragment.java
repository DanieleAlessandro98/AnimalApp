package it.uniba.dib.sms222334.Fragmets;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import it.uniba.dib.sms222334.Activity.MainActivity;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Presenters.UserPresenter;
import it.uniba.dib.sms222334.Presenters.VisitPresenter;
import it.uniba.dib.sms222334.Models.Visit;

public class ProfileFragment extends Fragment {
    final static String TAG="ProfileFragment";

    public enum Type{PRIVATE,PUBLIC_AUTHORITY,VETERINARIAN,ANIMAL}
    public enum TabPosition{ANIMAL,VISIT,EXPENSE,RELATION,HEALTH,FOOD}

    public static class Tab{
        public TabPosition tabPosition;
    }

    private Tab previousTab;
    
    private ProfileFragment.Type profileType;

    Button editButton,addVisitButton;
    TabLayout tabLayout;

    private int inflatedLayout;

    private UserRole role;

    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    private EditText nameEditText;
    private EditText surnameEditText;
    private TextView dateTextView;
    private EditText taxIDEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText passwordEditText;


    private UserPresenter userPresenter;

    public SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    private User profile;

    private ImageView photoImageView;
    private Button saveButton;
    private Button deleteButton;
    private Button editPhotoButton;

    private Dialog editDialog;
    private TextView nameView;
    private TextView emailView;

    public ProfileFragment(){

    }

    public static ProfileFragment newInstance(User profile, Context context) {
        ProfileFragment myFragment = new ProfileFragment();

        preferences=PreferenceManager.getDefaultSharedPreferences(context);
        myFragment.editor= preferences.edit();
        myFragment.editor.putString("profileData", new Gson().toJson(profile));
        myFragment.editor.putInt("profileRole", profile.getRole().ordinal());
        myFragment.editor.commit();

        return myFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        changeTab(TabPosition.ANIMAL,false);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        this.role=UserRole.values()[preferences.getInt("profileRole", 0)];
        String user = preferences.getString("profileData", "");

        String username = SessionManager.getInstance().getCurrentUser().getName();
        String email = SessionManager.getInstance().getCurrentUser().getEmail();

        switch (this.role){
            case PRIVATE:
                this.profile = new Gson().fromJson(user, new TypeToken<Private>() {}.getType());
                profileType=Type.PRIVATE;
                break;
            case PUBLIC_AUTHORITY:
                this.profile = new Gson().fromJson(user, new TypeToken<PublicAuthority>() {}.getType());
                profileType=Type.PUBLIC_AUTHORITY;
                break;
            case VETERINARIAN:
                this.profile = new Gson().fromJson(user, new TypeToken<Veterinarian>() {}.getType());
                profileType=Type.VETERINARIAN;
                break;
        }

        if(this.role==UserRole.VETERINARIAN){
            inflatedLayout=R.layout.veterinarian_profile_fragment;
        }
        else{
            inflatedLayout=R.layout.owner_profile_fragment;
        }

        final View layout= inflater.inflate(inflatedLayout,container,false);

        nameView = layout.findViewById(R.id.name);
        emailView = layout.findViewById(R.id.email);


        nameView.setText(username);
        emailView.setText(email);

        if(this.role==UserRole.VETERINARIAN){
            this.addVisitButton=layout.findViewById(R.id.create_visit);

            this.addVisitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchAddVisit();
                }
            });
        }


        this.previousTab=new Tab();

        editButton=layout.findViewById(R.id.edit_button);

        if(profile.getFirebaseID().compareTo(SessionManager.getInstance().getCurrentUser().getFirebaseID())!=0)
            editButton.setVisibility(View.INVISIBLE);

        userPresenter = new UserPresenter(this);

        editButton.setOnClickListener(v -> launchEditDialog());



        tabLayout=layout.findViewById(R.id.tab_layout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        changeTab(TabPosition.ANIMAL,true);
                        break;

                    case 1:
                        changeTab(TabPosition.VISIT,true);
                        break;

                    case 2:
                        changeTab(TabPosition.EXPENSE,true);
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

        this.photoPickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        userPresenter.pickPhoto(selectedImage);
                    }
                });

        Log.d(TAG,"qui teoricamente");

        return layout;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public Visit.visitType visitType;

    private int posizione = 0;

    private void launchAddVisit(){
        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        editDialog.setContentView(R.layout.create_visit);

        this.dateTextView = editDialog.findViewById(R.id.date_text_view);

        Spinner visitTypeSpinner= editDialog.findViewById(R.id.visit_type);
        Button backButton= editDialog.findViewById(R.id.back_button);
        Button createButton = editDialog.findViewById(R.id.save_button);
        Spinner animalchooser = editDialog.findViewById(R.id.animal_chooser);
        EditText doctorName = editDialog.findViewById(R.id.doctor_name);
        ImageButton dateButton = editDialog.findViewById(R.id.date_picker_button);

        ArrayAdapter<CharSequence> visitTypeAdapter= ArrayAdapter.createFromResource(getContext(),R.array.exam_type,
                android.R.layout.simple_list_item_1);
        visitTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visitTypeSpinner.setAdapter(visitTypeAdapter);

        ArrayList<String> animalListName = new ArrayList<>();

        for (int i = 0; i < ListFragment.animalList.size(); i++) {
            animalListName.add(ListFragment.animalList.get(i).getName());
        }

        ArrayAdapter<String> animal_chooseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,animalListName);
        animal_chooseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalchooser.setAdapter(animal_chooseAdapter);

        dateButton.setOnClickListener(new View.OnClickListener() {
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
                                dateTextView.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });

        backButton.setOnClickListener(v -> editDialog.cancel());
        final String[] animalValue = new String[1];

        visitTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                visitType = Visit.visitType.valueOf(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        animalchooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                animalValue[0] = adapterView.getItemAtPosition(i).toString();
                for (int j = 0; j < ListFragment.animalList.size(); j++) {
                    if (Objects.equals(animalValue[0], ListFragment.animalList.get(j).getName())){
                        posizione = j;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisitPresenter visit = new VisitPresenter();
                String date = dateTextView.getText().toString();//INIZIO Data di nascita
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date dateConvert = null;
                try {
                    dateConvert = dateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String visitName = doctorName.getText().toString();

                Visit value = visit.createVisit(visitType,
                        ListFragment.animalList.get(posizione),
                        dateConvert,
                        visitName,
                        profile.getFirebaseID());
                if (value != null){
                    ListFragment.visitList.add(value);
                    editDialog.cancel();
                    ListFragment.recyclerView.setAdapter(ListFragment.visitAdapter);
                }
            }
        });

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    private void launchEditDialog() {

        editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        switch (this.role){
            case PRIVATE:
                editDialog.setContentView(R.layout.private_profile_edit);

                photoImageView = editDialog.findViewById(R.id.profile_picture);
                nameEditText = editDialog.findViewById(R.id.nameEditText);
                surnameEditText = editDialog.findViewById(R.id.surnameEditText);
                dateTextView = editDialog.findViewById(R.id.date_text_view);
                taxIDEditText = editDialog.findViewById(R.id.tax_id_EditText);
                phoneEditText = editDialog.findViewById(R.id.phoneNumberEditText);
                emailEditText = editDialog.findViewById(R.id.emailEditText);
                passwordEditText = editDialog.findViewById(R.id.passwordEditText);

                break;
            case VETERINARIAN:
                editDialog.setContentView(R.layout.veterinarian_profile_edit);
                break;
            case PUBLIC_AUTHORITY:
                editDialog.setContentView(R.layout.authority_profile_edit);
                break;
        }

        saveButton = editDialog.findViewById(R.id.save_button);
        deleteButton = editDialog.findViewById(R.id.delete_button);
        editPhotoButton = editDialog.findViewById(R.id.edit_button);

        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    String name = nameEditText.getText().toString();
                    String surname = surnameEditText.getText().toString();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date birthDate = dateFormat.parse(dateTextView.getText().toString());

                    String taxID = taxIDEditText.getText().toString();
                    long phone = Long.parseLong(phoneEditText.getText().toString());
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    userPresenter.updateProfile(name, surname, birthDate, taxID, phone, email, password);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirm();
            }
        });

        editPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerResultLauncher.launch(photoIntent);
            }
        });

        userPresenter.initUserData();

        Spinner prefixSpinner= editDialog.findViewById(R.id.prefix_spinner);
        ArrayAdapter<CharSequence> prefixAdapter= ArrayAdapter.createFromResource(getContext(),R.array.phone_prefixes,
                android.R.layout.simple_list_item_1);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixSpinner.setAdapter(prefixAdapter);


        Button backButton= editDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> editDialog.cancel());

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }



    private void changeTab(ProfileFragment.TabPosition tabType,Boolean withAnimation){
        Fragment fragment=null;
        int enterAnimation=0,exitAnimation=0;

        switch (tabType){
            case ANIMAL:
                if(previousTab.tabPosition!= TabPosition.ANIMAL) {
                    previousTab.tabPosition= TabPosition.ANIMAL;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                    enterAnimation=withAnimation?R.anim.slide_right_in:0;
                    exitAnimation=withAnimation?R.anim.slide_right_out:0;
                }
                else{
                    return;
                }
                break;
            case VISIT:
                if(previousTab.tabPosition!= TabPosition.VISIT) {
                    if (previousTab.tabPosition == TabPosition.ANIMAL) {
                        enterAnimation=withAnimation?R.anim.slide_left_in:0;
                        exitAnimation=withAnimation?R.anim.slide_left_out:0;
                    }
                    else {
                        enterAnimation=withAnimation?R.anim.slide_right_in:0;
                        exitAnimation=withAnimation?R.anim.slide_right_out:0;
                    }

                    previousTab.tabPosition= TabPosition.VISIT;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                }
                else{
                    return;
                }
                break;
            case EXPENSE:
                if(previousTab.tabPosition!= TabPosition.EXPENSE){
                    previousTab.tabPosition= TabPosition.EXPENSE;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                    enterAnimation=withAnimation?R.anim.slide_left_in:0;
                    exitAnimation=withAnimation?R.anim.slide_left_out:0;
                }
                else{
                    return;
                }
                break;
            default:
                changeTab(TabPosition.ANIMAL,true);
                return;
        }



        FragmentManager fragmentManager=getParentFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        transaction.replace(R.id.recycle_container,fragment).commit();
    }

    public void onInitPrivateData(Private userPrivate) {
        nameEditText.setText(userPrivate.getName());
        surnameEditText .setText(userPrivate.getSurname());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");   // TODO: Spostare in utils
        String dateString = dateFormat.format(userPrivate.getBirthDate());
        dateTextView.setText(dateString);

        taxIDEditText.setText(userPrivate.getTaxIDCode());
        phoneEditText.setText(String.valueOf(userPrivate.getPhone()));
        emailEditText.setText(userPrivate.getEmail());
        passwordEditText.setText(userPrivate.getPassword());
        photoImageView.setImageBitmap(userPrivate.getPhoto());
    }

    public void showInvalidInput(int inputType) {
        switch (inputType) {
            case 1:
                nameEditText.setError(this.getString(R.string.invalid_user_name));
                break;

            case 2:
                surnameEditText.setError(this.getString(R.string.invalid_user_surname));
                break;

            case 3:
                dateTextView.setError(this.getString(R.string.invalid_user_birthdate));
                break;

            case 4:
                emailEditText.setError(this.getString(R.string.invalid_user_email));
                break;

            case 5:
                passwordEditText.setError(this.getString(R.string.invalid_user_password));
                break;
        }
    }

    public void showUpdateSuccessful() {
        Toast.makeText(requireContext(), this.getString(R.string.profile_update_successful), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(this.getString(R.string.profile_delete_alert_title));
        builder.setMessage(this.getString(R.string.profile_delete_alert_mex1));
        builder.setPositiveButton(this.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userPresenter.deleteProfile();
            }
        });
        builder.setNegativeButton(this.getString(R.string.profile_delete_alert_cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showLogoutSuccessful() {
        Toast.makeText(requireContext(), this.getString(R.string.profile_delete_successful), Toast.LENGTH_SHORT).show();

        ((MainActivity)getActivity()).changeTab(MainActivity.TabPosition.HOME);
        editDialog.cancel();
    }

    public void showLogoutError() {
        Toast.makeText(requireContext(), this.getString(R.string.profile_delete_failed), Toast.LENGTH_SHORT).show();
    }

    public void setPhotoPicked(Bitmap bitmap) {
        photoImageView.setImageBitmap(bitmap);
    }

    public Bitmap getPhotoPicked() {
        return ((BitmapDrawable)photoImageView.getDrawable()).getBitmap();
    }

    public void showPhotoUpdateError() {
        Toast.makeText(requireContext(), this.getString(R.string.photo_update_failed), Toast.LENGTH_SHORT).show();
    }

}
