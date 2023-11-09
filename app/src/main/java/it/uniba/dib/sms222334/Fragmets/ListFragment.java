package it.uniba.dib.sms222334.Fragmets;

import static android.app.Activity.RESULT_OK;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.PathologyDao;
import it.uniba.dib.sms222334.Database.Dao.RelationDao;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Food;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Pathology;
import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Presenters.AnimalPresenter;
import it.uniba.dib.sms222334.Presenters.ExpensePresenter;
import it.uniba.dib.sms222334.Presenters.FoodPresenter;
import it.uniba.dib.sms222334.Presenters.PathologyPresenter;
import it.uniba.dib.sms222334.Presenters.RelationPresenter;
import it.uniba.dib.sms222334.Presenters.VisitPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.AnimalStates;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;
import it.uniba.dib.sms222334.Views.RecycleViews.Animal.AnimalAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.Expences.ExpenseAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.Relation.RelationAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.SimpleText.SimpleTextAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.SwipeHelper;
import it.uniba.dib.sms222334.Views.RecycleViews.Visit.VisitAdapter;

public class ListFragment extends Fragment{

    final static String TAG = "ListFragment";

    ImageButton addButton;
    ImageView profilePicture; /*i added this here because it has to be passed on onActivityResult,
                                and this must be set before fragment is created(onCreateView())*/

    public static RecyclerView recyclerView;


    ProfileFragment.TabPosition tabPosition;
    ProfileFragment.Type profileType; //profile type that you're actually seeing(it can be animal profile)

    AnimalPresenter animalPresenter;

    public User currentUser; //profile of the user logged
    UserRole currentUserRole;

    private Animal animal;

    private User profile;

    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    public ListFragment() {
    }


    public static ListFragment newInstanceProfile(ProfileFragment.Tab tabPosition, ProfileFragment.Type profileType, User profile) {
        ListFragment myFragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt("tab_position", tabPosition.tabPosition.ordinal());
        args.putInt("profile_type", profileType.ordinal());
        args.putParcelable("profileData", profile);
        myFragment.setArguments(args);

        return myFragment;
    }

    //per l'animale
    public static ListFragment newInstanceAnimal(ProfileFragment.Tab tabPosition, Animal animal) {
        ListFragment myFragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt("tab_position", tabPosition.tabPosition.ordinal());
        args.putInt("profile_type", ProfileFragment.Type.ANIMAL.ordinal());
        args.putParcelable("animalData", animal);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG,"onAttach()");

        Bundle bundle=getArguments();

        if(bundle!=null)
            this.tabPosition= ProfileFragment.TabPosition.values()[bundle.getInt("tab_position")];
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView()");
        final View layout = inflater.inflate(R.layout.item_list_container, container, false);

        currentUser=SessionManager.getInstance().getCurrentUser();
        currentUserRole=currentUser.getRole();

        this.profileType = ProfileFragment.Type.values()[getArguments().getInt("profile_type")];

        animal = getArguments().getParcelable("animalData"); //è nullo nel caso del profilo

        this.profile = getArguments().getParcelable("profileData");


        addButton=layout.findViewById(R.id.add_button);
        recyclerView=layout.findViewById(R.id.list_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new ItemDecorator(0));


        switch (this.tabPosition){
            case ANIMAL:
                setAnimalList();
                break;
            case VISIT:
                setVisitList();
                break;
            case EXPENSE:
                setExpenseList();
                break;
            case RELATION:
                setRelationList();
                break;
            case HEALTH:
                setHealtList();
                break;
            case FOOD:
                setFoodList();
                break;
        }

        return layout;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG,"onViewStateRestored()");

        Bundle args=getArguments();

        if(args!=null) {
            this.tabPosition = ProfileFragment.TabPosition.values()[args.getInt("tab_position", 0)];
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG,"onStart()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop()");

        Bundle bundle=getArguments();

        if(bundle!=null) {
            bundle.putInt("tab_position", this.tabPosition.ordinal());
        }
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

    private void launchAddDialog() {
        final AnimalAppDialog addDialog=new AnimalAppDialog(getContext());

        //is is a owner profile it can add animal, veterinarian can't add anything
        if((this.profileType == ProfileFragment.Type.PUBLIC_AUTHORITY) || (this.profileType == ProfileFragment.Type.PRIVATE) ){
            if(this.tabPosition== ProfileFragment.TabPosition.ANIMAL){
                addDialog.setContentView(R.layout.add_animal);

                animalPresenter= new AnimalPresenter(addDialog);

                final Calendar c = Calendar.getInstance();

                boolean dateIsSetted[]=new boolean[]{false};

                Button saveButton= addDialog.findViewById(R.id.save_button);

                Button addPhotoButton= addDialog.findViewById(R.id.edit_button);

                ImageButton datePickerButton=addDialog.findViewById(R.id.date_picker_button);

                profilePicture=addDialog.findViewById(R.id.profile_picture);

                AnimalAppEditText name= addDialog.findViewById(R.id.nameEditText);

                TextView birthDate= addDialog.findViewById(R.id.date_text_view);

                AnimalAppEditText microChip= addDialog.findViewById(R.id.micro_chip);

                Spinner speciesSpinner= addDialog.findViewById(R.id.species_spinner);

                ArrayAdapter<CharSequence> speciesAdapter= ArrayAdapter.createFromResource(getActivity(),R.array.animal_species,
                        android.R.layout.simple_list_item_1);

                speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                speciesSpinner.setAdapter(speciesAdapter);

                Spinner raceSpinner= addDialog.findViewById(R.id.race_spinner);

                addDialog.setInputCallback(new AnimalCallbacks.inputValidate() {
                    @Override
                    public void InvalidName() {
                        name.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                        name.setError(getActivity().getString(R.string.invalid_user_name));
                    }

                    @Override
                    public void InvalidBirthDate() {
                        birthDate.setError(getActivity().getString(R.string.invalid_user_birthdate));
                    }

                    @Override
                    public void InvalidMicrochip() {
                        microChip.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                        microChip.setError(getActivity().getString(R.string.invalid_microchip));
                    }

                    @Override
                    public void MicrochipAlreadyUsed() {
                        name.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                        microChip.setError(getActivity().getString(R.string.microchip_already_exist));
                    }
                });



                addPhotoButton.setOnClickListener(v -> {
                    Intent photoIntent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    photoPickerResultLauncher.launch(photoIntent);
                });

                datePickerButton.setOnClickListener(v -> {
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getActivity(),
                            (view, year1, month1, dayOfMonth) -> {
                                birthDate.setText(dayOfMonth + "/" + (month1 +1) + "/" + year1);
                                birthDate.setError(null);
                                c.set(year1, month1,dayOfMonth);
                                dateIsSetted[0]=true;
                            }, year, month, day);

                    datePickerDialog.show();
                });

                speciesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        ArrayAdapter<CharSequence> raceAdapter;

                        switch (position){
                            case 0:
                                raceAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.dog_breeds,
                                        android.R.layout.simple_list_item_1);
                                break;
                            case 1:
                                raceAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.cat_breeds,
                                        android.R.layout.simple_list_item_1);
                                break;
                            case 2:
                                raceAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.fish_breeds,
                                        android.R.layout.simple_list_item_1);
                                break;
                            case 3:
                                raceAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.bird_breeds,
                                        android.R.layout.simple_list_item_1);
                                break;
                            case 4:
                                raceAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.rabbit_breeds,
                                        android.R.layout.simple_list_item_1);
                                break;
                            default:
                                raceAdapter=ArrayAdapter.createFromResource(getActivity(),R.array.dog_breeds,
                                        android.R.layout.simple_list_item_1);
                        }

                        raceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        raceSpinner.setAdapter(raceAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                saveButton.setOnClickListener(v -> {
                    animalPresenter.addAnimal(Animal.Builder.create("", AnimalStates.ADOPTED)
                            .setBirthDate(dateIsSetted[0]?c.getTime():null)
                            .setRace(raceSpinner.getSelectedItem().toString())
                            .setSpecies(AnimalSpecies.values()[speciesSpinner.getSelectedItemPosition()])
                            .setName(name.getText().toString())
                            .setOwner(SessionManager.getInstance().getCurrentUser().getFirebaseID())
                            .setPhoto(((BitmapDrawable)profilePicture.getDrawable()).getBitmap())
                            .setMicrochip(microChip.getText().toString())
                            .build());
                });

            }
            else{
                throw new IllegalArgumentException("This tab position is invalid for add on list in private");
            }
        }
        else if(this.profileType == ProfileFragment.Type.ANIMAL){//for animal
            RelationPresenter relationPresenter= new RelationPresenter();

            switch (this.tabPosition) {
                case RELATION:
                    addDialog.setContentView(R.layout.create_relation);
                    Spinner relationSpinner= addDialog.findViewById(R.id.relation_type);
                    String [] getAnimal = new String[1];
                    Spinner animalChooseSpinner = addDialog.findViewById(R.id.animal_chooser);
                    Button createVisit = addDialog.findViewById(R.id.save_button);
                    relationPresenter.action_getAnimalListForChooseAnimal(currentUser.getFirebaseID(), (RelationDao.OnGetListAnimalForChooseAnimal) animalList -> {
                        for (int i = 0; i < animalList.size(); i++) {
                            System.out.println("animal: "+animalList.get(i).getName());
                        }

                        ArrayList <String> animalListName = new ArrayList<>();

                        for (int i = 0; i < animalList.size(); i++) {
                            animalListName.add(animalList.get(i).getName());
                        }

                        ArrayAdapter <String> animal_chooseAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1,animalListName);
                        animal_chooseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        animalChooseSpinner.setAdapter(animal_chooseAdapter);

                        final Relation.relationType[] type = new Relation.relationType[1];
                        relationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                type[0] = Relation.relationType.valueOf(adapterView.getItemAtPosition(i).toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });

                        animalChooseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                getAnimal[0] = adapterView.getItemAtPosition(i).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        createVisit.setOnClickListener(view -> {
                            Animal chooseAnimal = null;

                            for (int i = 0; i < animalList.size(); i++) {
                                if (animalList.get(i).getName().equals(getAnimal[0])) {
                                    chooseAnimal = animalList.get(i);
                                }
                            }

                            if (chooseAnimal != null) {
                                RelationPresenter relation = new RelationPresenter();
                                relation.createRelation(animal.getFirebaseID(), type[0], chooseAnimal, new RelationDao.OnRelationCreateListener() {
                                    @Override
                                    public void onCreateSuccess(Relation relation) {
                                        addDialog.cancel();
                                        relationList.add(relation);
                                        recyclerView.setAdapter(relationAdapter);
                                    }
                                    @Override
                                    public void onCreateFailure() {
                                        Log.w("W","the relation is not created");
                                    }
                                });
                            }else{
                                Log.w(TAG,"Animal Class not found");
                            }
                        });

                        ArrayAdapter<CharSequence> relationAdapter= ArrayAdapter.createFromResource(getContext(),R.array.relation_type,
                                android.R.layout.simple_list_item_1);
                        relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        relationSpinner.setAdapter(relationAdapter);
                    });

                    break;
                case HEALTH:
                    addDialog.setContentView(R.layout.add_pathology);

                    Spinner pathologySpinner= addDialog.findViewById(R.id.pathology_type);
                    Button save = addDialog.findViewById(R.id.save_button);

                    String[] getValue = new String[1];

                    pathologySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            getValue[0] = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    save.setOnClickListener(view -> {
                        PathologyPresenter pathology = new PathologyPresenter();
                        pathology.action_create(animal.getFirebaseID(), getValue[0], new PathologyDao.OnPathologyCreateListener() {
                            @Override
                            public void onCreatedReady(Pathology pathology) {
                                addDialog.cancel();
                                animal.addPathology(pathology);
                            }

                            @Override
                            public void onFailureReady() {
                                Log.i("I","Pathology created failure");
                            }
                        });
                    });
                    ArrayAdapter<CharSequence> pathologyAdapter= ArrayAdapter.createFromResource(getActivity(),R.array.animal_pathologies,
                            android.R.layout.simple_list_item_1);
                    pathologyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pathologySpinner.setAdapter(pathologyAdapter);
                    break;
                case FOOD:
                    addDialog.setContentView(R.layout.add_food);
                    Button createFood = addDialog.findViewById(R.id.save_button);
                    AnimalAppEditText nameFood = addDialog.findViewById(R.id.nameEditText);

                    createFood.setOnClickListener(v -> {
                        Food newFood=Food.Builder.create("", nameFood.getText().toString())
                                .setAnimalID(this.animal.getFirebaseID())
                                .build();

                        boolean validName=new FoodPresenter().addFood(
                                newFood,
                                new AnimalCallbacks.creationCallback() {
                                    @Override
                                    public void createdSuccesfully() {
                                        animal.addFood(newFood);

                                        addDialog.cancel();
                                    }

                                    @Override
                                    public void failedCreation() {}
                                });

                        if(!validName) {
                            nameFood.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                            nameFood.setError("nome invalido");
                        }

                    });

                    break;
                case EXPENSE:
                    addDialog.setContentView(R.layout.create_expense);
                    Spinner expenseSpinner= addDialog.findViewById(R.id.expense_type);
                    ArrayAdapter<CharSequence> expenseAdapter= ArrayAdapter.createFromResource(getActivity(),R.array.expense_categories,
                            android.R.layout.simple_list_item_1);
                    expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    expenseSpinner.setAdapter(expenseAdapter);

                    AnimalAppEditText expense = addDialog.findViewById(R.id.expense);
                    AnimalAppEditText note = addDialog.findViewById(R.id.note);
                    Button createExpense = addDialog.findViewById(R.id.save_button);

                    Expense.expenseType[] expenseType= {Expense.expenseType.ACCESSORY};
                    expenseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            expenseType[0] = Expense.expenseType.values()[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    createExpense.setOnClickListener(v -> {

                        String expenseValue=expense.getText().toString();

                        if(expenseValue.isEmpty()){
                            expense.setInputValidate(AnimalAppEditText.ValidateInput.INVALID_INPUT);
                            expense.setError("inserisci prezzo");
                        }
                        else{
                            Expense newExpense = Expense.Builder.create("", Double.valueOf(expenseValue))
                                    .setAnimalID(this.animal.getFirebaseID())
                                    .setNote(note.getText().toString())
                                    .setCategory(expenseType[0])
                                    .build();

                            new ExpensePresenter().addExpense(
                                    newExpense,
                                    new AnimalCallbacks.creationCallback() {
                                        @Override
                                        public void createdSuccesfully() {
                                            animal.addExpense(newExpense);

                                            addDialog.cancel();
                                        }

                                        @Override
                                        public void failedCreation() {
                                        }
                                    });
                        }
                    });
                    break;
            }
        }


        Button backButton= addDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> addDialog.cancel());

        addDialog.show();
        addDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        addDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        addDialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void setAnimalList(){
        ArrayList<Animal> animalList;
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,0);

        animalPresenter= new AnimalPresenter();

        animalList=this.profileType!= ProfileFragment.Type.VETERINARIAN?
                ((Owner) this.profile).getAnimalList()
                :((Veterinarian) this.profile).getAnimalList();

        if(profileType == ProfileFragment.Type.VETERINARIAN){
            addButton.setVisibility(View.GONE);
        } else if (SessionManager.getInstance().getCurrentUser().getFirebaseID().compareTo(this.profile.getFirebaseID()) != 0) {
            addButton.setVisibility(View.GONE);
        } else{
            addButton.setOnClickListener(v -> launchAddDialog());
        }


        this.photoPickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData()!=null) {
                        Uri selectedImage = result.getData().getData();
                        profilePicture.setImageURI(selectedImage);
                    }
                });



        AnimalAdapter adapter=new AnimalAdapter(animalList,getContext());

        adapter.setOnAnimalClickListener(this::openAnimalPage);

        this.profile.setUserLoadingCallBack(new UserCallback.UserStateListener() {
            @Override
            public void notifyItemLoaded() {
                adapter.notifyItemInserted(0);
            }

            @Override
            public void notifyItemUpdated(int position) {adapter.notifyItemChanged(position);}

            @Override
            public void notifyItemRemoved(int position) {
                adapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void setVisitList(){
        ArrayList<Visit> visitList;
        VisitAdapter visitAdapter;
        int[] deleteIndexForCompleteList ={0};

        if(this.profileType == ProfileFragment.Type.ANIMAL){
            visitList=this.animal.getVisits();
            visitAdapter=new VisitAdapter(visitList,getContext());


            this.animal.setVisitCallback(new AnimalCallbacks.visitCallback() {
                @Override
                public void notifyVisitLoaded() {
                    visitAdapter.notifyItemInserted(0);
                }

                @Override
                public void notifyVisitRemoved(int position) {
                    visitAdapter.notifyItemRemoved(position);
                }
            });
        }
        else if(this.profileType == ProfileFragment.Type.VETERINARIAN){
            Veterinarian veterinarian=((Veterinarian)this.profile);

            visitList=veterinarian.getVisitList();

            visitAdapter=new VisitAdapter(visitList,getContext());
            veterinarian.setVisitCallback(new AnimalCallbacks.visitCallback() {
                @Override
                public void notifyVisitLoaded() {
                    visitAdapter.notifyItemInserted(0);
                }

                @Override
                public void notifyVisitRemoved(int position) {
                    visitAdapter.notifyItemRemoved(position);
                }
            });
        }
        else{
            visitList=new ArrayList<>();
            visitAdapter=new VisitAdapter(visitList,getContext());
            for (Animal animal : ((Owner) this.profile).getAnimalList()) {
                visitList.addAll(animal.getVisits());

                animal.setVisitCallback(new AnimalCallbacks.visitCallback() {
                    @Override
                    public void notifyVisitLoaded() {
                        visitAdapter.notifyItemInserted(0);
                    }

                    @Override
                    public void notifyVisitRemoved(int position) {
                        visitAdapter.notifyItemRemoved(deleteIndexForCompleteList[0]);
                    }
                });
            }

        }

        visitAdapter.setOnVisitClickListener(this::openVisitPage);

        recyclerView.setAdapter(visitAdapter);

        addButton.setVisibility(View.GONE);


        if(this.profileType == ProfileFragment.Type.ANIMAL && (!AnimalPresenter.checkAnimalProperty(this.animal)))
            return;

        if(this.profileType != ProfileFragment.Type.ANIMAL && (SessionManager.getInstance().getCurrentUser().getFirebaseID().compareTo(this.profile.getFirebaseID()) != 0))
            return;

        SwipeHelper VisitSwipeHelper = new SwipeHelper(getActivity(), recyclerView) {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            AnimalAppDialog.launchConfirmDialog(() -> {
                                VisitPresenter visit = new VisitPresenter();

                                Visit deleteVisit=visitList.get(pos);

                                if (visit.removeVisit(deleteVisit)) {
                                    if(profileType == ProfileFragment.Type.ANIMAL){
                                        animal.removeVisit(deleteVisit);
                                    }
                                    else if(profileType == ProfileFragment.Type.VETERINARIAN){
                                        ((Veterinarian)profile).removeVisit(deleteVisit);
                                    }
                                    else {
                                        for (Animal animal : ((Owner) SessionManager.getInstance().getCurrentUser()).getAnimalList()) {
                                            if(animal.getFirebaseID().compareTo(deleteVisit.getAnimal().getFirebaseID())==0){
                                                deleteIndexForCompleteList[0]=pos;
                                                animal.removeVisit(deleteVisit);
                                                visitList.remove(deleteVisit);
                                                break;
                                            }
                                        }
                                    }
                                }


                                return null;
                            },getContext());

                        }
                ));
            }
        };

    }

    private void setExpenseList(){
        ArrayList<Expense> expensesList;

        ExpenseAdapter expenseAdapter;

        int[] deleteIndexForCompleteList ={0};

        if(profileType != ProfileFragment.Type.ANIMAL) {
            addButton.setVisibility(View.GONE);

            expensesList=new ArrayList<>();

            expenseAdapter=new ExpenseAdapter(expensesList,getContext());

            for (Animal animal : ((Owner) this.profile).getAnimalList()) {
                expensesList.addAll(animal.getExpenses());

                animal.setExpensesCallback(new AnimalCallbacks.expensesCallback() {
                    @Override
                    public void notifyExpensesLoaded() {
                        expenseAdapter.notifyItemInserted(0);
                    }

                    @Override
                    public void notifyExpensesRemoved(int position) {
                        expenseAdapter.notifyItemRemoved(deleteIndexForCompleteList[0]);
                    }
                });
            }
        }
        else{
            expensesList=this.animal.getExpenses();

            expenseAdapter=new ExpenseAdapter(expensesList,getContext());

            addButton.setOnClickListener(v -> launchAddDialog());

            this.animal.setExpensesCallback(new AnimalCallbacks.expensesCallback() {
                @Override
                public void notifyExpensesLoaded() {
                    expenseAdapter.notifyItemInserted(0);
                }

                @Override
                public void notifyExpensesRemoved(int position) {
                    expenseAdapter.notifyItemRemoved(position);
                }
            });
        }

        if((profileType == ProfileFragment.Type.ANIMAL) && (SessionManager.getInstance().getCurrentUser().getRole() == UserRole.VETERINARIAN)) {
            addButton.setVisibility(View.GONE);
        }

        recyclerView.setAdapter(expenseAdapter);

        if(this.profileType == ProfileFragment.Type.ANIMAL && (!AnimalPresenter.checkAnimalProperty(this.animal)))
            return;

        if(this.profileType != ProfileFragment.Type.ANIMAL && (SessionManager.getInstance().getCurrentUser().getFirebaseID().compareTo(this.profile.getFirebaseID()) != 0))
            return;

        SwipeHelper ExpenseSwipeHelper = new SwipeHelper(getActivity(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            AnimalAppDialog.launchConfirmDialog(() -> {
                                Expense expense= expensesList.get(pos);
                                expense.delete(new AnimalCallbacks.eliminationCallback() {
                                    @Override
                                    public void eliminatedSuccesfully() {
                                        if(profileType != ProfileFragment.Type.ANIMAL) {
                                            for (Animal animal : ((Owner) SessionManager.getInstance().getCurrentUser()).getAnimalList()) {
                                                if(animal.getFirebaseID().compareTo(expense.getAnimalID())==0){
                                                    deleteIndexForCompleteList[0]=pos;
                                                    animal.removeExpense(expense);
                                                    expensesList.remove(deleteIndexForCompleteList[0]);
                                                    break;
                                                }
                                            }
                                        }
                                        else{
                                            animal.removeExpense(expense);
                                        }
                                    }

                                    @Override
                                    public void failedElimination() {

                                    }
                                });

                                return null;
                            }, getContext());
                        }
                ));
            }
        };
    }

    private ArrayList<Relation> relationList = new ArrayList<>();
    private RelationAdapter relationAdapter;

    public void setRelationList(){
        RelationPresenter presenter = new RelationPresenter();
        presenter.action_getRelation(animal.getFirebaseID(), relationListGet -> {
            relationList=new ArrayList<>();
            relationList.addAll(relationListGet);
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH,-1600);
            relationAdapter=new RelationAdapter(relationList,getContext());
            addButton.setOnClickListener(v -> launchAddDialog());

            recyclerView.setAdapter(relationAdapter);
        });

        if((profileType == ProfileFragment.Type.ANIMAL) && (SessionManager.getInstance().getCurrentUser().getRole() == UserRole.VETERINARIAN)) {
            addButton.setVisibility(View.GONE);
        }

        if(!AnimalPresenter.checkAnimalProperty(this.animal))
            return;

        SwipeHelper RelationSwipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            AnimalAppDialog.launchConfirmDialog(() -> {
                                RelationPresenter relation = new RelationPresenter();
                                Relation relationClass = relationAdapter.getRelationList().get(pos);

                                relation.deleteRelation(relationClass);
                                relationAdapter.removeRelation(pos);
                                System.out.println("delete the relation");

                                return null;
                            },getContext());
                        }
                ));
            }
        };

    }


    public void setHealtList(){
        SimpleTextAdapter<Pathology> pathologyAdapter;
        ArrayList<Pathology> pathologyList=this.animal.getPathologies();

        pathologyAdapter=new SimpleTextAdapter<>(pathologyList);

        addButton.setOnClickListener(v -> launchAddDialog() );

        if((profileType == ProfileFragment.Type.ANIMAL) && (SessionManager.getInstance().getCurrentUser().getRole() == UserRole.VETERINARIAN)) {
            addButton.setVisibility(View.GONE);
        }

        this.animal.setPathologyCallback(new AnimalCallbacks.pathologyCallback() {
            @Override
            public void notifyPathologyLoaded() {
                pathologyAdapter.notifyItemInserted(0);
            }

            @Override
            public void notifyPathologyRemoved(int position) {
                pathologyAdapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setAdapter(pathologyAdapter);

        if(!AnimalPresenter.checkAnimalProperty(this.animal))
            return;

        SwipeHelper PathologySwipeHelper = new SwipeHelper(getActivity(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            AnimalAppDialog.launchConfirmDialog(() -> {
                                Pathology deletePathology=pathologyList.get(pos);
                                deletePathology.delete(new AnimalCallbacks.eliminationCallback() {
                                    @Override
                                    public void eliminatedSuccesfully() {
                                        animal.removePathology(deletePathology);
                                    }

                                    @Override
                                    public void failedElimination() {

                                    }
                                });
                                return null;
                            },getContext());
                        }
                ));
            }
        };

    }


    public void setFoodList(){
        ArrayList<Food> foodList=this.animal.getFoods();

        SimpleTextAdapter<Food> foodAdapter=new SimpleTextAdapter<>(foodList);

        addButton.setOnClickListener(v -> launchAddDialog());

        if((profileType == ProfileFragment.Type.ANIMAL) && (SessionManager.getInstance().getCurrentUser().getRole() == UserRole.VETERINARIAN)) {
            addButton.setVisibility(View.GONE);
        }

        this.animal.setFoodCallback(new AnimalCallbacks.foodCallback() {
            @Override
            public void notifyFoodLoaded() {
                foodAdapter.notifyItemInserted(0);
            }

            @Override
            public void notifyFoodRemoved(int position) {
                foodAdapter.notifyItemRemoved(position);
            }
        });

        recyclerView.setAdapter(foodAdapter);


        if(!AnimalPresenter.checkAnimalProperty(this.animal))
            return;

        SwipeHelper FoodSwipeHelper = new SwipeHelper(getActivity(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            AnimalAppDialog.launchConfirmDialog(() -> {

                                Food food=foodList.get(pos);
                                food.delete(new AnimalCallbacks.eliminationCallback() {
                                    @Override
                                    public void eliminatedSuccesfully() {
                                        animal.removeFood(food);
                                    }

                                    @Override
                                    public void failedElimination() {

                                    }
                                });
                                return null;
                            },getContext());
                        }
                ));
            }
        };
    }


    private void openAnimalPage(Animal animal){
        FragmentManager fragmentManager=getParentFragmentManager();

        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.frame_for_fragment, AnimalFragment.newInstance(animal),"animalPage").commit();
    }

    private void openVisitPage(Visit visit){
        FragmentManager fragmentManager=getParentFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.frame_for_fragment,VisitFragment.newInstance(visit),"visitPage").commit();
    }
}
