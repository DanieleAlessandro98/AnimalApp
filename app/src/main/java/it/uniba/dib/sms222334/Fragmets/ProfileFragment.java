package it.uniba.dib.sms222334.Fragmets;

import android.annotation.SuppressLint;
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
import android.location.Address;
import android.location.Geocoder;
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
import android.widget.AutoCompleteTextView;
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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import it.uniba.dib.sms222334.Utils.CoordinateUtilities;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Presenters.UserPresenter;
import it.uniba.dib.sms222334.Presenters.VisitPresenter;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;

public class ProfileFragment extends Fragment implements OnMapReadyCallback {
    final static String TAG="ProfileFragment";
    private static final String MAPVIEW_BUNDLE_KEY="MapViewBundleKey";

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

    Button editButton, addVisitButton, logoutButton;
    ImageButton callButton;

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
    private AutoCompleteTextView locationEditText;
    private UserPresenter userPresenter;

    private User profile;

    private ImageView editPhotoImageView;
    private ImageView photoImageView;
    private Button saveButton;
    private Button deleteButton;
    private Button editPhotoButton;
    private ImageButton searchLocationButton;

    private ImageView profilePhoto;

    public Dialog dialog;
    private TextView nameView;
    private TextView emailView;

    private ListFragment fragment;
    private VisitPresenter visitPresenter;

    private boolean tabEnabled;
    private MapView mapView;
    private GoogleMap map;
    private boolean mapEnable = false;

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

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView()");

        this.previousTab=new Tab();

        tabEnabled=true;

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
        else if(SessionManager.getInstance().getCurrentUser().getFirebaseID().compareTo(profile.getFirebaseID()) == 0 ){
            inflatedLayout =R.layout.owner_profile_fragment;
        }
        else{
            inflatedLayout =R.layout.stranger_owner_profile_fragment;
        }

        final View layout= inflater.inflate(inflatedLayout,container,false);

        nameView = layout.findViewById(R.id.name);
        emailView = layout.findViewById(R.id.email);
        profilePhoto = layout.findViewById(R.id.profile_picture);
        tabLayout=layout.findViewById(R.id.tab_layout);
        editButton=layout.findViewById(R.id.edit_button);
        logoutButton=layout.findViewById(R.id.logout_button);

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
                this.callButton=layout.findViewById(R.id.call_button);
                this.addVisitButton.setVisibility(View.INVISIBLE);
                this.callButton.setVisibility(View.INVISIBLE);
            }

            if(SessionManager.getInstance().getCurrentUser().getFirebaseID().compareTo(this.profile.getFirebaseID()) != 0){
                tabLayout.setVisibility(View.GONE);
                tabEnabled=false;
            }
        }

        if((this.role == UserRole.VETERINARIAN) || (SessionManager.getInstance().getCurrentUser().getFirebaseID().compareTo(this.profile.getFirebaseID()) != 0)){
            this.callButton=layout.findViewById(R.id.call_button);

            this.callButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + this.profile.getPhone()));
                startActivity(intent);
            });
        }

        if(profile.getFirebaseID().compareTo(SessionManager.getInstance().getCurrentUser().getFirebaseID())!=0) {
            Bundle mapViewBundle=null;
            if(savedInstanceState!=null)
                mapViewBundle=savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);

            mapView=(MapView) layout.findViewById(R.id.mapview);
            mapView.onCreate(mapViewBundle);
            mapView.getMapAsync(this);

            mapEnable = true;

            mapView.setVisibility(View.VISIBLE);

            editButton.setVisibility(View.INVISIBLE);
            logoutButton.setVisibility(View.INVISIBLE);
        }

        userPresenter = new UserPresenter(this);

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

        if(tabEnabled) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    clickedTab = TabPosition.values()[tab.getPosition()];
                    changeTab(clickedTab, true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            tabLayout.selectTab(tabLayout.getTabAt(this.clickedTab.ordinal()));

            if (getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null)
                changeTab(this.clickedTab, false);
        }

        logoutButton.setOnClickListener(v -> {
            final AnimalAppDialog logoutDialog=new AnimalAppDialog(getContext());

            logoutDialog.setContentView(getContext().getString(R.string.confirm_logout), AnimalAppDialog.DialogType.INFO);
            logoutDialog.setConfirmAction(t -> {
                SessionManager.getInstance().logoutUser();
                logoutDialog.cancel();
                showLogoutSuccessful(false);
            });

            logoutDialog.setUndoAction(t -> logoutDialog.cancel());
            logoutDialog.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mapEnable)
            mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mapEnable)
            mapView.onPause();
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
        Log.d(TAG, "onsaveInstanceState()");

        if (mapEnable) {
            Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
            if (mapViewBundle == null) {
                mapViewBundle = new Bundle();
                outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
            }

            mapView.onSaveInstanceState(mapViewBundle);
        }
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

        if (mapEnable)
            mapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG,"onDetach()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        if (mapEnable)
            mapView.onLowMemory();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(profile.getLocation().getLatitude(), profile.getLocation().getLongitude())));
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
                    visitName,(Veterinarian) profile);

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
                locationEditText = dialog.findViewById(R.id.location_edit_text);
                emailEditText = dialog.findViewById(R.id.emailEditText);
                passwordEditText = dialog.findViewById(R.id.passwordEditText);
                break;

            case VETERINARIAN:
                dialog.setContentView(R.layout.veterinarian_profile_edit);

                editPhotoImageView = dialog.findViewById(R.id.profile_picture);
                companyNameEditText = dialog.findViewById(R.id.nameEditText);
                locationEditText = dialog.findViewById(R.id.location_edit_text);
                Spinner prefixSpinner = dialog.findViewById(R.id.prefix_spinner);
                phoneEditText = dialog.findViewById(R.id.phoneNumberEditText);
                emailEditText = dialog.findViewById(R.id.emailEditText);
                passwordEditText = dialog.findViewById(R.id.passwordEditText);
                break;

            case PUBLIC_AUTHORITY:
                dialog.setContentView(R.layout.authority_profile_edit);

                editPhotoImageView = dialog.findViewById(R.id.profile_picture);
                companyNameEditText = dialog.findViewById(R.id.nameEditText);
                prefixSpinner = dialog.findViewById(R.id.prefix_spinner);
                phoneEditText = dialog.findViewById(R.id.phoneNumberEditText);
                locationEditText = dialog.findViewById(R.id.location_edit_text);
                emailEditText = dialog.findViewById(R.id.emailEditText);
                passwordEditText = dialog.findViewById(R.id.passwordEditText);
                break;
        }

        Button saveButton = dialog.findViewById(R.id.save_button);
        Button deleteButton = dialog.findViewById(R.id.delete_button);
        Button editPhotoButton = dialog.findViewById(R.id.edit_button);
        searchLocationButton = dialog.findViewById(R.id.search_location_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line);
        locationEditText.setAdapter(adapter);

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

                        String location = locationEditText.getText().toString();
                        String companyname = null;
                        userPresenter.mailcheckUpdateProfile(name, surname, birthDate, taxID, phone, email, password, location, companyname);

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;

                    case VETERINARIAN:
                            String companyname = companyNameEditText.getText().toString();
                            String site = locationEditText.getText().toString();
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
                            site = locationEditText.getText().toString();
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

        editPhotoButton.setOnClickListener(v -> {
            Intent photoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerResultLauncher.launch(photoIntent);
        });

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
        locationEditText.setText(CoordinateUtilities.getAddressFromLatLng(getContext(), userPrivate.getLocation(), false));
        emailEditText.setText(userPrivate.getEmail());
        passwordEditText.setText(userPrivate.getPassword());
        editPhotoImageView.setImageBitmap(userPrivate.getPhoto());
    }

    public void onInitAuthorityData(PublicAuthority userAuthority) {
        companyNameEditText.setText(userAuthority.getName());
        locationEditText.setText(CoordinateUtilities.getAddressFromLatLng(getContext(), userAuthority.getLocation(), false));

        phoneEditText.setText(Long.toString(userAuthority.getPhone()));
        emailEditText.setText(userAuthority.getEmail());
        passwordEditText.setText(userAuthority.getPassword());
        editPhotoImageView.setImageBitmap(userAuthority.getPhoto());
    }

    public void onInitVeterinarianData(Veterinarian userVeterinarian) {
        companyNameEditText.setText(userVeterinarian.getName());
        locationEditText.setText(CoordinateUtilities.getAddressFromLatLng(getContext(), userVeterinarian.getLocation(), false));

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

            case 7:
                locationEditText.setError(this.getString(R.string.invalid_user_location));
                break;
        }
    }

    public void showUpdateSuccessful(User profile) {
        Toast.makeText(requireContext(), this.getString(R.string.profile_update_successful), Toast.LENGTH_SHORT).show();
        refresh(profile);
        dialog.cancel();
        this.editOpen=false;
    }

    public void showUpdateError() {
        Toast.makeText(requireContext(), this.getString(R.string.profile_update_error), Toast.LENGTH_SHORT).show();
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

    public void showLogoutSuccessful(boolean deletedAccount) {

        if (deletedAccount)
            Toast.makeText(requireContext(), this.getString(R.string.profile_delete_successful), Toast.LENGTH_SHORT).show();

        if (dialog != null)
            dialog.cancel();

        ((MainActivity)getActivity()).changeTab(MainActivity.TabPosition.HOME);
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
