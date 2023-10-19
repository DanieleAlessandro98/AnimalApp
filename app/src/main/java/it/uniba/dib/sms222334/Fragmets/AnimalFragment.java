package it.uniba.dib.sms222334.Fragmets;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.preference.PreferenceManager;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.Calendar;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Presenters.AnimalPresenter;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.AnimalStates;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;

public class AnimalFragment extends Fragment {

    final static String TAG="AnimalFragment";

    Button editButton,backButton;
    TabLayout tabLayout;

    TextView name,species,race,age,owner,state;

    ProfileFragment.Type profileType;

    boolean profilePictureFlag=false;

    ImageView profilePicture; /*i added this here because it has to be passed on onActivityResult,
                                and this must be set before fragment is created(onCreateView())*/

    Animal animal;


    private ProfileFragment.Tab previousTab;

    public SharedPreferences.Editor editor;
    private static SharedPreferences preferences;

    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    public AnimalFragment(){

    }

    public static AnimalFragment newInstance(Animal animal, Context context) {
        AnimalFragment myFragment = new AnimalFragment();

        preferences= PreferenceManager.getDefaultSharedPreferences(context);
        myFragment.editor= preferences.edit();
        myFragment.editor.putString("animalData", new Gson().toJson(animal));
        myFragment.editor.commit();

        return myFragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(true) //TODO da implementare il check della proprietà dell'animale
            changeTab(ProfileFragment.TabPosition.RELATION,false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout;

        this.profileType= ProfileFragment.Type.ANIMAL;

        if(true){ //TODO 2_da implementare il check della proprietà dell'animale
            layout= inflater.inflate(R.layout.animal_fragment,container,false);
            tabLayout=layout.findViewById(R.id.tab_layout);

            this.previousTab=new ProfileFragment.Tab();

            this.photoPickerResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData()!=null) {
                            Uri selectedImage = result.getData().getData();
                            profilePicture.setImageURI(selectedImage);
                            profilePictureFlag=true;
                        }
                    });

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()){
                        case 0:
                            changeTab(ProfileFragment.TabPosition.RELATION,true);
                            break;

                        case 1:
                            changeTab(ProfileFragment.TabPosition.HEALTH,true);
                            break;

                        case 2:
                            changeTab(ProfileFragment.TabPosition.FOOD,true);
                            break;

                        case 3:
                            changeTab(ProfileFragment.TabPosition.VISIT,true);
                            break;

                        case 4:
                            changeTab(ProfileFragment.TabPosition.EXPENSE,true);
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

            editButton=layout.findViewById(R.id.edit_button);

            editButton.setOnClickListener(v -> launchEditDialog());
        }
        else{
            layout= inflater.inflate(R.layout.stranger_animal_fragment,container,false);
        }

        final String animaString=preferences.getString("animalData","");

        this.animal=new Gson().fromJson(animaString, new TypeToken<Animal>() {}.getType());

        this.name=layout.findViewById(R.id.name);
        this.species=layout.findViewById(R.id.species);
        this.race=layout.findViewById(R.id.race);
        this.age=layout.findViewById(R.id.age);
        this.owner=layout.findViewById(R.id.owner);
        this.state=layout.findViewById(R.id.state);


        refresh(animal);


        backButton=layout.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());


        return layout;
    }

    private void changeTab(ProfileFragment.TabPosition tabType,Boolean withAnimation){
        Fragment fragment=null;
        int enterAnimation=0,exitAnimation=0;

        switch (tabType){
            case RELATION:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.RELATION) {
                    previousTab.tabPosition= ProfileFragment.TabPosition.RELATION;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
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
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
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
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
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
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
                }
                else {
                    return;
                }
                break;
            case EXPENSE:
                if(previousTab.tabPosition!= ProfileFragment.TabPosition.EXPENSE) {
                    previousTab.tabPosition= ProfileFragment.TabPosition.EXPENSE;
                    fragment= ListFragment.newInstance(previousTab,this.profileType);
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

    private void launchEditDialog() {

        final AnimalAppDialog editDialog=new AnimalAppDialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        editDialog.setContentView(R.layout.edit_animal);

        TextView name,microchip,date;
        androidx.appcompat.widget.SearchView searchView=editDialog.findViewById(R.id.owner_chooser);

        name=editDialog.findViewById(R.id.nameEditText);
        microchip=editDialog.findViewById(R.id.micro_chipEditText);
        date=editDialog.findViewById(R.id.date_text_view);
        profilePicture=editDialog.findViewById(R.id.profile_picture);

        name.setText(this.animal.getName());
        microchip.setText(this.animal.getMicrochip());
        Calendar birthDate=Calendar.getInstance();
        birthDate.setTime(animal.getBirthDate());
        date.setText(birthDate.get(Calendar.DAY_OF_MONTH)+ "/" + (birthDate.get(Calendar.MONTH)+1) + "/" + birthDate.get(Calendar.YEAR));
        profilePicture.setImageBitmap(animal.getPhoto());
        searchView.setQuery(SessionManager.getInstance().getCurrentUser().getEmail(),false);

        final Calendar c = Calendar.getInstance();
        c.setTime(animal.getBirthDate());

        boolean dateIsSetted[]=new boolean[]{false};

        Button editPhotoButton= editDialog.findViewById(R.id.edit_foto_button);
        Button deleteButton= editDialog.findViewById(R.id.delete_button);
        Button saveButton= editDialog.findViewById(R.id.save_button);
        Button editOwner= editDialog.findViewById(R.id.edit_owner_button);
        ImageButton datePickerButton=editDialog.findViewById(R.id.date_picker_button);

        SearchManager searchManager=(SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);

        if(searchManager!=null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
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
                    String suggestionText = cursor.getString(suggestionIndex);

                    searchView.setQuery(suggestionText, false);
                }

                return true;
            }
        });

        editOwner.setOnClickListener(v -> {
            searchView.setEnabled(!searchView.isEnabled());
        });

        editDialog.setInputCallback(new AnimalCallbacks.inputValidate() {
            @Override
            public void InvalidName() {
                name.setError("nome non valido");
            }

            @Override
            public void InvalidBirthDate() {
                date.setError("data non valida");
            }

            @Override
            public void InvalidMicrochip() {
                microchip.setError("microchip non valido");
            }

            @Override
            public void MicrochipAlreadyUsed() {
                microchip.setError("microchip esiste già");
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
                        dateIsSetted[0]=true;
                    }, year, month, day);

            datePickerDialog.show();
        });

        editPhotoButton.setOnClickListener(v -> {
            Intent photoIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerResultLauncher.launch(photoIntent);
        });

        deleteButton.setOnClickListener(view -> {
            final Dialog deleteDialog=new AnimalAppDialog(getContext());
            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            deleteDialog.setContentView(R.layout.delete_dialog);

            Button undoButton,confirmButton;

            undoButton=deleteDialog.findViewById(R.id.undo_button);
            confirmButton=deleteDialog.findViewById(R.id.delete_button);

            undoButton.setOnClickListener(v -> deleteDialog.cancel());

            confirmButton.setOnClickListener(v -> {
                        AnimalPresenter presenter=new AnimalPresenter(editDialog);
                        presenter.deleteAnimal(animal);
                        deleteDialog.cancel();
                        getParentFragmentManager().popBackStack();
                    }
            );


            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            deleteDialog.show();
        });

        saveButton.setOnClickListener(v -> {
            AnimalPresenter presenter=new AnimalPresenter(editDialog,this);

            String oldMicroChip=animal.getMicrochip();

            animal.setBirthDate(c.getTime());
            animal.setName(name.getText().toString());
            animal.setMicrochip(microchip.getText().toString());
            animal.setPhoto(((BitmapDrawable)profilePicture.getDrawable()).getBitmap());
            animal.setOwnerReference(searchView.getQuery().toString());

            presenter.editAnimal(animal,oldMicroChip,profilePictureFlag);
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
        this.owner.setText(animal.getOwnerReference());
        this.state.setText(AnimalStates.values()[animal.getState().ordinal()].toString());
    }

}
