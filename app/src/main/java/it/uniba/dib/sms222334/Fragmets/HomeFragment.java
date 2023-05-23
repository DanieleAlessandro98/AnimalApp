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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.NoScrolLayoutManager;
import it.uniba.dib.sms222334.Views.RecycleViews.RequestSegnalation.RequestSegnalationAdapter;

public class HomeFragment extends Fragment {

    final static String TAG="HomeFragment";

    ProfileFragment.Type profileType;
    Button requestButton;
    ImageButton warningButton;

    RecyclerView recyclerView;
    public HomeFragment(ProfileFragment.Type profileType){
        this.profileType=profileType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout= inflater.inflate(R.layout.home_fragment,container,false);

        recyclerView = layout.findViewById(R.id.list_item);

        ArrayList<Document> listaProva=new ArrayList<>();
        Request r1=Request.Builder.create(Request.requestType.FIND_ANIMAL, 130.0F,13.0F)
                .setCreatorName("Giuseppe")
                .setSpecies("Cane")
                .setDescription("Cerco cane bellissimo ciao")
                .build();

        listaProva.add(r1);
        listaProva.add(r1);
        listaProva.add(r1);
        listaProva.add(r1);

        RequestSegnalationAdapter adapter=new RequestSegnalationAdapter(listaProva,getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDecorator(0));

        Log.d(TAG,recyclerView.getRecycledViewPool().getRecycledViewCount(R.layout.request_list_item)+"");

        requestButton = layout.findViewById(R.id.add_request);
        warningButton = layout.findViewById(R.id.add_warning);
        warningButton.setOnClickListener(v -> launchWarningDialog());

        if(profileType== ProfileFragment.Type.VETERINARIAN){
            requestButton.setVisibility(View.GONE);
        }
        else{
            requestButton.setOnClickListener(v -> launchRequestDialog());
        }



        return layout;
    }

    private void launchWarningDialog() {
        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.add_segnalation);
        Button backButton= editDialog.findViewById(R.id.back_button);

        Spinner segnalationSpinner= editDialog.findViewById(R.id.segnalation_spinner);

        Spinner speciesSpinner= editDialog.findViewById(R.id.species_spinner);

        ArrayAdapter<CharSequence> speciesAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.animal_species,
                android.R.layout.simple_list_item_1);

        speciesSpinner.setAdapter(speciesAdapter);

        ArrayAdapter<CharSequence> segnalationAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.segnalation_type,
                android.R.layout.simple_list_item_1);

        segnalationSpinner.setAdapter(segnalationAdapter);

        backButton.setOnClickListener(v -> editDialog.cancel());

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }


    private void launchRequestDialog() {

        final Dialog editDialog=new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.add_request);



        Spinner requestSpinner= editDialog.findViewById(R.id.request_spinner);
        Spinner animalSpinner= editDialog.findViewById(R.id.animal_chooser);
        Spinner speciesSpinner= editDialog.findViewById(R.id.species_chooser);
        AnimalAppEditText beds= editDialog.findViewById(R.id.beds);

        ArrayAdapter<CharSequence> requestAdapter;
        ArrayAdapter<CharSequence> speciesAdapter=ArrayAdapter.createFromResource(getContext(),
                R.array.animal_species,
                android.R.layout.simple_list_item_1);
        speciesSpinner.setAdapter(speciesAdapter);

        if(profileType== ProfileFragment.Type.PRIVATE){
            requestAdapter= ArrayAdapter.createFromResource(getContext(),
                    R.array.private_request_type,
                    android.R.layout.simple_list_item_1);

            beds.setVisibility(View.GONE);

            requestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            speciesSpinner.setVisibility(View.VISIBLE);
                            animalSpinner.setVisibility(View.GONE);
                            break;
                        case 1:
                            animalSpinner.setVisibility(View.VISIBLE);
                            speciesSpinner.setVisibility(View.GONE);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        else{
            requestAdapter= ArrayAdapter.createFromResource(getContext(),
                    R.array.authority_request_type,
                    android.R.layout.simple_list_item_1);

            requestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0:
                            speciesSpinner.setVisibility(View.VISIBLE);
                            beds.setVisibility(View.VISIBLE);
                            animalSpinner.setVisibility(View.GONE);
                            break;
                        case 1:
                            beds.setVisibility(View.GONE);
                            animalSpinner.setVisibility(View.VISIBLE);
                            speciesSpinner.setVisibility(View.GONE);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }





        requestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestSpinner.setAdapter(requestAdapter);

        Button backButton= editDialog.findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> editDialog.cancel());

        requestSpinner.setSelection(0);

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

}
