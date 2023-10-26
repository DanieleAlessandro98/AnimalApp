package it.uniba.dib.sms222334.Fragmets;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Presenters.ReportPresenter;
import it.uniba.dib.sms222334.Presenters.RequestPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.LocationTracker;
import it.uniba.dib.sms222334.Utils.Permissions.AndroidPermission;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.Permissions.PermissionInterface;
import it.uniba.dib.sms222334.Utils.Permissions.PermissionManager;
import it.uniba.dib.sms222334.Utils.RequestType;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.Expences.RequestReport.RequestReportAdapter;

public class HomeFragment extends Fragment implements PermissionInterface<AndroidPermission> {

    final static String TAG = "HomeFragment";

    UserRole role;
    Button requestButton;
    ImageButton reportButton;

    RecyclerView recyclerView;

    Boolean isLogged;

    ReportPresenter reportPresenter;
    RequestPresenter requestPresenter;
    private ActivityResultLauncher<Intent> photoPickerResultLauncher;
    private ImageView photoImageView;
    private Dialog editDialog;
    private Animal selectedAnimal;
    private boolean isSharedAnimalProfile;
    private RequestReportAdapter adapter;
    private ArrayList combinedList;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        LocationTracker.getInstance(getContext()).startLocationUpdates();

        this.isLogged = SessionManager.getInstance().isLogged();

        if (isLogged)
            this.role = SessionManager.getInstance().getCurrentUser().getRole();

        reportButton.setOnClickListener(v -> {
            launchReportDialog();
        });

        if (role == UserRole.VETERINARIAN) {
            requestButton.setVisibility(View.GONE);
        } else {
            requestButton.setOnClickListener(v -> {
                if (isLogged)
                    launchRequestDialog();
                else
                    ((MainActivity) getActivity()).forceLogin();
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reportPresenter = new ReportPresenter(this);
        requestPresenter = new RequestPresenter(this);

        LocationTracker.getInstance(getContext()).startLocationUpdates();

        registerPermissionLauncher();

        final View layout = inflater.inflate(R.layout.home_fragment, container, false);

        recyclerView = layout.findViewById(R.id.list_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new ItemDecorator(0));

        loadReportsAndRequests();

        Log.d(TAG, recyclerView.getRecycledViewPool().getRecycledViewCount(R.layout.request_list_item) + "");

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
        launchPermissionHandler(AndroidPermission.ACCESS_FINE_LOCATION);
    }

    public void openReportDialog() {
        selectedAnimal = null;

        editDialog = new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.add_report);

        photoImageView = editDialog.findViewById(R.id.image_view);

        Button backButton = editDialog.findViewById(R.id.back_button);
        Button saveButton = editDialog.findViewById(R.id.save_button);
        ImageButton photoButton = editDialog.findViewById(R.id.image_button);

        AnimalAppEditText name = editDialog.findViewById(R.id.nameEditText);
        AnimalAppEditText description = editDialog.findViewById(R.id.description);
        AnimalAppEditText age = editDialog.findViewById(R.id.ageEditText);
        Switch shareAnimalProfile = editDialog.findViewById(R.id.share_profile_switch);

        Spinner reportSpinner = editDialog.findViewById(R.id.report_spinner);
        Spinner myAnimalSpinner = editDialog.findViewById(R.id.my_animal_spinner);
        Spinner speciesSpinner = editDialog.findViewById(R.id.species_spinner);

        ArrayAdapter<CharSequence> speciesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.animal_species,
                android.R.layout.simple_list_item_1);
        speciesSpinner.setAdapter(speciesAdapter);

        List<Animal> myAnimalNames = new ArrayList<>();
        ArrayAdapter<Animal> myAnimalAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                myAnimalNames);
        myAnimalSpinner.setAdapter(myAnimalAdapter);

        ArrayAdapter<CharSequence> reportAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.report_type,
                android.R.layout.simple_list_item_1);
        reportSpinner.setAdapter(reportAdapter);

        reportSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleReportTypeSelection(position, name, description, age, reportSpinner, speciesSpinner, myAnimalSpinner, shareAnimalProfile, myAnimalNames, myAnimalAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
                handleMyAnimalSelection(position, reportSpinner, myAnimalSpinner, speciesSpinner, myAnimalNames, name, description, age, shareAnimalProfile);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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

        saveButton.setOnClickListener(v -> {
            Location location = LocationTracker.getInstance(getContext()).getLocation();
            if (location != null) {
                reportPresenter.onAdd(
                        reportSpinner.getSelectedItemPosition(),
                        speciesSpinner.getSelectedItemPosition(),
                        description.getText().toString(),
                        name.getText().toString(),
                        age.getText().toString(),
                        (float) location.getLatitude(),
                        (float) location.getLongitude(),
                        selectedAnimal == null ? "" : selectedAnimal.getFirebaseID(),
                        isSharedAnimalProfile
                );
            }
        });

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void handleReportTypeSelection(int position, AnimalAppEditText name, AnimalAppEditText description, AnimalAppEditText age, Spinner reportSpinner, Spinner speciesSpinner, Spinner myAnimalSpinner, Switch shareAnimalProfile, List<Animal> myAnimalNames, ArrayAdapter<Animal> myAnimalAdapter) {
        if (position == 1 && !SessionManager.getInstance().isLogged()) {
            showNotLoggedForLostOption();
            reportSpinner.setSelection(0);
            return;
        }

        if (position == 1 && reportPresenter.getMyAnimalNames(false).size() == 0) {
            showNotAnimalForLostOption();
            reportSpinner.setSelection(0);
            return;
        }

        switch (position) {
            case 0:
                updateViewForMyAnimalsSelection(myAnimalSpinner, false);
                updateViewForMyAnimalsProfile(shareAnimalProfile, false);

                setFocusableReportFields(name, age, true);
                clearCommonFields(name, description, age, speciesSpinner);

                myAnimalSpinner.setSelection(0);

                myAnimalNames.clear();
                myAnimalAdapter.notifyDataSetChanged();

                selectedAnimal = null;
                break;

            case 1:
                updateViewForMyAnimalsSelection(myAnimalSpinner, true);
                updateViewForMyAnimalsProfile(shareAnimalProfile, true);

                setFocusableReportFields(name, age, false);

                myAnimalNames.clear();
                myAnimalNames.addAll(reportPresenter.getMyAnimalNames(false));
                myAnimalAdapter.notifyDataSetChanged();

                if (myAnimalNames.size() > 0) {
                    selectedAnimal = myAnimalNames.get(0);
                    myAnimalSpinner.setSelection(0);
                    fillCommonFields(name, description, age, speciesSpinner);
                } else {
                    selectedAnimal = null;
                    clearCommonFields(name, description, age, speciesSpinner);
                }
                break;

            case 2:
                updateViewForMyAnimalsSelection(myAnimalSpinner, SessionManager.getInstance().isLogged());
                updateViewForMyAnimalsProfile(shareAnimalProfile, SessionManager.getInstance().isLogged());

                myAnimalNames.clear();
                myAnimalNames.addAll(reportPresenter.getMyAnimalNames(true));
                myAnimalAdapter.notifyDataSetChanged();

                selectedAnimal = null;
                myAnimalSpinner.setSelection(0);
                clearCommonFields(name, description, age, speciesSpinner);
                break;
        }
    }

    private void handleMyAnimalSelection(int position, Spinner reportSpinner, Spinner myAnimalSpinner, Spinner speciesSpinner, List<Animal> myAnimalNames, AnimalAppEditText name, AnimalAppEditText description, AnimalAppEditText age, Switch shareAnimalProfile) {
        if (reportSpinner.getSelectedItemPosition() == 1 && position >= 0) {
            selectedAnimal = myAnimalNames.get(position);

            fillCommonFields(name, description, age, speciesSpinner);
        } else if (reportSpinner.getSelectedItemPosition() == 2 && position == 0) {
            selectedAnimal = null;

            setFocusableReportFields(name, age, true);
            clearCommonFields(name, description, age, speciesSpinner);

            updateViewForMyAnimalsSelection(myAnimalSpinner, true);
            updateViewForMyAnimalsProfile(shareAnimalProfile, false);
        } else if (reportSpinner.getSelectedItemPosition() == 2 && position >= 1) {
            selectedAnimal = myAnimalNames.get(position);

            setFocusableReportFields(name, age, false);
            fillCommonFields(name, description, age, speciesSpinner);

            updateViewForMyAnimalsSelection(myAnimalSpinner, true);
            updateViewForMyAnimalsProfile(shareAnimalProfile, true);
        } else {
            selectedAnimal = null;
            clearCommonFields(name, description, age, speciesSpinner);
        }
    }

    private void setFocusableReportFields(AnimalAppEditText name, AnimalAppEditText age, boolean focusable) {
        name.setFocusable(focusable);
        name.setFocusableInTouchMode(focusable);
        age.setFocusable(focusable);
        age.setFocusableInTouchMode(focusable);
    }

    private void clearCommonFields(AnimalAppEditText name, AnimalAppEditText description, AnimalAppEditText age, Spinner speciesSpinner) {
        name.setText("");
        description.setText("");
        age.setText("");
        speciesSpinner.setSelection(0);
        photoImageView.setImageBitmap(null);
    }

    private void fillCommonFields(AnimalAppEditText name, AnimalAppEditText description, AnimalAppEditText age, Spinner speciesSpinner) {
        name.setText(selectedAnimal.getName());
        description.setText("");
        age.setText(DateUtilities.calculateAge(selectedAnimal.getBirthDate(), getContext()));
        speciesSpinner.setSelection(selectedAnimal.getSpecies().ordinal());
        photoImageView.setImageBitmap(selectedAnimal.getPhoto());
    }

    private void updateViewForMyAnimalsSelection(Spinner myAnimalSpinner, boolean isVisible) {
        if (isVisible)
            myAnimalSpinner.setVisibility(View.VISIBLE);
        else
            myAnimalSpinner.setVisibility(View.GONE);
    }

    private void updateViewForMyAnimalsProfile(Switch shareAnimalProfile, boolean isVisible) {
        if (isVisible)
            shareAnimalProfile.setVisibility(View.VISIBLE);
        else
            shareAnimalProfile.setVisibility(View.GONE);

        isSharedAnimalProfile = false;
        shareAnimalProfile.setChecked(false);
    }

    private void launchRequestDialog() {
        selectedAnimal = null;

        editDialog = new Dialog(getContext());
        editDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        editDialog.setContentView(R.layout.add_request);

        Spinner requestSpinner = editDialog.findViewById(R.id.request_spinner);
        Spinner animalSpinner = editDialog.findViewById(R.id.animal_chooser);
        Spinner speciesSpinner = editDialog.findViewById(R.id.species_chooser);
        AnimalAppEditText beds = editDialog.findViewById(R.id.beds);
        AnimalAppEditText description = editDialog.findViewById(R.id.description);

        ArrayAdapter<CharSequence> requestAdapter;
        ArrayAdapter<CharSequence> speciesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.animal_species,
                android.R.layout.simple_list_item_1);
        speciesSpinner.setAdapter(speciesAdapter);

        List<Animal> myAnimalNames = requestPresenter.getMyAnimalNames();
        ArrayAdapter<Animal> animalAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                myAnimalNames);
        animalSpinner.setAdapter(animalAdapter);

        if (role == UserRole.PRIVATE) {
            requestAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.private_request_type,
                    android.R.layout.simple_list_item_1);

            beds.setVisibility(View.GONE);

            requestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
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
        } else {
            requestAdapter = ArrayAdapter.createFromResource(getContext(),
                    R.array.authority_request_type,
                    android.R.layout.simple_list_item_1);

            requestSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
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

        animalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAnimal = myAnimalNames.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        requestAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestSpinner.setAdapter(requestAdapter);
        requestSpinner.setSelection(0);

        Button backButton = editDialog.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> editDialog.cancel());

        Button createButton = editDialog.findViewById(R.id.create_button);
        createButton.setOnClickListener(v -> requestPresenter.onAdd(
                findRequestType(requestSpinner),
                description.getText().toString(),
                speciesSpinner.getSelectedItemPosition(),
                selectedAnimal,
                (role == UserRole.PUBLIC_AUTHORITY && requestSpinner.getSelectedItemPosition() == 0) ? beds.getText().toString() : null
        ));

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void loadReportsAndRequests() {
        combinedList = new ArrayList();
        adapter = new RequestReportAdapter(combinedList, getContext());
        recyclerView.setAdapter(adapter);

        reportPresenter.getReportList(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                combinedList.add(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDataRetrieved(ArrayList results) {
            }

            @Override
            public void onDataNotFound() {
            }

            @Override
            public void onDataQueryError(Exception e) {
            }
        });

        requestPresenter.getRequestList(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                combinedList.add(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDataRetrieved(ArrayList results) {
            }

            @Override
            public void onDataNotFound() {
            }

            @Override
            public void onDataQueryError(Exception e) {
            }
        });
    }

    private RequestType findRequestType(Spinner requestSpinner) {
        int selectedItemPosition = requestSpinner.getSelectedItemPosition();

        if (role == UserRole.PRIVATE) {
            if (selectedItemPosition == 0)
                return RequestType.FIND_ANIMAL;
            else if (selectedItemPosition == 1)
                return RequestType.OFFER_ANIMAL;
        } else {
            if (selectedItemPosition == 0)
                return RequestType.OFFER_BEDS;
            else if (selectedItemPosition == 1)
                return RequestType.OFFER_ANIMAL;
        }

        return null;
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

    public void showReportCreateSuccessful() {
        Toast.makeText(requireContext(), this.getString(R.string.report_create_successful), Toast.LENGTH_SHORT).show();

        if (editDialog != null)
            editDialog.cancel();
    }

    public void showReportCreateError() {
        Toast.makeText(requireContext(), this.getString(R.string.report_create_error), Toast.LENGTH_SHORT).show();
    }

    private void showNotLoggedForLostOption() {
        Toast.makeText(getContext(), this.getString(R.string.invalid_report_select_lost_option_1), Toast.LENGTH_SHORT).show();
    }

    private void showNotAnimalForLostOption() {
        Toast.makeText(getContext(), this.getString(R.string.invalid_report_select_lost_option_2), Toast.LENGTH_SHORT).show();
    }

    public void showInvalidReportDescription() {
        Toast.makeText(getContext(), this.getString(R.string.invalid_report_description), Toast.LENGTH_SHORT).show();
    }

    public void showInvalidRequestDescription() {
        Toast.makeText(getContext(), this.getString(R.string.invalid_request_description), Toast.LENGTH_SHORT).show();
    }

    public void showInvalidRequestBeds() {
        Toast.makeText(requireContext(), this.getString(R.string.invalid_request_beds), Toast.LENGTH_SHORT).show();
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

    public void showInvalidReportSelectedAnimal() {
        Toast.makeText(requireContext(), this.getString(R.string.invalid_report_selected_animal), Toast.LENGTH_SHORT).show();
    }

    public void showRequestCreateSuccessful() {
        Toast.makeText(requireContext(), this.getString(R.string.request_create_successful), Toast.LENGTH_SHORT).show();

        if (editDialog != null)
            editDialog.cancel();
    }

    public void showRequestCreateError() {
        Toast.makeText(requireContext(), this.getString(R.string.request_create_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Fragment getFragment() {
        return this;
    }

    @Override
    public void registerPermissionLauncher() {
        requestPermissionLauncher = PermissionManager.getInstance().registerPermissionLauncher(this);
    }

    @Override
    public void requestPermission(AndroidPermission permission) {
        String permissionString = AndroidPermission.findManifestStringFromAndroidPermission(permission);
        requestPermissionLauncher.launch(new String[]{permissionString});
    }

    @Override
    public void launchPermissionHandler(AndroidPermission permission) {
        PermissionManager.getInstance().checkAndRequestPermission(getActivity(), this, permission);
    }

    @Override
    public void showPermissionExplanation(AndroidPermission permission) {
        switch (permission) {
            case ACCESS_FINE_LOCATION:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(this.getString(R.string.permission_location_explanation_title_for_report));
                builder.setMessage(this.getString(R.string.permission_location_explanation_description_for_report));
                builder.setPositiveButton(this.getString(R.string.permission_explanation_dialog_grant), (dialog, which) -> {
                    requestPermission(permission);
                    dialog.dismiss();
                });

                builder.setNegativeButton(this.getString(R.string.permission_explanation_dialog_cancel), null);
                builder.show();
                break;
        }
    }

    @Override
    public void permissionGranted(AndroidPermission permission) {
        switch (permission) {
            case ACCESS_FINE_LOCATION:
                openReportDialog();
                break;
        }
    }

    @Override
    public void permissionNotGranted(AndroidPermission permission) {
        switch (permission) {
            case ACCESS_FINE_LOCATION:
                AnimalAppDialog dialog = new AnimalAppDialog(getContext());
                dialog.setContentView(this.getString(R.string.permission_location_not_granted_description_for_report), AnimalAppDialog.DialogType.CRITICAL);
                dialog.setBannerText(this.getString(R.string.warning));
                dialog.hideButtons();
                dialog.show();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationTracker.getInstance(getContext()).stopLocationUpdates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocationTracker.getInstance(getContext()).stopLocationUpdates();
    }
}
