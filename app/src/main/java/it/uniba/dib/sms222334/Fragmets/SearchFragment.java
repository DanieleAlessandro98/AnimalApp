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

import it.uniba.dib.sms222334.Activity.MainActivity;
import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
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

    VeterinarianAuthoritiesAdapter adapter;


    public SearchFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();

        this.isLogged = SessionManager.getInstance().isLogged();

        if (isLogged)
            this.role = SessionManager.getInstance().getCurrentUser().getRole();

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = layout.findViewById(R.id.list_item);

        ArrayList<User> listaProva = new ArrayList<>();
        Veterinarian v1 = Veterinarian.Builder.create("TestID", "giuseppeblabla", "ciao")
                .setLegalSite(new GeoPoint(40.52943978714336,17.58860651778748)).build();
        PublicAuthority p1 = PublicAuthority.Builder.create("TestID", "giuseppeblabla", "ciao")
                .setLegalSite(new GeoPoint(40.88685094399862,17.16984022995251)).build();

        listaProva.add(v1);
        listaProva.add(p1);
        listaProva.add(p1);
        listaProva.add(v1);
        listaProva.add(p1);
        listaProva.add(v1);
        listaProva.add(p1);
        listaProva.add(v1);

        this.adapter = new VeterinarianAuthoritiesAdapter(listaProva, getContext());

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDecorator(0));

        return layout;
    }
}
