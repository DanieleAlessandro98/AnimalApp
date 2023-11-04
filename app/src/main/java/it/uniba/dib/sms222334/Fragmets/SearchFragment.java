package it.uniba.dib.sms222334.Fragmets;

import android.os.Bundle;
import android.os.Parcel;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities.VeterinarianAuthoritiesAdapter;

public class SearchFragment extends Fragment {

    private boolean isLogged;
    private UserRole role;
    RecyclerView recyclerView;

    public VeterinarianAuthoritiesAdapter adapter;

    User userClicked;

    public static ArrayList<User> profileList=new ArrayList<>();

    static boolean firstLoad=true;


    public SearchFragment() {}

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
            else
                ((MainActivity)getActivity()).forceLogin();
        });


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

        return layout;
    }

    private void openProfile(User profile){
        FragmentManager fragmentManager = getParentFragmentManager();

        userClicked=profile;
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.addToBackStack("itemPage");
        transaction.replace(R.id.frame_for_fragment, ProfileFragment.newInstance(profile)).commit();
    }
}
