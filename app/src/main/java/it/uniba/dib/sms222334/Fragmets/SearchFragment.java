package it.uniba.dib.sms222334.Fragmets;

import android.os.Bundle;
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

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms222334.Activity.MainActivity;
import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Database.Dao.User.VeterinarianDao;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Presenters.VeterinarianPresenter;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.RecycleViews.Animal.AnimalAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.RequestSegnalation.RequestSegnalationAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities.VeterinarianAuthoritiesAdapter;

public class SearchFragment extends Fragment {

    private boolean isLogged;
    private UserRole role;
    RecyclerView recyclerView;

    public VeterinarianAuthoritiesAdapter adapter;


    public SearchFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();

        this.isLogged = SessionManager.getInstance().isLogged();

        if (isLogged)
            this.role = SessionManager.getInstance().getCurrentUser().getRole();


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = layout.findViewById(R.id.list_item);

        ArrayList<User> listaProva = new ArrayList<>();

        VeterinarianPresenter presenter = new VeterinarianPresenter();
        presenter.action_getVeterinarian(new VeterinarianDao.OnVeterinarianListener() {
            @Override
            public void onGetVeterinarianListener(List<Veterinarian> veterinarianList) {
                listaProva.addAll(veterinarianList);
                adapter = new VeterinarianAuthoritiesAdapter(listaProva, getContext());

                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                recyclerView.addItemDecoration(new ItemDecorator(0));

                adapter.setOnProfileClickListener(profile -> {
                    if(isLogged){
                        FragmentManager fragmentManager = getParentFragmentManager();

                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.addToBackStack("itemPage");
                        transaction.replace(R.id.frame_for_fragment, ProfileFragment.newInstance(profile,getContext())).commit();
                    }
                    else
                        ((MainActivity)getActivity()).forceLogin();
                });
            }
        });


        return layout;
    }
}
