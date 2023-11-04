package it.uniba.dib.sms222334.Fragmets;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Presenters.AnimalPresenter;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.AnimalStates;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;
import it.uniba.dib.sms222334.Views.Carousel.CarouselPageAdapter;

public class AnimalFragment extends Fragment {

    final static String TAG="AnimalFragment";
    Animal animal;

    Button editButton,backButton;
    TabLayout tabLayout;

    TextView name,species,race,age,state;

    public ViewPager2 mViewPager;

    ShapeableImageView profileImage;

    ProfileFragment.Type profileType;

    boolean profilePictureFlag=false;

    ImageView newProfilePicture; /*i added this here because it has to be passed on onActivityResult,
                                and this must be set before fragment is created(onCreateView())*/

    private ProfileFragment.Tab previousTab, clickedTab;

    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    public AnimalFragment(){}

    public static AnimalFragment newInstance(Animal animal) {
        AnimalFragment myFragment = new AnimalFragment();

        Bundle args = new Bundle();
        args.putParcelable("animalData", animal);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(AnimalPresenter.checkAnimalProperty(animal) || (SessionManager.getInstance().getCurrentUser().getRole() == UserRole.VETERINARIAN)){
            tabLayout.selectTab(tabLayout.getTabAt(this.clickedTab.tabPosition.ordinal()));
            changeTab(this.clickedTab.tabPosition,false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if((SessionManager.getInstance().getCurrentUser().getRole() == UserRole.VETERINARIAN) || (AnimalPresenter.checkAnimalProperty(animal)))
            outState.putInt("tab_position",this.clickedTab.tabPosition.ordinal());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout;

        this.profileType= ProfileFragment.Type.ANIMAL;

        Bundle args = getArguments();
        if (args != null) {
            animal = args.getParcelable("animalData");
        }

        if(AnimalPresenter.checkAnimalProperty(animal)){
            layout= inflater.inflate(R.layout.animal_fragment,container,false);
            tabLayout=layout.findViewById(R.id.tab_layout);

            this.previousTab=new ProfileFragment.Tab();

            this.clickedTab=new ProfileFragment.Tab();

            if(savedInstanceState!=null){
                this.clickedTab.tabPosition= ProfileFragment.TabPosition.values()[savedInstanceState.getInt("tab_position",0)];
            }
            else{
                this.clickedTab.tabPosition= ProfileFragment.TabPosition.RELATION;
            }

            initTabListener();

            editButton=layout.findViewById(R.id.edit_button);

            this.photoPickerResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData()!=null) {
                            Uri selectedImage = result.getData().getData();
                            newProfilePicture.setImageURI(selectedImage);
                            profilePictureFlag=true;
                        }
                    });

            editButton.setOnClickListener(v -> launchEditDialog());
        }
        else if(SessionManager.getInstance().getCurrentUser().getRole() == UserRole.VETERINARIAN){
            layout= inflater.inflate(R.layout.animal_fragment,container,false);
            tabLayout=layout.findViewById(R.id.tab_layout);

            editButton=layout.findViewById(R.id.edit_button);

            editButton.setVisibility(View.INVISIBLE);

            this.previousTab=new ProfileFragment.Tab();

            this.clickedTab=new ProfileFragment.Tab();

            if(savedInstanceState!=null){
                this.clickedTab.tabPosition= ProfileFragment.TabPosition.values()[savedInstanceState.getInt("tab_position",0)];
            }
            else{
                this.clickedTab.tabPosition= ProfileFragment.TabPosition.RELATION;
            }

            initTabListener();
        }
        else{
            layout= inflater.inflate(R.layout.stranger_animal_fragment,container,false);
        }

        this.name=layout.findViewById(R.id.name);
        this.species=layout.findViewById(R.id.species);
        this.race=layout.findViewById(R.id.race);
        this.age=layout.findViewById(R.id.age);
        this.state=layout.findViewById(R.id.state);
        this.profileImage=layout.findViewById(R.id.profile_picture);

        this.mViewPager=layout.findViewById(R.id.carousel);

        refresh(animal);

        backButton=layout.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return layout;
    }

    private void initTabListener(){
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    clickedTab.tabPosition= ProfileFragment.TabPosition.values()[tab.getPosition()];

                    changeTab(clickedTab.tabPosition,true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
    }

    private void changeTab(ProfileFragment.TabPosition tabType,Boolean withAnimation){
        Fragment fragment;
        int enterAnimation,exitAnimation;

        switch (tabType){
            case RELATION:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.RELATION) {
                    previousTab.tabPosition= ProfileFragment.TabPosition.RELATION;
                    fragment= ListFragment.newInstanceAnimal(previousTab,animal);
                    enterAnimation=withAnimation?R.anim.slide_right_in:0;
                    exitAnimation=withAnimation?R.anim.slide_right_out:0;
                }
                else{
                    return;
                }
                break;
            case HEALTH:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.HEALTH) {
                    if (previousTab.tabPosition == ProfileFragment.TabPosition.RELATION) {
                        enterAnimation=withAnimation?R.anim.slide_left_in:0;
                        exitAnimation=withAnimation?R.anim.slide_left_out:0;
                    }
                    else {
                        enterAnimation=withAnimation?R.anim.slide_right_in:0;
                        exitAnimation=withAnimation?R.anim.slide_right_out:0;
                    }

                    previousTab.tabPosition= ProfileFragment.TabPosition.HEALTH;
                    fragment= ListFragment.newInstanceAnimal(previousTab,animal);
                }
                else{
                    return;
                }
                break;
            case FOOD:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.FOOD) {
                    if ((previousTab.tabPosition == ProfileFragment.TabPosition.RELATION) || (previousTab.tabPosition == ProfileFragment.TabPosition.HEALTH)) {
                        enterAnimation=withAnimation?R.anim.slide_left_in:0;
                        exitAnimation=withAnimation?R.anim.slide_left_out:0;
                    }
                    else {
                        enterAnimation=withAnimation?R.anim.slide_right_in:0;
                        exitAnimation=withAnimation?R.anim.slide_right_out:0;
                    }

                    previousTab.tabPosition= ProfileFragment.TabPosition.FOOD;
                    fragment= ListFragment.newInstanceAnimal(previousTab,animal);
                }
                else{
                    return;
                }
                break;
            case VISIT:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.VISIT) {
                    if (previousTab.tabPosition != ProfileFragment.TabPosition.EXPENSE) {
                        enterAnimation=withAnimation?R.anim.slide_left_in:0;
                        exitAnimation=withAnimation?R.anim.slide_left_out:0;
                    }
                    else {
                        enterAnimation=withAnimation?R.anim.slide_right_in:0;
                        exitAnimation=withAnimation?R.anim.slide_right_out:0;
                    }


                    previousTab.tabPosition= ProfileFragment.TabPosition.VISIT;
                    fragment= ListFragment.newInstanceAnimal(previousTab,animal);
                }
                else {
                    return;
                }
                break;
            case EXPENSE:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.EXPENSE) {
                    previousTab.tabPosition= ProfileFragment.TabPosition.EXPENSE;
                    fragment= ListFragment.newInstanceAnimal(previousTab,animal);
                    enterAnimation=withAnimation?R.anim.slide_left_in:0;
                    exitAnimation=withAnimation?R.anim.slide_left_out:0;

                }
                else{
                    return;
                }
                break;
            default:
                changeTab(ProfileFragment.TabPosition.RELATION,true);
                return;
        }

        FragmentManager fragmentManager=getParentFragmentManager();

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.setCustomAnimations(enterAnimation, exitAnimation);
        transaction.replace(R.id.recycle_container,fragment).commit();
    }

    @SuppressLint("ResourceType")
    private void launchEditDialog() {

        final AnimalAppDialog editDialog=new AnimalAppDialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        editDialog.setContentView(R.layout.edit_animal);

        TextView date;
        AnimalAppEditText microchip,nameEditText;
        androidx.appcompat.widget.SearchView searchView=editDialog.findViewById(R.id.owner_chooser);
        AnimalAppEditText owner=editDialog.findViewById(R.id.owner_chooser_edit_text);

        nameEditText=editDialog.findViewById(R.id.nameEditText);
        microchip=editDialog.findViewById(R.id.micro_chipEditText);
        date=editDialog.findViewById(R.id.date_text_view);
        newProfilePicture =editDialog.findViewById(R.id.profile_picture);

        nameEditText.setText(this.animal.getName());
        microchip.setText(this.animal.getMicrochip());
        Calendar birthDate=Calendar.getInstance();
        birthDate.setTime(animal.getBirthDate());
        date.setText(birthDate.get(Calendar.DAY_OF_MONTH)+ "/" + (birthDate.get(Calendar.MONTH)+1) + "/" + birthDate.get(Calendar.YEAR));
        newProfilePicture.setImageBitmap(animal.getPhoto());

        final Calendar c = Calendar.getInstance();
        c.setTime(animal.getBirthDate());

        String oldMicroChip=animal.getMicrochip();

        final String[] newOwnerReference = new String[1];
        newOwnerReference[0]=SessionManager.getInstance().getCurrentUser().getFirebaseID();

        Button editPhotoButton= editDialog.findViewById(R.id.edit_foto_button);
        Button deleteButton= editDialog.findViewById(R.id.delete_button);
        Button saveButton= editDialog.findViewById(R.id.save_button);
        Button editOwner= editDialog.findViewById(R.id.edit_owner_button);
        ImageButton datePickerButton=editDialog.findViewById(R.id.date_picker_button);

        SearchManager searchManager=(SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

        if(searchManager!=null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        final boolean[] isLocked = {true};

        String userEmail=SessionManager.getInstance().getCurrentUser().getEmail();

        owner.setHint(userEmail);

        String[] newEmailOwner=new String[]{owner.getHint().toString()};

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                    return true;
            }
            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter suggestionAdapter = searchView.getSuggestionsAdapter();
                Cursor cursor = suggestionAdapter.getCursor();

                Log.d(TAG, cursor.getColumnName(0)+""+cursor.getColumnName(1));

                if (cursor != null && cursor.moveToPosition(position)) {
                    int suggestionIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1);
                    int referenceIndex= cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_2);
                    String suggestionText = cursor.getString(suggestionIndex);

                    newOwnerReference[0] = cursor.getString(referenceIndex);

                    owner.setHint(suggestionText);

                    newEmailOwner[0]=owner.getHint().toString();

                    switchSearchTextView(searchView,owner,editOwner,R.drawable.baseline_lock_24);

                    isLocked[0] =!isLocked[0];
                }

                return true;
            }
        });

        editOwner.setOnClickListener(v -> {
            switchSearchTextView(searchView,owner,editOwner,isLocked[0] ?R.drawable.baseline_lock_open_24:R.drawable.baseline_lock_24);

            isLocked[0] =!isLocked[0];
        });

        editDialog.setInputCallback(new AnimalCallbacks.inputValidate() {
            @Override
            public void InvalidName() {
                nameEditText.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                nameEditText.setError(getContext().getString(R.string.invalid_user_name));
            }

            @Override
            public void InvalidBirthDate() {
                date.setError(getContext().getString(R.string.invalid_user_birthdate));
            }

            @Override
            public void InvalidMicrochip() {
                microchip.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                microchip.setError(getContext().getString(R.string.invalid_microchip));
            }

            @Override
            public void MicrochipAlreadyUsed() {
                microchip.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                microchip.setError(getContext().getString(R.string.microchip_already_exist));
            }
        });

        datePickerButton.setOnClickListener(v -> {
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        date.setText(dayOfMonth + "/" + (month1 +1) + "/" + year1);
                        date.setError(null);
                        c.set(year1, month1,dayOfMonth);
                    }, year, month, day);

            datePickerDialog.show();
        });

        editPhotoButton.setOnClickListener(v -> {
            Intent photoIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerResultLauncher.launch(photoIntent);
        });

        deleteButton.setOnClickListener(view -> {
            final AnimalAppDialog deleteDialog=new AnimalAppDialog(getContext());

            deleteDialog.setContentView(getContext().getString(R.string.delete_animal_warning), AnimalAppDialog.DialogType.CRITICAL);

            deleteDialog.setConfirmAction(v -> {
                AnimalPresenter presenter=new AnimalPresenter(editDialog);
                presenter.deleteAnimal(animal);
                deleteDialog.cancel();
                getParentFragmentManager().popBackStack();
            });

            deleteDialog.setUndoAction(v -> deleteDialog.cancel());

            deleteDialog.show();
        });

        saveButton.setOnClickListener(v -> {

            if((owner.getVisibility() == View.VISIBLE) && (newEmailOwner[0].compareTo(userEmail) != 0)){
                final AnimalAppDialog saveDialog=new AnimalAppDialog(getContext());

                saveDialog.setContentView(getContext().getString(R.string.change_owner_warning) +" "+ newEmailOwner[0], AnimalAppDialog.DialogType.WARNING);

                saveDialog.setConfirmAction(t -> {
                   updateAnimal(editDialog,c,microchip,nameEditText,newEmailOwner[0],newOwnerReference,oldMicroChip);

                    saveDialog.cancel();
                });

                saveDialog.setUndoAction(t -> saveDialog.cancel());

                saveDialog.show();

            } else if (owner.getVisibility() == View.VISIBLE) {
                updateAnimal(editDialog,c,microchip,nameEditText,newEmailOwner[0],newOwnerReference,oldMicroChip);
            } else{
                Toast.makeText(getContext(), getContext().getString(R.string.right_owner_advice), Toast.LENGTH_SHORT).show();
                switchSearchTextView(searchView,owner,editOwner,R.drawable.baseline_lock_24);
                owner.setInputValidate(AnimalAppEditText.ValidateInput.WARNING_INPUT);

                isLocked[0] =!isLocked[0];
            }
        });

        Button backButton= editDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> editDialog.cancel());

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void refresh(Animal animal){
        this.name.setText(animal.getName());
        this.species.setText(animal.getSpeciesString(animal.getSpecies(), getContext()));
        this.race.setText(animal.getRace());
        this.age.setText(DateUtilities.calculateAge(animal.getBirthDate(),getContext()));
        this.state.setText(AnimalStates.values()[animal.getState().ordinal()].toString());
        this.profileImage.setImageBitmap(animal.getPhoto());

        CarouselPageAdapter mAdapter = new CarouselPageAdapter(this, animal);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageTransformer(mAdapter);

        mViewPager.setCurrentItem(0);
    }

    private void updateAnimal(AnimalAppDialog editDialog, Calendar c, AnimalAppEditText microchip,AnimalAppEditText nameEditText, String newEmailOwner, String[] newOwnerReference, String oldMicroChip){
        AnimalPresenter presenter=new AnimalPresenter(editDialog,this);

        animal.setBirthDate(c.getTime());
        animal.setName(nameEditText.getText().toString());
        animal.setMicrochip(microchip.getText().toString());
        animal.setPhoto(((BitmapDrawable) newProfilePicture.getDrawable()).getBitmap());
        animal.setOwnerReference(newOwnerReference[0]);

        presenter.editAnimal(animal,newEmailOwner,oldMicroChip,profilePictureFlag);
    }

    private void switchSearchTextView(SearchView search, AnimalAppEditText edit, Button editOwner,int resId){
        edit.setVisibility(Math.abs(edit.getVisibility()-View.GONE));
        search.setVisibility(Math.abs(search.getVisibility()-View.GONE));
        search.setQuery(edit.getHint(),false);

        if(search.isIconified()){
            search.setIconified(false);
            search.setIconified(false);
            //do not delete this!! it's duplicated because the searchView on the first one clear the query text
            //on the second one iconified it
        }

        editOwner.setBackgroundResource(resId);
        ViewGroup.MarginLayoutParams layoutParams= (ViewGroup.MarginLayoutParams) editOwner.getLayoutParams();
        layoutParams.topMargin=Math.abs(layoutParams.topMargin-23);
    }

}
