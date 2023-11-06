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
import android.provider.Settings;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Activity.MainActivity;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Presenters.ReportPresenter;
import it.uniba.dib.sms222334.Presenters.RequestPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.LocationTracker;
import it.uniba.dib.sms222334.Utils.Permissions.AndroidPermission;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Utils.Permissions.PermissionInterface;
import it.uniba.dib.sms222334.Utils.Permissions.PermissionManager;
import it.uniba.dib.sms222334.Utils.ReportType;
import it.uniba.dib.sms222334.Utils.RequestType;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;
import it.uniba.dib.sms222334.Views.AnimalAppEditText;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.RequestReport.RequestReportAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.RequestReport.ReportViewHolder;
import it.uniba.dib.sms222334.Views.RecycleViews.RequestReport.RequestViewHolder;

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

    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private ArrayList<Document> requestAndReportsList;
    private RequestReportAdapter adapter;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reportPresenter = new ReportPresenter(this);
        requestPresenter = new RequestPresenter(this);

        LocationTracker.getInstance(getContext()).setNotifyLocationChangedListener(new LocationTracker.NotifyLocationChanged() {
            @Override
            public void locationChanged() {
                refreshRequestReportDistances();
            }
        });

        registerPermissionLauncher();

        final View layout = inflater.inflate(R.layout.home_fragment, container, false);

        recyclerView = layout.findViewById(R.id.list_item);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new ItemDecorator(0));

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

    @Override
    public void onStart() {
        super.onStart();

        LocationTracker.getInstance(getContext()).startLocationTracking();

        initReportsAndRequests();
        loadReportsAndRequests();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.isLogged = SessionManager.getInstance().isLogged();

        if (isLogged)
            this.role = SessionManager.getInstance().getCurrentUser().getRole();

        reportButton.setOnClickListener(v -> {
            launchReportDialog(null);
        });

        if (role == UserRole.VETERINARIAN) {
            requestButton.setVisibility(View.GONE);
        } else {
            requestButton.setOnClickListener(v -> {
                if (isLogged)
                    launchRequestDialog(null);
                else
                    ((MainActivity) getActivity()).forceLogin();
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        LocationTracker.getInstance(getContext()).stopLocationUpdates();
    }

    private void launchReportDialog(Report report) {
        openReportDialog(report);
    }

    public void openReportDialog(Report report) {
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
        Switch updateReportLocation = editDialog.findViewById(R.id.update_report_location_switch);

        Spinner reportSpinner = editDialog.findViewById(R.id.report_spinner);
        Spinner myAnimalSpinner = editDialog.findViewById(R.id.my_animal_spinner);
        Spinner speciesSpinner = editDialog.findViewById(R.id.species_spinner);

        ArrayAdapter<CharSequence> speciesAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.animal_species,
                android.R.layout.simple_list_item_1);
        speciesSpinner.setAdapter(speciesAdapter);

        List<Animal> myAnimalNames = reportPresenter.getMyAnimalNames(false);
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
                if (report == null)
                    handleReportTypeSelection(position, name, description, age, reportSpinner, speciesSpinner, myAnimalSpinner, shareAnimalProfile, myAnimalNames, myAnimalAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        speciesSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (reportSpinner.getSelectedItemPosition() == 1 || (reportSpinner.getSelectedItemPosition() == 2 && myAnimalSpinner.getSelectedItemPosition() != 0))
                    return true;

                return false;
            }
        });

        myAnimalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (report == null)
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
            switch (LocationTracker.getInstance(getContext()).checkLocationState()) {
                case PERMISSION_NOT_GRANTED:
                    launchPermissionHandler(AndroidPermission.ACCESS_FINE_LOCATION);
                    break;

                case PROVIDER_DISABLED:
                    showGPSDisabledDialog();
                    break;

                case LOCATION_IS_NOT_TRACKING:
                    LocationTracker.getInstance(getContext()).startLocationTracking();
                    break;
            }

            if (report == null) {
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
            } else {
                Location location;
                if (updateReportLocation.isChecked())
                    location = LocationTracker.getInstance(getContext()).getLocation();
                else {
                    location = new Location("GeoPointProvider");
                    location.setLatitude(report.getLocation().getLatitude());
                    location.setLongitude(report.getLocation().getLongitude());
                }

                reportPresenter.onEdit(
                        report,
                        description.getText().toString(),
                        name.getText().toString(),
                        age.getText().toString(),
                        (float) location.getLatitude(),
                        (float) location.getLongitude()
                );
            }
        });

        if (report != null) {
            reportSpinner.setSelection(report.getType().ordinal());
            reportSpinner.setEnabled(false);

            speciesSpinner.setSelection(report.getAnimalSpecies().ordinal());
            speciesSpinner.setEnabled(false);

            if (report.getType() == ReportType.FIND)
                myAnimalSpinner.setVisibility(View.GONE);
            else if (report.getType() == ReportType.LOST)
                myAnimalSpinner.setVisibility(View.VISIBLE);
            else if (report.getType() == ReportType.IN_DANGER) {
                if (report.getAnimalID().equals(""))
                    myAnimalSpinner.setVisibility(View.GONE);
                else
                    myAnimalSpinner.setVisibility(View.VISIBLE);
            }

            updateReportLocation.setVisibility(View.VISIBLE);

            photoImageView.setImageBitmap(report.getReportPhoto());

            name.setText(report.getAnimalName());
            age.setText(DateUtilities.parseAgeDate(report.getAnimalAge(), getContext()));
            if (!report.getAnimalID().equals("")) {
                name.setEnabled(false);
                age.setEnabled(false);
            }

            description.setText(report.getDescription());

            if (!report.getAnimalID().equals("")) {
                int position = -1;

                for (int i = 0; i < myAnimalNames.size(); i++) {
                    Animal animal = myAnimalNames.get(i);
                    if (animal.getFirebaseID().equals(report.getAnimalID())) {
                        position = i;
                        break;
                    }
                }

                if (position != -1) {
                    myAnimalSpinner.setSelection(position);
                    myAnimalSpinner.setEnabled(false);
                }
            }

            ((TextView) editDialog.findViewById(R.id.add_report_title)).setText(this.getString(R.string.edit_report));
            saveButton.setText(this.getString(R.string.edit));
        }

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

    private void launchRequestDialog(Request request) {
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

        createButton.setOnClickListener(v -> {
            if (request != null) {
                requestPresenter.onEdit(
                        request,
                        description.getText().toString(),
                        speciesSpinner.getSelectedItemPosition(),
                        (role == UserRole.PUBLIC_AUTHORITY && requestSpinner.getSelectedItemPosition() == 0) ? beds.getText().toString() : null
                );
            } else {
                requestPresenter.onAdd(
                        findRequestType(requestSpinner),
                        description.getText().toString(),
                        speciesSpinner.getSelectedItemPosition(),
                        selectedAnimal,
                        (role == UserRole.PUBLIC_AUTHORITY && requestSpinner.getSelectedItemPosition() == 0) ? beds.getText().toString() : null
                );
            }
        });

        if (request != null) {
            if ((role == UserRole.PRIVATE && request.getType() == RequestType.FIND_ANIMAL) || (role == UserRole.PUBLIC_AUTHORITY && request.getType() == RequestType.OFFER_BEDS))
                requestSpinner.setSelection(0);
            else
                requestSpinner.setSelection(request.getType().ordinal());

            requestSpinner.setEnabled(false);

            Animal requestAnimal = request.getAnimal();
            if (requestAnimal != null) {
                int position = -1;
                for (int i = 0; i < myAnimalNames.size(); i++) {
                    Animal animal = myAnimalNames.get(i);
                    if (animal.getFirebaseID().equals(requestAnimal.getFirebaseID())) {
                        position = i;
                        break;
                    }
                }

                if (position != -1) {
                    animalSpinner.setSelection(position);
                    animalSpinner.setEnabled(false);

                    speciesSpinner.setSelection(requestAnimal.getSpecies().ordinal());
                    speciesSpinner.setEnabled(false);
                }
            }

            description.setText(request.getDescription());

            if (request.getType() == RequestType.OFFER_BEDS) {
                speciesSpinner.setSelection(request.getAnimalSpecies().ordinal());
                speciesSpinner.setEnabled(false);

                beds.setText(Integer.toString(request.getNBeds()));
            }

            ((TextView) editDialog.findViewById(R.id.add_request_title)).setText(this.getString(R.string.edit_request));
            createButton.setText(this.getString(R.string.edit));
        }

        editDialog.show();
        editDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        editDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        editDialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void initReportsAndRequests() {
        if (requestAndReportsList == null)
            requestAndReportsList = new ArrayList<>();

        if (adapter == null)
            adapter = new RequestReportAdapter(this, requestAndReportsList);

        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                recyclerView.post(new Runnable() {
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        if (position != -1 && adapter != null) {
                            if (adapter.isMenuShown(position))
                                adapter.closeMenu();
                            else
                                adapter.showMenu(position);
                        }
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                recyclerView.post(new Runnable() {
                    public void run() {
                        adapter.closeMenu();
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void loadReportsAndRequests() {
        LocationTracker.LocationState state = LocationTracker.getInstance(getContext()).checkLocationState();

        switch (state) {
            case PERMISSION_NOT_GRANTED:
                launchPermissionHandler(AndroidPermission.ACCESS_FINE_LOCATION);
                break;

            case PROVIDER_DISABLED:
                showGPSDisabledDialog();
                break;

            case LOCATION_IS_NOT_TRACKING:
                LocationTracker.getInstance(getContext()).startLocationTracking();
                break;
        }

        requestAndReportsList.clear();
        adapter.notifyDataSetChanged();

        reportPresenter.getReportList(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                requestAndReportsList.add((Document) result);
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
                requestAndReportsList.add((Document) result);
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

    public void deleteRequestReport(Document requestReport) {
        if (requestReport instanceof Request) {
            requestPresenter.delete((Request) requestReport);
        } else {
            reportPresenter.delete((Report) requestReport);
        }
    }

    public void sharePositionRequestReport(Document requestReport) {
        GeoPoint location;
        String subject;
        if (requestReport instanceof Request) {
            location = ((Request) requestReport).getLocation();
            subject = this.getString(R.string.request_location_subject);
        } else {
            location = ((Report) requestReport).getLocation();
            subject = this.getString(R.string.report_location_subject);
        }

        String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude();

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);

        startActivity(Intent.createChooser(sharingIntent, this.getString(R.string.location_chooser_title)));
    }

    public void editRequestReport(Document requestReport) {
        if (requestReport instanceof Request) {
            launchRequestDialog((Request) requestReport);
        } else
            launchReportDialog((Report) requestReport);
    }

    public void callRequestUser(Document requestReport) {
        long phone;
        if (requestReport instanceof Request)
            phone = ((Request) requestReport).getUser().getPhone();
        else
            phone = ((Report) requestReport).getUser().getPhone();

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
        if (intent != null)
            startActivity(intent);
    }

    private void refreshRequestReportDistances() {
        LocationTracker.LocationState state = LocationTracker.getInstance(getContext()).checkLocationState();

        switch (state) {
            case PERMISSION_NOT_GRANTED:
                launchPermissionHandler(AndroidPermission.ACCESS_FINE_LOCATION);
                break;

            case PROVIDER_DISABLED:
                showGPSDisabledDialog();
                break;

            case LOCATION_IS_NOT_TRACKING:
                LocationTracker.getInstance(getContext()).startLocationTracking();
                break;

            case LOCATION_IS_TRACKING_AND_NOT_AVAILABLE:
            case LOCATION_IS_TRACKING_AND_AVAILABLE:
                Location devicePosition = LocationTracker.getInstance(getContext()).getLocation();

                if (adapter != null && !adapter.isMenuShown()) {
                    for (int i = 0; i < adapter.getItemCount(); i++) {
                        RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                        if (viewHolder instanceof RequestViewHolder) {
                            RequestViewHolder requestViewHolder = (RequestViewHolder) viewHolder;
                            requestViewHolder.updateDistance(devicePosition);
                        } else if (viewHolder instanceof ReportViewHolder) {
                            ReportViewHolder reportViewHolder = (ReportViewHolder) viewHolder;
                            reportViewHolder.updateDistance(devicePosition);
                        }
                    }

                    if (devicePosition != null)
                        adapter.sortByDistance();
                    else
                        LocationTracker.getInstance(getContext()).showLocationNotAvailable();
                }
                break;
        }
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

    public void showReportUpdateSuccessful(Document document) {
        Toast.makeText(requireContext(), this.getString(R.string.report_update_successful), Toast.LENGTH_SHORT).show();

        if (adapter != null)
            adapter.updateDocument(document);

        if (editDialog != null)
            editDialog.cancel();
    }

    public void showReportUpdateError() {
        Toast.makeText(requireContext(), this.getString(R.string.report_update_error), Toast.LENGTH_SHORT).show();
    }

    public void showDocumentDeleteSuccessful(Document requestReport) {
        if (requestReport instanceof Report)
            Toast.makeText(requireContext(), this.getString(R.string.report_delete_successful), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(requireContext(), this.getString(R.string.request_delete_successful), Toast.LENGTH_SHORT).show();

        if (requestReport != null && adapter != null)
            adapter.removeDocument(requestReport);
    }

    public void showDocumentDeleteError(Document requestReport) {
        if (requestReport instanceof Report)
            Toast.makeText(requireContext(), this.getString(R.string.report_delete_error), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(requireContext(), this.getString(R.string.request_delete_error), Toast.LENGTH_SHORT).show();
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

    public void showRequestUpdateSuccessful(Document document) {
        Toast.makeText(requireContext(), this.getString(R.string.request_update_successful), Toast.LENGTH_SHORT).show();

        if (adapter != null)
            adapter.updateDocument(document);

        if (editDialog != null)
            editDialog.cancel();
    }

    public void showRequestUpdateError() {
        Toast.makeText(requireContext(), this.getString(R.string.request_update_error), Toast.LENGTH_SHORT).show();
    }

    public void showGPSDisabledDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        if (editDialog == null)
            alertDialog.setMessage(getContext().getString(R.string.location_gps_disabled_message_for_distance_report));
        else
            alertDialog.setMessage(getContext().getString(R.string.location_gps_disabled_message_for_create_report));
        alertDialog.setPositiveButton(getContext().getString(R.string.settings), (dialog, which) -> {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            getContext().startActivity(intent);
        });
        alertDialog.setNegativeButton(getContext().getString(R.string.location_gps_disabled_cancel), (dialog, which) -> {
        });
        alertDialog.show();
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
                builder.setTitle(this.getString(R.string.permission_location_explanation_title));
                if (editDialog == null)
                    builder.setMessage(this.getString(R.string.permission_location_explanation_description_for_distance_report));
                else
                    builder.setMessage(this.getString(R.string.permission_location_explanation_description_for_create_report));
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
                if (editDialog == null)
                    refreshRequestReportDistances();
                else
                    openReportDialog(null);
                break;
        }
    }

    @Override
    public void permissionNotGranted(AndroidPermission permission) {
        switch (permission) {
            case ACCESS_FINE_LOCATION:
                AnimalAppDialog dialog = new AnimalAppDialog(getContext());
                if (editDialog == null)
                    dialog.setContentView(this.getString(R.string.permission_location_not_granted_description_for_distance_report), AnimalAppDialog.DialogType.CRITICAL);
                else
                    dialog.setContentView(this.getString(R.string.permission_location_not_granted_description_for_create_report), AnimalAppDialog.DialogType.CRITICAL);
                dialog.setBannerText(this.getString(R.string.warning));
                dialog.hideButtons();
                dialog.show();
                break;
        }
    }
}
