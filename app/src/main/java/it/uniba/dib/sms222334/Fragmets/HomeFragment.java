package it.uniba.dib.sms222334.Fragmets;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Activity.MainActivity;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Presenters.ReportPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.ReportType;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.Expences.RequestReport.RequestReportAdapter;

public class HomeFragment extends Fragment {

    final static String TAG="HomeFragment";

    UserRole role;
    Button requestButton;
    ImageButton reportButton;

    RecyclerView recyclerView;

    Boolean isLogged;

    ReportPresenter reportPresenter;
    private ActivityResultLauncher<Intent> photoPickerResultLauncher;
    private ImageView photoImageView;
    private Dialog editDialog;
    private Animal selectedAnimal;
    private boolean isSharedAnimalProfile;


    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        reportPresenter = new ReportPresenter(this);

        this.isLogged = SessionManager.getInstance().isLogged();

        if (isLogged)
            this.role = SessionManager.getInstance().getCurrentUser().getRole();

        reportButton.setOnClickListener(v -> {
            launchReportDialog();
        });

        if(role == UserRole.VETERINARIAN){
            requestButton.setVisibility(View.GONE);
        }
        else{
            requestButton.setOnClickListener(v -> {
                if(isLogged)
                    launchRequestDialog();
                else
                    ((MainActivity)getActivity()).forceLogin();
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout= inflater.inflate(R.layout.home_fragment,container,false);

        recyclerView = layout.findViewById(R.id.list_item);

        ArrayList<Document> listaProva=new ArrayList<>();
        Request r1=Request.Builder.create("TestID", Request.requestType.FIND_ANIMAL, 130.0F,13.0F)
                .setCreatorName("Giuseppe")
                .setSpecies("Cane")
                .setDescription("Cerco cane bellissimo ciao")
                .build();

        listaProva.add(r1);
        listaProva.add(r1);
        listaProva.add(r1);
        listaProva.add(r1);

        RequestReportAdapter adapter=new RequestReportAdapter(listaProva,getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDecorator(0));

        Log.d(TAG,recyclerView.getRecycledViewPool().getRecycledViewCount(R.layout.request_list_item)+"");

        requestButton = layout.findViewById(R.id.add_request);
        reportButton = layout.findViewById(R.id.add_report);

        this.photoPickerResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        reportPresenter.pickPhoto(selectedImage);
                    }
                });

        return layout;
    }

    private void launchReportDialog() {
        editDialog = new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.add_report);

        List<Animal> myAnimalNames = reportPresenter.getMyAnimalNames();
        isSharedAnimalProfile = false;

        photoImageView = editDialog.findViewById(R.id.image_view);

        Button backButton= editDialog.findViewById(R.id.back_button);
        Button saveButton= editDialog.findViewById(R.id.save_button);
        ImageButton photoButton = editDialog.findViewById(R.id.image_button);

        AnimalAppEditText name = editDialog.findViewById(R.id.nameEditText);
        AnimalAppEditText description = editDialog.findViewById(R.id.description);
        AnimalAppEditText age = editDialog.findViewById(R.id.ageEditText);
        Switch shareAnimalProfile = editDialog.findViewById(R.id.share_profile_switch);

        Spinner reportSpinner= editDialog.findViewById(R.id.report_spinner);
        Spinner myAnimalSpinner = editDialog.findViewById(R.id.my_animal_spinner);
        Spinner speciesSpinner= editDialog.findViewById(R.id.species_spinner);

        ArrayAdapter<CharSequence> speciesAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.animal_species,
                android.R.layout.simple_list_item_1);
        speciesSpinner.setAdapter(speciesAdapter);

        ArrayAdapter<Animal> myAnimalAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                myAnimalNames);
        myAnimalSpinner.setAdapter(myAnimalAdapter);

        ArrayAdapter<CharSequence> reportAdapter= ArrayAdapter.createFromResource(getContext(),
                R.array.report_type,
                android.R.layout.simple_list_item_1);
        reportSpinner.setAdapter(reportAdapter);

        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        myAnimalSpinner.setVisibility(View.GONE);
                        shareAnimalProfile.setVisibility(View.GONE);

                        name.setFocusable(true);
                        name.setFocusableInTouchMode(true);
                        age.setFocusable(true);
                        age.setFocusableInTouchMode(true);

                        isSharedAnimalProfile = false;
                        shareAnimalProfile.setChecked(false);

                        selectedAnimal = null;
                        name.setText("");
                        description.setText("");
                        age.setText("");
                        myAnimalSpinner.setSelection(0);
                        speciesSpinner.setSelection(0);
                        photoImageView.setImageBitmap(null);
                        break;

                    case 1:
                        if (!SessionManager.getInstance().isLogged()) {
                            showNotLoggedForLostOption();
                            reportSpinner.setSelection(0);
                        } else {
                            myAnimalSpinner.setVisibility(View.VISIBLE);
                            shareAnimalProfile.setVisibility(View.VISIBLE);

                            name.setFocusable(false);
                            name.setFocusableInTouchMode(false);
                            age.setFocusable(false);
                            age.setFocusableInTouchMode(false);

                            isSharedAnimalProfile = false;
                            shareAnimalProfile.setChecked(false);

                            if (myAnimalNames.size() > 0) {
                                selectedAnimal = myAnimalNames.get(0);

                                name.setText(selectedAnimal.getName());
                                description.setText("");
                                age.setText(DateUtilities.calculateAge(selectedAnimal.getBirthDate(), getContext()));
                                photoImageView.setImageBitmap(selectedAnimal.getPhoto());
                            } else {
                                selectedAnimal = null;

                                name.setText("");
                                description.setText("");
                                age.setText("");
                                myAnimalSpinner.setSelection(0);
                                speciesSpinner.setSelection(0);
                                photoImageView.setImageBitmap(null);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAnimal = null;
                name.setText("");
                description.setText("");
                age.setText("");
                myAnimalSpinner.setSelection(0);
                speciesSpinner.setSelection(0);
                photoImageView.setImageBitmap(null);
            }
        });

        speciesSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (reportSpinner.getSelectedItemPosition() == 1)
                    return true;

                return false;
            }
        });

        myAnimalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (reportSpinner.getSelectedItemPosition() == 1) {
                    selectedAnimal = myAnimalNames.get(position);

                    name.setText(selectedAnimal.getName());
                    description.setText("");
                    age.setText(DateUtilities.calculateAge(selectedAnimal.getBirthDate(), getContext()));
                    photoImageView.setImageBitmap(selectedAnimal.getPhoto());
                } else {
                    selectedAnimal = null;
                    name.setText("");
                    description.setText("");
                    age.setText("");
                    myAnimalSpinner.setSelection(0);
                    speciesSpinner.setSelection(0);
                    photoImageView.setImageBitmap(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedAnimal = null;
                name.setText("");
                description.setText("");
                age.setText("");
                myAnimalSpinner.setSelection(0);
                speciesSpinner.setSelection(0);
                photoImageView.setImageBitmap(null);
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                photoPickerResultLauncher.launch(photoIntent);
            }
        });

        shareAnimalProfile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSharedAnimalProfile = isChecked;
            }
        });

        backButton.setOnClickListener(v -> editDialog.cancel());

        saveButton.setOnClickListener(v -> reportPresenter.onAdd(
                reportSpinner.getSelectedItemPosition(),
                speciesSpinner.getSelectedItemPosition(),
                description.getText().toString(),
                name.getText().toString(),
                age.getText().toString(),
                selectedAnimal == null ? "" : selectedAnimal.getFirebaseID(),
                isSharedAnimalProfile
                ));

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void launchRequestDialog() {

        editDialog=new Dialog(getContext());
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

        if(role== UserRole.PRIVATE){
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

    public void showInvalidReportDescription() {
        Toast.makeText(getContext(), this.getString(R.string.invalid_report_description), Toast.LENGTH_SHORT).show();
    }

    public void setPhotoPicked(Bitmap bitmap) {
        photoImageView.setImageBitmap(bitmap);
    }

    public Bitmap getPhotoPicked() {
        Drawable drawable = photoImageView.getDrawable();
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();

        return null;
    }

    public void showPhotoUpdateError() {
        Toast.makeText(requireContext(), this.getString(R.string.photo_update_failed), Toast.LENGTH_SHORT).show();
    }

    public void showCreateSuccessful() {
        Toast.makeText(requireContext(), this.getString(R.string.report_create_successful), Toast.LENGTH_SHORT).show();

        if (editDialog != null)
            editDialog.cancel();
    }

    public void showCreateError() {
        Toast.makeText(requireContext(), this.getString(R.string.report_create_error), Toast.LENGTH_SHORT).show();
    }

    private void showNotLoggedForLostOption() {
        Toast.makeText(getContext(), this.getString(R.string.report_select_lost_option), Toast.LENGTH_SHORT).show();
    }

    public void showInvalidAge(int validationCode) {
        switch (validationCode) {
            case 1:
                Toast.makeText(getContext(), this.getString(R.string.invalid_report_age_1), Toast.LENGTH_SHORT).show();
                break;

            case 2:
                Toast.makeText(getContext(), this.getString(R.string.invalid_report_age_2), Toast.LENGTH_SHORT).show();
                break;

            case 3:
                Toast.makeText(getContext(), this.getString(R.string.invalid_report_age_3), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
