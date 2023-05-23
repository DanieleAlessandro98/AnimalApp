package it.uniba.dib.sms222334.Fragmets;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.Spinner;

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

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Food;
import it.uniba.dib.sms222334.Models.Pathology;
import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.RecycleViews.Animal.AnimalAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.Expences.ExpenseAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.Relation.RelationAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.SimpleText.SimpleTextAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.SwipeHelper;
import it.uniba.dib.sms222334.Views.RecycleViews.Visit.VisitAdapter;

public class ListFragment extends Fragment {

    final static String TAG = "ListFragment";

    ImageButton addButton;

    RecyclerView recyclerView;


    ProfileFragment.Tab tabPosition;
    ProfileFragment.Type profileType;

    public ListFragment(ProfileFragment.Tab tabPosition,ProfileFragment.Type profileType) {

        this.tabPosition = tabPosition;
        this.profileType = profileType;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.item_list_container, container, false);

        addButton=layout.findViewById(R.id.add_button);
        recyclerView=layout.findViewById(R.id.list_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new ItemDecorator(0));

        Animal a1=Animal.Builder.create(Animal.stateList.ADOPTED)
                .setAge(3)
                .setSpecies("Dog")
                .setName("Alberto")
                .build();

        switch (this.tabPosition.tabPosition){
            case ANIMAL:
                if(profileType == ProfileFragment.Type.VETERINARIAN){
                    addButton.setVisibility(View.GONE);
                }
                else{
                    addButton.setOnClickListener(v -> launchAddDialog() );
                }

                ArrayList<Animal> animalList=new ArrayList<>();



                animalList.add(a1);
                animalList.add(a1);
                animalList.add(a1);
                animalList.add(a1);
                animalList.add(a1);


                AnimalAdapter adapter=new AnimalAdapter(animalList,getContext());
                adapter.setOnAnimalClickListener(animal -> {
                    //TODO launch AnimalFragment
                    FragmentManager fragmentManager=getParentFragmentManager();

                    FragmentTransaction transaction= fragmentManager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.frame_for_fragment,new AnimalFragment(animal)).commit();
                    tabPosition.tabPosition=null;
                });
                recyclerView.setAdapter(adapter);
                
                break;
            case VISIT:
                addButton.setVisibility(View.GONE);

                ArrayList<Visit> visitList=new ArrayList<>();
                Calendar calendar=Calendar.getInstance();

                Visit v1=Visit.Builder.create("Test", Visit.visitType.CONTROL, calendar.getTime())
                                .setState(Visit.visitState.NOT_EXECUTED)
                                .setAnimal(a1)
                                .build();

                visitList.add(v1);
                visitList.add(v1);
                visitList.add(v1);
                visitList.add(v1);
                visitList.add(v1);
                
                VisitAdapter visitAdapter=new VisitAdapter(visitList,getContext());

                visitAdapter.setOnVisitClickListener(visit -> {
                    FragmentManager fragmentManager=getParentFragmentManager();

                    FragmentTransaction transaction= fragmentManager.beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.replace(R.id.frame_for_fragment,new VisitFragment(visit,profileType)).commit();
                });

                SwipeHelper VisitSwipeHelper = new SwipeHelper(getContext(), recyclerView) {
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
                break;
            case EXPENSE:
                Log.d(TAG,tabPosition+"");
                if(profileType != ProfileFragment.Type.ANIMAL)
                    addButton.setVisibility(View.GONE);
                else
                    addButton.setOnClickListener(v -> launchAddDialog() );
                
                ArrayList<Expense> expenseList=new ArrayList<>();

                Expense e1=Expense.Builder.create(14.94)
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
                break;
            case RELATION:
                addButton.setOnClickListener(v -> launchAddDialog() );
                ArrayList<Relation> relationList=new ArrayList<>();

                Relation r1=Relation.Builder.create(Relation.relationType.INCOMPATIBLE,a1).build();

                relationList.add(r1);
                relationList.add(r1);
                relationList.add(r1);
                relationList.add(r1);
                relationList.add(r1);


                RelationAdapter relationAdapter=new RelationAdapter(relationList,getContext());

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
                                                relationAdapter.removeRelation(pos);
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

                recyclerView.setAdapter(relationAdapter);
                break;
            case HEALTH:
                addButton.setOnClickListener(v -> launchAddDialog() );

                ArrayList<Pathology> pathologyList=new ArrayList<>();

                Pathology p1=Pathology.Builder.create(a1,"Scogliosi").build();
                pathologyList.add(p1);
                pathologyList.add(p1);
                pathologyList.add(p1);
                pathologyList.add(p1);
                pathologyList.add(p1);


                SimpleTextAdapter<Pathology> pathologyAdapter=new SimpleTextAdapter<>(pathologyList);

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
                                                pathologyAdapter.removeSimpleElement(pos);
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

                recyclerView.setAdapter(pathologyAdapter);
                break;
            case FOOD:
                addButton.setOnClickListener(v -> launchAddDialog() );

                ArrayList<Food> foodList=new ArrayList<>();

                Food f1=Food.Builder.create("Riso patate e cozze",a1).build();
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
                break;
        }

        return layout;
    }

    private void launchAddDialog() {

        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //is is a owner profile it can add animal, veterinarian can't add anything
        if((this.profileType == ProfileFragment.Type.PUBLIC_AUTHORITY) || (this.profileType == ProfileFragment.Type.PRIVATE) ){
            if(this.tabPosition.tabPosition== ProfileFragment.TabPosition.ANIMAL){
                    editDialog.setContentView(R.layout.add_animal);

                    Spinner speciesSpinner= editDialog.findViewById(R.id.species_spinner);
                    ArrayAdapter<CharSequence> speciesAdapter= ArrayAdapter.createFromResource(getContext(),R.array.animal_species,
                            android.R.layout.simple_list_item_1);
                    speciesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    speciesSpinner.setAdapter(speciesAdapter);
                    Spinner raceSpinner= editDialog.findViewById(R.id.race_spinner);

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
            }
            else{
                throw new IllegalArgumentException("This tab position is invalid for add on list in private");
            }
        }
        else if(this.profileType == ProfileFragment.Type.ANIMAL){//for animal
            switch (this.tabPosition.tabPosition) {
                case RELATION:
                    editDialog.setContentView(R.layout.create_relation);

                    Spinner relationSpinner= editDialog.findViewById(R.id.relation_type);
                    ArrayAdapter<CharSequence> relationAdapter= ArrayAdapter.createFromResource(getContext(),R.array.relation_type,
                            android.R.layout.simple_list_item_1);
                    relationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    relationSpinner.setAdapter(relationAdapter);
                    break;
                case HEALTH:
                    editDialog.setContentView(R.layout.add_pathology);
                    Spinner pathologySpinner= editDialog.findViewById(R.id.pathology_type);
                    ArrayAdapter<CharSequence> pathologyAdapter= ArrayAdapter.createFromResource(getContext(),R.array.animal_pathologies,
                            android.R.layout.simple_list_item_1);
                    pathologyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pathologySpinner.setAdapter(pathologyAdapter);
                    break;
                case FOOD:
                    editDialog.setContentView(R.layout.add_food);
                    break;
                case EXPENSE:
                    editDialog.setContentView(R.layout.create_expense);
                    Spinner expenseSpinner= editDialog.findViewById(R.id.expense_type);
                    ArrayAdapter<CharSequence> expenseAdapter= ArrayAdapter.createFromResource(getContext(),R.array.expense_categories,
                            android.R.layout.simple_list_item_1);
                    expenseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    expenseSpinner.setAdapter(expenseAdapter);
                    break;
            }
        }


        Button backButton= editDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> editDialog.cancel());

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
