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

import it.uniba.dib.sms222334.R;

public class ListFragment extends Fragment {

    final static String TAG = "ListFragment";

    ImageButton addButton;


    ProfileFragment.TabPosition tabPosition;
    ProfileFragment.Type profileType;

    public ListFragment(ProfileFragment.TabPosition tabPosition,ProfileFragment.Type profileType) {

        this.tabPosition = tabPosition;
        this.profileType = profileType;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.item_list_container, container, false);

        addButton=layout.findViewById(R.id.add_button);

        switch (this.tabPosition){
            case ANIMAL:
                if(profileType == ProfileFragment.Type.VETERINARIAN){
                    addButton.setVisibility(View.INVISIBLE);
                }
                else{
                    addButton.setOnClickListener(v -> launchAddDialog() );
                }
                //TODO adapter and viewholder for animal
                break;
            case VISIT:
                addButton.setVisibility(View.INVISIBLE);
                //TODO adapter and viewholder for visit
                break;
            case EXPENSE:
                Log.d(TAG,tabPosition+"");
                if(profileType != ProfileFragment.Type.ANIMAL)
                    addButton.setVisibility(View.INVISIBLE);
                else
                    addButton.setOnClickListener(v -> launchAddDialog() );
                //TODO adapter and viewholder for expense
                break;
            case RELATION:
                addButton.setOnClickListener(v -> launchAddDialog() );
                break;
            case HEALTH:
                addButton.setOnClickListener(v -> launchAddDialog() );
                break;
            case FOOD:
                addButton.setOnClickListener(v -> launchAddDialog() );
                break;
        }

        return layout;
    }

    private void launchAddDialog() {

        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //is is a owner profile it can add animal, veterinarian can't add anything
        if((this.profileType == ProfileFragment.Type.PUBLIC_AUTHORITY) || (this.profileType == ProfileFragment.Type.PRIVATE) ){
            switch (this.tabPosition){
                case ANIMAL:
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
                    break;
                default:
                    throw new IllegalArgumentException("This tab position is invalid for add on list in private");
            }
        }
        else if(this.profileType == ProfileFragment.Type.ANIMAL){//for animal
            switch (this.tabPosition) {
                case RELATION:
                    editDialog.setContentView(R.layout.create_relation);
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
                    ArrayAdapter<CharSequence> expenseAdapter= ArrayAdapter.createFromResource(getContext(),R.array.accessory_categories,
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
