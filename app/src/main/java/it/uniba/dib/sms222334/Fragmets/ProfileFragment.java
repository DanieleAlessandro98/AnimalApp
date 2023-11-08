package it.uniba.dib.sms222334.Fragmets;

import android.app.DatePickerDialog;
import android.app.Dialog;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import it.uniba.dib.sms222334.Activity.MainActivity;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Owner;
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
import it.uniba.dib.sms222334.Views.AnimalAppDialog;

public class ProfileFragment extends Fragment {
    final static String TAG="ProfileFragment";

    final private String FRAGMENT_TAG="profile_tab_fragment";
    private EditText companyNameEditText;

    public enum Type{PRIVATE,PUBLIC_AUTHORITY,VETERINARIAN,ANIMAL}
    public enum TabPosition{ANIMAL,VISIT,EXPENSE,RELATION,HEALTH,FOOD}

    public static class Tab{
        public TabPosition tabPosition;
    }

    private Tab previousTab;
    private TabPosition clickedTab;
    
    private ProfileFragment.Type profileType;

    Button editButton,addVisitButton;

    boolean editOpen,visitOpen;
    TabLayout tabLayout;

    private UserRole role;

    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    private EditText nameEditText;
    private EditText surnameEditText;
    private TextView dateTextView;
    private EditText taxIDEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText siteEditText;


    private UserPresenter userPresenter;

    private User profile;

    private ImageView editPhotoImageView;

    private ImageView profilePhoto;

    public Dialog dialog;
    private TextView nameView;
    private TextView emailView;

    private ListFragment fragment;
    private VisitPresenter visitPresenter;

    public ProfileFragment(){}

    public static ProfileFragment newInstance(User profile) {
        ProfileFragment myFragment = new ProfileFragment();

        Bundle args = new Bundle();
        args.putParcelable("profileData", profile);
        myFragment.setArguments(args);

        return myFragment;
    }

    public void refresh(User profile){
        this.profile=profile;
        nameView.setText(profile.getName().toUpperCase());
        emailView.setText(profile.getEmail());
        profilePhoto.setImageBitmap(profile.getPhoto());

        if(getArguments()!=null){
            getArguments().remove("profileData");
            getArguments().putParcelable("profileData",this.profile);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach()");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView()");

        this.previousTab=new Tab();

        this.previousTab.tabPosition=TabPosition.RELATION; //i want the initial tab is different everytime


        this.profile= getArguments().getParcelable("profileData");
        this.role=profile.getRole();

        switch (this.role){
            case PRIVATE:
                profileType=Type.PRIVATE;
                break;
            case PUBLIC_AUTHORITY:
                profileType=Type.PUBLIC_AUTHORITY;
                break;
            case VETERINARIAN:
                profileType=Type.VETERINARIAN;
                break;
        }

        int inflatedLayout;

        if(this.role==UserRole.VETERINARIAN){
            inflatedLayout =R.layout.veterinarian_profile_fragment;
        }
        else{
            inflatedLayout =R.layout.owner_profile_fragment;
        }

        final View layout= inflater.inflate(inflatedLayout,container,false);

        nameView = layout.findViewById(R.id.name);
        emailView = layout.findViewById(R.id.email);
        profilePhoto = layout.findViewById(R.id.profile_picture);

        refresh(this.profile);

        if(this.role==UserRole.VETERINARIAN){
            this.addVisitButton=layout.findViewById(R.id.create_visit);

            if(SessionManager.getInstance().getCurrentUser().getRole()!=UserRole.VETERINARIAN){
                this.addVisitButton.setOnClickListener(v -> {
                    if(!visitOpen){
                        visitOpen=true;
                        launchAddVisit();
                    }
                });

                if(visitOpen)
                    launchAddVisit();
            }
            else{
                this.addVisitButton.setVisibility(View.INVISIBLE);
            }

        }


        userPresenter = new UserPresenter(this);


        editButton=layout.findViewById(R.id.edit_button);

        if(profile.getFirebaseID().compareTo(SessionManager.getInstance().getCurrentUser().getFirebaseID())!=0)
            editButton.setVisibility(View.INVISIBLE);

        tabLayout=layout.findViewById(R.id.tab_layout);

        this.photoPickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        userPresenter.pickPhoto(selectedImage);
                    }
                });

        return layout;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG,"onViewStateRestored()");

        Bundle args=getArguments();

        if(args!=null){
            this.clickedTab = ProfileFragment.TabPosition.values()[args.getInt("tab_position", 0)];
            this.editOpen = args.getBoolean("edit_open");
            this.visitOpen = args.getBoolean("visit_open");
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart()");

        editButton.setOnClickListener(v -> {
            if(!editOpen){
                editOpen=true;
                launchEditDialog();
            }
        });

        if(editOpen)
            launchEditDialog();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                clickedTab=TabPosition.values()[tab.getPosition()];

                changeTab(clickedTab,true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.selectTab(tabLayout.getTabAt(this.clickedTab.ordinal()));

        if(getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG)==null)
            changeTab(this.clickedTab,false);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop()");

        Bundle args=getArguments();

        if(args!=null) {
            args.putInt("tab_position", this.clickedTab.ordinal());
            args.putBoolean("edit_open",this.editOpen);
            args.putBoolean("visit_open",this.visitOpen);
        }

        if(dialog!=null)
            dialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG,"onsaveInstanceState()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG,"onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach()");
    }

    public Visit.visitType visitType;
    private int posizione = 0;

    private void launchAddVisit(){
        dialog =new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.create_visit);

        this.dateTextView = dialog.findViewById(R.id.date_text_view);
        visitPresenter = new VisitPresenter(fragment);

        Spinner visitTypeSpinner= dialog.findViewById(R.id.visit_type);
        Button backButton= dialog.findViewById(R.id.back_button);
        Button createButton = dialog.findViewById(R.id.save_button);
        Spinner animalchooser = dialog.findViewById(R.id.animal_chooser);
        EditText doctorName = dialog.findViewById(R.id.doctor_name);
        ImageButton dateButton = dialog.findViewById(R.id.date_picker_button);

        ArrayAdapter<CharSequence> visitTypeAdapter= ArrayAdapter.createFromResource(getContext(),R.array.exam_type,
                android.R.layout.simple_list_item_1);
        visitTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        visitTypeSpinner.setAdapter(visitTypeAdapter);
        ArrayList <Animal> animalList = ((Owner)SessionManager.getInstance().getCurrentUser()).getAnimalList();
        ArrayList<String> animalListName = new ArrayList<>();
        
        for (int i = 0; i < animalList.size(); i++) {
            animalListName.add(animalList.get(i).getName());
        }

        ArrayAdapter<String> animal_chooseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,animalListName);
        animal_chooseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        animalchooser.setAdapter(animal_chooseAdapter);

        final Calendar c = Calendar.getInstance();

        dateButton.setOnClickListener(v -> {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        dateTextView.setText(dayOfMonth + "/" + (month1 +1) + "/" + year1);
                        c.set(year1, month1,dayOfMonth);
                    }, year, month, day);

            datePickerDialog.show();
        });

        backButton.setOnClickListener(v -> {
            dialog.cancel();
            visitOpen=false;
        });
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
                for (int j = 0; j < animalList.size(); j++) {
                    if (Objects.equals(animalValue[0], animalList.get(j).getName())){
                        posizione = j;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createButton.setOnClickListener(view -> {
            Date date = c.getTime();

            String visitName = doctorName.getText().toString();

            visitPresenter.createVisit(dialog,visitType,
                    animalList.get(posizione),
                    new Timestamp(date),
                    visitName,profile.getFirebaseID());

            dialog.cancel();
            visitOpen=false;

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void launchEditDialog() {

        getArguments().putBoolean("editOpen",true);

        dialog =new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        switch (this.role){
            case PRIVATE:
                dialog.setContentView(R.layout.private_profile_edit);

                editPhotoImageView = dialog.findViewById(R.id.profile_picture);
                nameEditText = dialog.findViewById(R.id.nameEditText);
                surnameEditText = dialog.findViewById(R.id.surnameEditText);
                dateTextView = dialog.findViewById(R.id.date_text_view);
                taxIDEditText = dialog.findViewById(R.id.tax_id_EditText);
                phoneEditText = dialog.findViewById(R.id.phoneNumberEditText);
                emailEditText = dialog.findViewById(R.id.emailEditText);
                passwordEditText = dialog.findViewById(R.id.passwordEditText);

                break;
            case VETERINARIAN:
                dialog.setContentView(R.layout.veterinarian_profile_edit);

                editPhotoImageView = dialog.findViewById(R.id.profile_picture);
                companyNameEditText = dialog.findViewById(R.id.nameEditText);
                siteEditText = dialog.findViewById(R.id.surnameEditText);
                Spinner prefixSpinner = dialog.findViewById(R.id.prefix_spinner);
                phoneEditText = dialog.findViewById(R.id.phoneNumberEditText);
                emailEditText = dialog.findViewById(R.id.emailEditText);
                passwordEditText = dialog.findViewById(R.id.passwordEditText);

                break;
            case PUBLIC_AUTHORITY:
                dialog.setContentView(R.layout.authority_profile_edit);

                editPhotoImageView = dialog.findViewById(R.id.profile_picture);
                companyNameEditText = dialog.findViewById(R.id.nameEditText);
                siteEditText = dialog.findViewById(R.id.surnameEditText);
                prefixSpinner = dialog.findViewById(R.id.prefix_spinner);
                phoneEditText = dialog.findViewById(R.id.phoneNumberEditText);
                emailEditText = dialog.findViewById(R.id.emailEditText);
                passwordEditText = dialog.findViewById(R.id.passwordEditText);

                break;
        }

        Button saveButton = dialog.findViewById(R.id.save_button);
        Button deleteButton = dialog.findViewById(R.id.delete_button);
        Button editPhotoButton = dialog.findViewById(R.id.edit_button);
        Button logoutButton = dialog.findViewById(R.id.logout_button);



        saveButton.setOnClickListener(v -> {
            switch (role) {
                case PRIVATE:
                    try {
                        String name = nameEditText.getText().toString();
                        String surname = surnameEditText.getText().toString();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date birthDate = dateFormat.parse(dateTextView.getText().toString());

                        String taxID = taxIDEditText.getText().toString();
                        String phone = phoneEditText.getText().toString();
                        String email = emailEditText.getText().toString();
                        String password = passwordEditText.getText().toString();

                        String site = null;
                        String companyname = null;
                        userPresenter.mailcheckUpdateProfile(name, surname, birthDate, taxID, phone, email, password, site, companyname);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case VETERINARIAN:
                            String companyname = companyNameEditText.getText().toString();
                            String site = "N/D";/*siteEditText.getText().toString(); */ //TODO= Aggiungere parametro alla funzione
                            String phone = phoneEditText.getText().toString();
                            String email = emailEditText.getText().toString();
                            String password = passwordEditText.getText().toString();
                            String name = null;
                            String surname = null;
                            Date birthDate = null;
                            String taxID = null;
                            userPresenter.mailcheckUpdateProfile(name, surname, birthDate, taxID, phone, email, password, site, companyname);

                        break;
                    case PUBLIC_AUTHORITY:
                            companyname = companyNameEditText.getText().toString();
                            site = "N/D";/*siteEditText.getText().toString(); */ //TODO= Aggiungere parametro alla funzione
                            phone = phoneEditText.getText().toString();
                            email = emailEditText.getText().toString();
                            password = passwordEditText.getText().toString();
                            name = null;
                            surname = null;
                            birthDate = null;
                            taxID = null;
                            userPresenter.mailcheckUpdateProfile(name, surname, birthDate, taxID, phone, email, password, site, companyname);
                        break;
                }

            editOpen=false;
        });

        deleteButton.setOnClickListener(v -> showDeleteConfirm());

        logoutButton.setOnClickListener(v -> {
            final AnimalAppDialog logoutDialog=new AnimalAppDialog(getContext());

            logoutDialog.setContentView(getContext().getString(R.string.confirm_logout), AnimalAppDialog.DialogType.INFO);

            logoutDialog.setConfirmAction(t -> {
                //TODO qui fai il callback per il logout
            });

            logoutDialog.setUndoAction(t -> logoutDialog.cancel());

            logoutDialog.show();
        });

        editPhotoButton.setOnClickListener(v -> {
            Intent photoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerResultLauncher.launch(photoIntent);
        });

        userPresenter.initUserData();

        Spinner prefixSpinner= dialog.findViewById(R.id.prefix_spinner);
        ArrayAdapter<CharSequence> prefixAdapter= ArrayAdapter.createFromResource(getContext(),R.array.phone_prefixes,
                android.R.layout.simple_list_item_1);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixSpinner.setAdapter(prefixAdapter);


        Button backButton= dialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {
            dialog.cancel();
            editOpen=false;
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }



    private void changeTab(ProfileFragment.TabPosition tabType,Boolean withAnimation){
        fragment=null;
        int enterAnimation,exitAnimation;

        switch (tabType){
            case ANIMAL:
                if(previousTab.tabPosition!= TabPosition.ANIMAL) {
                    previousTab.tabPosition= TabPosition.ANIMAL;
                    fragment= ListFragment.newInstanceProfile(previousTab,this.profileType,this.profile);
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
                    fragment= ListFragment.newInstanceProfile(previousTab,this.profileType,this.profile);
                }
                else{
                    return;
                }
                break;
            case EXPENSE:
                if(previousTab.tabPosition!= TabPosition.EXPENSE){
                    previousTab.tabPosition= TabPosition.EXPENSE;
                    fragment= ListFragment.newInstanceProfile(previousTab,this.profileType,this.profile);
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
        transaction.replace(R.id.recycle_container,fragment,FRAGMENT_TAG).commit();
    }

    public void onInitPrivateData(Private userPrivate) {
        nameEditText.setText(userPrivate.getName());
        surnameEditText .setText(userPrivate.getSurname());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");   // TODO: Spostare in utils
        String dateString = dateFormat.format(userPrivate.getBirthDate());
        dateTextView.setText(dateString);

        taxIDEditText.setText(userPrivate.getTaxIDCode());
        phoneEditText.setText(Long.toString(userPrivate.getPhone()));
        emailEditText.setText(userPrivate.getEmail());
        passwordEditText.setText(userPrivate.getPassword());
        editPhotoImageView.setImageBitmap(userPrivate.getPhoto());
    }

    public void onInitAuthorityData(PublicAuthority userAuthority) {
        companyNameEditText.setText(userAuthority.getName());
        siteEditText.setText("N/D"); //TODO: Verificare il legal site

        phoneEditText.setText(Long.toString(userAuthority.getPhone()));
        emailEditText.setText(userAuthority.getEmail());
        passwordEditText.setText(userAuthority.getPassword());
        editPhotoImageView.setImageBitmap(userAuthority.getPhoto());
    }

    public void onInitVeterinarianData(Veterinarian userVeterinarian) {
        companyNameEditText.setText(userVeterinarian.getName());
        siteEditText.setText("N/D"); //TODO: Verificare il legal site

        phoneEditText.setText(Long.toString(userVeterinarian.getPhone()));
        emailEditText.setText(userVeterinarian.getEmail());
        passwordEditText.setText(userVeterinarian.getPassword());
        editPhotoImageView.setImageBitmap(userVeterinarian.getPhoto());
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
            case 6:
                emailEditText.setError(this.getString(R.string.already_used_email));
                break;
        }
    }

    public void showUpdateSuccessful(User profile) {
        Toast.makeText(requireContext(), this.getString(R.string.profile_update_successful), Toast.LENGTH_SHORT).show();
        refresh(profile);
        dialog.cancel();
        this.editOpen=false;
    }

    private void showDeleteConfirm() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(this.getString(R.string.profile_delete_alert_title));
        builder.setMessage(this.getString(R.string.profile_delete_alert_mex1));
        builder.setPositiveButton(this.getString(R.string.confirm), (dialog, which) -> {
            userPresenter.deleteProfile();
            editOpen=false;
        });
        builder.setNegativeButton(this.getString(R.string.profile_delete_alert_cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showLogoutSuccessful() {

        Toast.makeText(requireContext(), this.getString(R.string.profile_delete_successful), Toast.LENGTH_SHORT).show();

        ((MainActivity)getActivity()).changeTab(MainActivity.TabPosition.HOME);
        dialog.cancel();
        this.editOpen=false;
    }

    public void showLogoutError() {
        Toast.makeText(requireContext(), this.getString(R.string.profile_delete_failed), Toast.LENGTH_SHORT).show();
    }

    public void setPhotoPicked(Bitmap bitmap) {
        editPhotoImageView.setImageBitmap(bitmap);
    }

    public Bitmap getPhotoPicked() {
        return ((BitmapDrawable) editPhotoImageView.getDrawable()).getBitmap();
    }

    public void showPhotoUpdateError() {
        Toast.makeText(requireContext(), this.getString(R.string.photo_update_failed), Toast.LENGTH_SHORT).show();
    }

}
