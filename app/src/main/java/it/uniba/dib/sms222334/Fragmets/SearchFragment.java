package it.uniba.dib.sms222334.Fragmets;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import it.uniba.dib.sms222334.Activity.MainActivity;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.User.UserCallback;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Presenters.VeterinarianPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.LocationTracker;
import it.uniba.dib.sms222334.Utils.Permissions.AndroidPermission;
import it.uniba.dib.sms222334.Utils.Permissions.PermissionInterface;
import it.uniba.dib.sms222334.Utils.Permissions.PermissionManager;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities.VeterinarianAuthoritiesAdapter;

public class SearchFragment extends Fragment implements PermissionInterface<AndroidPermission> {

    private boolean isLogged;
    private UserRole role;
    RecyclerView recyclerView;
    public VeterinarianAuthoritiesAdapter adapter;
    User userClicked;
    public static ArrayList<User> profileList=new ArrayList<>();
    static boolean firstLoad=true;

    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    public SearchFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = layout.findViewById(R.id.list_item);
        adapter = new VeterinarianAuthoritiesAdapter(profileList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDecorator(0));

        adapter.setOnProfileClickListener(profile -> {
            if(isLogged){
                openProfile(profile);
            }
            else{
                Bundle bundle=new Bundle();

                bundle.putParcelable("profile",profile);

                ((MainActivity)getActivity()).forceLogin(bundle);
            }
        });

        LocationTracker.getInstance(getContext()).setNotifyLocationChangedListener(new LocationTracker.NotifyLocationChanged() {
            @Override
            public void locationChanged() {
                refreshPubVetDistances();
            }
        });

        registerPermissionLauncher();

        if(firstLoad){
            profileList.add(0,Private.Builder.create("","","").build()); //progress bar
            adapter.notifyItemInserted(0);

            VeterinarianPresenter presenter = new VeterinarianPresenter();
            presenter.action_getVeterinarian(new UserCallback.UserFindCallback() {
                @Override
                public void onUserFound(User user) {
                    profileList.add(profileList.size()-1, user);
                    adapter.notifyItemInserted(profileList.size()-1);
                }

                @Override
                public void onLastUserFound() {
                    int lastIndex=profileList.size()-1;
                    profileList.remove(lastIndex);
                    adapter.notifyItemRemoved(lastIndex);
                }

                @Override
                public void onUserNotFound(Exception e) {

                }
            });

            firstLoad=false;
        }

        if(savedInstanceState!=null){
            User user=savedInstanceState.getParcelable("profileClicked");

            if(user!=null)
                openProfile(user);
        }

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

        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();

        this.isLogged = SessionManager.getInstance().isLogged();

        if (isLogged)
            this.role = SessionManager.getInstance().getCurrentUser().getRole();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
        outState.putParcelable("profileClicked",this.userClicked);
    }

    private void openProfile(User profile){
        FragmentManager fragmentManager = getParentFragmentManager();

        userClicked=profile;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack("itemPage");
        transaction.replace(R.id.frame_for_fragment, ProfileFragment.newInstance(profile)).commit();
    }

    private void refreshPubVetDistances() {
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
                Location devicePosition = LocationTracker.getInstance(getContext()).getLocation(true);
                if (devicePosition != null)
                    adapter.sortByDistance();
                else
                    LocationTracker.getInstance(getContext()).showLocationNotAvailable();
                break;
        }
    }

    public void showGPSDisabledDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setMessage(getContext().getString(R.string.location_gps_disabled_message_for_distance_pub_vet));
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
                builder.setMessage(this.getString(R.string.permission_location_explanation_description_for_distance_pub_vet));
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
                refreshPubVetDistances();
                break;
        }
    }

    @Override
    public void permissionNotGranted(AndroidPermission permission) {
        switch (permission) {
            case ACCESS_FINE_LOCATION:
                AnimalAppDialog dialog = new AnimalAppDialog(getContext());
                dialog.setContentView(this.getString(R.string.permission_location_not_granted_description_for_distance_pub_vet), AnimalAppDialog.DialogType.CRITICAL);
                dialog.setBannerText(this.getString(R.string.warning));
                dialog.hideButtons();
                dialog.show();
                break;
        }
    }
}
