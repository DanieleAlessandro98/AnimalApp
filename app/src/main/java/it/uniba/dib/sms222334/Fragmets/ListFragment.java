package it.uniba.dib.sms222334.Fragmets;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Food;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Pathology;
import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.Presenters.AnimalPresenter;
import it.uniba.dib.sms222334.Presenters.PathologyPresenter;
import it.uniba.dib.sms222334.Presenters.RelationPresenter;
import it.uniba.dib.sms222334.Presenters.VisitPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;
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

    User currentUser; //profile of the user logged
    UserRole currentUserRole;
    static Animal animal;

    private ActivityResultLauncher<Intent> photoPickerResultLauncher;

    public ListFragment() {
        currentUser=SessionManager.getInstance().getCurrentUser();

        currentUserRole=currentUser.getRole();
    }

    public static ListFragment newInstance(ProfileFragment.Tab tabPosition,ProfileFragment.Type profileType) {
        ListFragment myFragment = new ListFragment();
        animal = AnimalFragment.animal; // Non cancellare questa riga, viene usato nella patologia dell'animale
        Bundle args = new Bundle();
        args.putInt("tab_position", tabPosition.tabPosition.ordinal());
        args.putInt("profile_type", profileType.ordinal());
        myFragment.setArguments(args);

        return myFragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.item_list_container, container, false);

        this.tabPosition = ProfileFragment.TabPosition.values()[getArguments().getInt("tab_position")];
        this.profileType = ProfileFragment.Type.values()[getArguments().getInt("profile_type")];

        getArguments().clear();

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

    public enum relationType{FRIEND,INCOMPATIBLE,COHABITEE}

    private void launchAddDialog() {

        final AnimalAppDialog addDialog=new AnimalAppDialog(getContext());
        addDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

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

                    TextView name= addDialog.findViewById(R.id.nameEditText);

                    TextView birthDate= addDialog.findViewById(R.id.date_text_view);

                    TextView microChip= addDialog.findViewById(R.id.micro_chip);

                    Spinner speciesSpinner= addDialog.findViewById(R.id.species_spinner);

                    ArrayAdapter<CharSequence> speciesAdapter= ArrayAdapter.createFromResource(getContext(),R.array.animal_species,
                            android.R.layout.simple_list_item_1);

                    speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    speciesSpinner.setAdapter(speciesAdapter);

                    Spinner raceSpinner= addDialog.findViewById(R.id.race_spinner);

                    addDialog.setInputCallback(new AnimalCallbacks.inputValidate() {
                        @Override
                        public void InvalidName() {
                            name.setError("nome non valido");
                        }

                        @Override
                        public void InvalidBirthDate() {
                            birthDate.setError("data non valida");
                        }

                        @Override
                        public void InvalidMicrochip() {
                            microChip.setError("microchip non valido");
                        }

                        @Override
                        public void MicrochipAlreadyUsed() {
                            microChip.setError("microchip esiste già");
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
                                getContext(),
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
                                    raceAdapter=ArrayAdapter.createFromResource(getContext(),R.array.dog_breeds,
                                            android.R.layout.simple_list_item_1);
                                    break;
                                case 1:
                                    raceAdapter=ArrayAdapter.createFromResource(getContext(),R.array.cat_breeds,
                                            android.R.layout.simple_list_item_1);
                                    break;
                                case 2:
                                    raceAdapter=ArrayAdapter.createFromResource(getContext(),R.array.fish_breeds,
                                            android.R.layout.simple_list_item_1);
                                    break;
                                case 3:
                                    raceAdapter=ArrayAdapter.createFromResource(getContext(),R.array.bird_breeds,
                                            android.R.layout.simple_list_item_1);
                                    break;
                                case 4:
                                    raceAdapter=ArrayAdapter.createFromResource(getContext(),R.array.rabbit_breeds,
                                            android.R.layout.simple_list_item_1);
                                    break;
                                default:
                                    raceAdapter=ArrayAdapter.createFromResource(getContext(),R.array.dog_breeds,
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
                        animalPresenter.addAnimal(Animal.Builder.create("", Animal.stateList.ADOPTED)
                            .setBirthDate(dateIsSetted[0]?c.getTime():null)
                            .setRace(raceSpinner.getSelectedItem().toString())
                            .setSpecies(speciesSpinner.getSelectedItem().toString())
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
            switch (this.tabPosition) {
                case RELATION:
                    addDialog.setContentView(R.layout.create_relation);

                    Spinner relationSpinner= addDialog.findViewById(R.id.relation_type);
                    // TODO ricambiare in xml textview in spinner di animalchoose
                    TextView animalChooseSpinner = addDialog.findViewById(R.id.animal_chooser);
                    Button createVisit = addDialog.findViewById(R.id.save_button);


                    String [] getAnimal = new String[1];
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

                    getAnimal[0] = animalChooseSpinner.getText().toString();

                    /*animalChooseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            getAnimal[0] = adapterView.getItemAtPosition(i).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });*/

                    createVisit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RelationPresenter relation = new RelationPresenter();
                            Relation relationValue = relation.createRelation(animal.getFirebaseID(), type[0],animal);
                            if(relationValue != null) {
                                addDialog.cancel();
                                relationList.add(relationValue);
                                recyclerView.setAdapter(relationAdapter);
                            }else{
                                System.out.println("there are a error");
                            }
                        }
                    });

                    ArrayAdapter<CharSequence> relationAdapter= ArrayAdapter.createFromResource(getContext(),R.array.relation_type,
                            android.R.layout.simple_list_item_1);
                    relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    relationSpinner.setAdapter(relationAdapter);
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
                    /**
                     * ho creato e inizializzato la classe present, poi ho chiamato il metodo del present
                     * per creare una patologia e ritorna la classe patologia del model se la creazione è
                     * andata a buon fine, infine ho cancellato il dialogo e ho aggiunto la classe patologia
                     * alla lista delle patologie con inseguito un aggiornameto della view.
                     */
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PathologyPresenter pathology = new PathologyPresenter();
                            Pathology pathologyValue = pathology.action_create(animal.getFirebaseID(),getValue[0]);
                            if (pathologyValue != null) {
                                addDialog.cancel();

                                pathologyList.add(pathologyValue);
                                recyclerView.setAdapter(pathologyAdapter);
                            }
                        }
                    });
                    ArrayAdapter<CharSequence> pathologyAdapter= ArrayAdapter.createFromResource(getContext(),R.array.animal_pathologies,
                            android.R.layout.simple_list_item_1);
                    pathologyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pathologySpinner.setAdapter(pathologyAdapter);
                    break;
                case FOOD:
                    addDialog.setContentView(R.layout.add_food);
                    break;
                case EXPENSE:
                    addDialog.setContentView(R.layout.create_expense);
                    Spinner expenseSpinner= addDialog.findViewById(R.id.expense_type);
                    ArrayAdapter<CharSequence> expenseAdapter= ArrayAdapter.createFromResource(getContext(),R.array.expense_categories,
                            android.R.layout.simple_list_item_1);
                    expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    expenseSpinner.setAdapter(expenseAdapter);
                    break;
            }
        }


        Button backButton= addDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> addDialog.cancel());

        addDialog.show();
        addDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        addDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public static ArrayList<Animal> animalList;

    public void setAnimalList(){
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,0);

        animalPresenter= new AnimalPresenter();

        animalList=this.currentUserRole!=UserRole.VETERINARIAN?((Owner) this.currentUser).getAnimalList():new ArrayList<>();

        if(profileType == ProfileFragment.Type.VETERINARIAN){
            addButton.setVisibility(View.GONE);
        }
        else{
            addButton.setOnClickListener(v -> launchAddDialog() );
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

        adapter.setOnAnimalClickListener(animal -> {
            FragmentManager fragmentManager=getParentFragmentManager();

            ArrayList<Pathology> list = PathologyPresenter.action_getPathology(animal.getFirebaseID());
            for (int i=0; i<animalList.size();i++){
                System.out.println("esterno    "+animalList.get(i).getName());
            }
            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.addToBackStack("itemPage");
            transaction.replace(R.id.frame_for_fragment, AnimalFragment.newInstance(animal,getContext())).commit();
            tabPosition=null;
        });


        this.currentUser.setUserLoadingCallBack(new UserCallback.UserStateListener() {
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

    public static VisitAdapter visitAdapter;
    public static ArrayList<Visit> visitList = new ArrayList<>();

    public void setVisitList(){
        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-1600);

        Animal a1=Animal.Builder.create("testID", Animal.stateList.ADOPTED)
                .setSpecies("Dog")
                .setBirthDate(c.getTime())
                .setName("Alberto")
                .build();

        addButton.setVisibility(View.GONE);

        Calendar calendar=Calendar.getInstance();

        Visit v1=Visit.Builder.create("TestID", "Test", Visit.visitType.CONTROL, calendar.getTime())
                .setState(Visit.visitState.NOT_EXECUTED)
                .setAnimal(a1)
                .build();

        visitList.add(v1);
        visitList.add(v1);
        visitList.add(v1);
        visitList.add(v1);
        visitList.add(v1);

        System.out.println("per elimina visita");

        visitAdapter=new VisitAdapter(visitList,getContext());

        visitAdapter.setOnVisitClickListener(visit -> {
            FragmentManager fragmentManager=getParentFragmentManager();

            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.addToBackStack("itemPage");
            transaction.replace(R.id.frame_for_fragment,VisitFragment.newInstance(visit)).commit();
        });

        SwipeHelper VisitSwipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            final Dialog deleteDialog=new Dialog(getContext());
                            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            deleteDialog.setContentView(R.layout.delete_dialog);

                            Button undoButton,confirmButton;

                            undoButton=deleteDialog.findViewById(R.id.undo_button);
                            confirmButton=deleteDialog.findViewById(R.id.delete_button);

                            undoButton.setOnClickListener(v -> deleteDialog.cancel());

                            confirmButton.setOnClickListener(v -> {
                                VisitPresenter visit = new VisitPresenter();
                                    if(visit.removeVisit(visitAdapter.getVisitList().get(pos).getFirebaseID()))
                                        visitAdapter.removeVisit(pos);
                                        deleteDialog.cancel();
                                    }
                            );


                            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            deleteDialog.show();
                        }
                ));
            }
        };


        recyclerView.setAdapter(visitAdapter);
    }

    public void setExpenseList(){
        Log.d(TAG,tabPosition+"");
        if(profileType != ProfileFragment.Type.ANIMAL)
            addButton.setVisibility(View.GONE);
        else
            addButton.setOnClickListener(v -> launchAddDialog() );

        ArrayList<Expense> expenseList=new ArrayList<>();

        Expense e1=Expense.Builder.create("TestID", 14.94)
                .setCategory(Expense.expenseType.ACCESSORY)
                .setnote("Tennis ball").build();

        expenseList.add(e1);
        expenseList.add(e1);
        expenseList.add(e1);
        expenseList.add(e1);
        expenseList.add(e1);


        ExpenseAdapter expenseAdapter=new ExpenseAdapter(expenseList,getContext());

        SwipeHelper ExpenseSwipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            final Dialog deleteDialog=new Dialog(getContext());
                            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            deleteDialog.setContentView(R.layout.delete_dialog);

                            Button undoButton,confirmButton;

                            undoButton=deleteDialog.findViewById(R.id.undo_button);
                            confirmButton=deleteDialog.findViewById(R.id.delete_button);

                            undoButton.setOnClickListener(v -> deleteDialog.cancel());

                            confirmButton.setOnClickListener(v -> {
                                        expenseAdapter.removeExpense(pos);
                                        deleteDialog.cancel();
                                    }
                            );


                            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            deleteDialog.show();
                        }
                ));
            }
        };

        recyclerView.setAdapter(expenseAdapter);
    }

    private ArrayList<Relation> relationList;
    private RelationAdapter relationAdapter;

    public void setRelationList(){

        relationList=new ArrayList<>();

        final Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-1600);

        Animal a1=Animal.Builder.create("testID", Animal.stateList.ADOPTED)
                .setSpecies("Dog")
                .setBirthDate(c.getTime())
                .setName("Alberto")
                .build();

        Relation r1=Relation.Builder.create("ueue",Relation.relationType.INCOMPATIBLE,a1).build();

        relationList.add(r1);
        relationList.add(r1);
        relationList.add(r1);
        relationList.add(r1);
        relationList.add(r1);

        relationAdapter=new RelationAdapter(relationList,getContext());

        addButton.setOnClickListener(v -> launchAddDialog() );

        SwipeHelper RelationSwipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            final Dialog deleteDialog=new Dialog(getContext());
                            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            deleteDialog.setContentView(R.layout.delete_dialog);

                            Button undoButton,confirmButton;

                            undoButton=deleteDialog.findViewById(R.id.undo_button);
                            confirmButton=deleteDialog.findViewById(R.id.delete_button);

                            undoButton.setOnClickListener(v -> deleteDialog.cancel());

                            confirmButton.setOnClickListener(v -> {
                                    RelationPresenter relation = new RelationPresenter();
                                    if (relation.deleteRelation(relationAdapter.getRelationList().get(pos).getFirebaseID())) {
                                        relationAdapter.removeRelation(pos);
                                        deleteDialog.cancel();
                                        System.out.println("eliminato");
                                    }
                                }
                            );


                            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            deleteDialog.show();
                        }
                ));
            }
        };

        recyclerView.setAdapter(relationAdapter);
    }
    private SimpleTextAdapter<Pathology> pathologyAdapter;
    private ArrayList<Pathology> pathologyList;

    public void setHealtList(){

        System.out.println("dentro setHealtList()");
        pathologyList=new ArrayList<>();



        Pathology p1=Pathology.Builder.create("TestID", "Scogliosi").build();
        /*pathologyList.add(p1);
        pathologyList.add(p1);
        pathologyList.add(p1);
        pathologyList.add(p1);
        pathologyList.add(p1);*/
        pathologyAdapter=new SimpleTextAdapter<>(pathologyList);

        addButton.setOnClickListener(v -> launchAddDialog() );

        SwipeHelper PathologySwipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            final Dialog deleteDialog=new Dialog(getContext());
                            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            deleteDialog.setContentView(R.layout.delete_dialog);

                            Button undoButton,confirmButton;

                            undoButton=deleteDialog.findViewById(R.id.undo_button);
                            confirmButton=deleteDialog.findViewById(R.id.delete_button);

                            undoButton.setOnClickListener(v -> deleteDialog.cancel());

                            confirmButton.setOnClickListener(v -> {
                                    PathologyPresenter pathology = new PathologyPresenter();
                                    if (pathology.action_delete(pathologyAdapter.simpleItemList.get(pos).getFirebaseID(),pathologyAdapter.simpleItemList.get(pos).getName())) {
                                        pathologyAdapter.removeSimpleElement(pos);
                                        deleteDialog.cancel();
                                    }
                                }
                            );


                            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            deleteDialog.show();
                        }
                ));
            }
        };

        recyclerView.setAdapter(pathologyAdapter);

    }

    public void setFoodList(){
        addButton.setOnClickListener(v -> launchAddDialog() );

        ArrayList<Food> foodList=new ArrayList<>();


        Food f1=Food.Builder.create("TestID", "Riso patate e cozze").build();
        foodList.add(f1);
        foodList.add(f1);
        foodList.add(f1);
        foodList.add(f1);
        foodList.add(f1);


        SimpleTextAdapter<Food> foodAdapter=new SimpleTextAdapter<>(foodList);

        SwipeHelper FoodSwipeHelper = new SwipeHelper(getContext(), recyclerView) {
            @Override
            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
                underlayButtons.add(new SwipeHelper.UnderlayButton(
                        getResources().getString(R.string.delete),
                        0,
                        Color.parseColor("#CD4C51"),
                        pos -> {
                            final Dialog deleteDialog=new Dialog(getContext());
                            deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            deleteDialog.setContentView(R.layout.delete_dialog);

                            Button undoButton,confirmButton;

                            undoButton=deleteDialog.findViewById(R.id.undo_button);
                            confirmButton=deleteDialog.findViewById(R.id.delete_button);

                            undoButton.setOnClickListener(v -> deleteDialog.cancel());

                            confirmButton.setOnClickListener(v -> {
                                        foodAdapter.removeSimpleElement(pos);
                                        deleteDialog.cancel();
                                    }
                            );


                            deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            deleteDialog.show();
                        }
                ));
            }
        };

        recyclerView.setAdapter(foodAdapter);
    }
}
