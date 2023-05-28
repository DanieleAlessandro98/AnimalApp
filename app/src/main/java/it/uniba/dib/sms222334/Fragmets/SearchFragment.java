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

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.RecycleViews.ItemDecorator;
import it.uniba.dib.sms222334.Views.RecycleViews.RequestSegnalation.RequestSegnalationAdapter;
import it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities.VeterinarianAuthoritiesAdapter;

public class SearchFragment extends Fragment {

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layout = inflater.inflate(R.layout.search_fragment, container, false);

        recyclerView = layout.findViewById(R.id.list_item);

        ArrayList<User> listaProva = new ArrayList<>();
        Veterinarian v1 = Veterinarian.Builder.create("TestID", "giuseppeblabla", "ciao")
                .setLegalSite("Francavilla Fontana").build();
        PublicAuthority p1 = PublicAuthority.Builder.create("TestID", "giuseppeblabla", "ciao").
        setLegalSite("Brindisi").build();

        listaProva.add(v1);
        listaProva.add(p1);
        listaProva.add(p1);
        listaProva.add(v1);
        listaProva.add(p1);
        listaProva.add(v1);
        listaProva.add(p1);
        listaProva.add(v1);


        VeterinarianAuthoritiesAdapter adapter = new VeterinarianAuthoritiesAdapter(listaProva, getContext());

        adapter.setOnProfileClickListener(new VeterinarianAuthoritiesAdapter.OnProfileClicked() {
            @Override
            public void OnProfileClicked(User profile) {
                FragmentManager fragmentManager = getParentFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.addToBackStack(null);
                transaction.replace(R.id.frame_for_fragment, new ProfileFragment(profile)).commit();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ItemDecorator(0));

        return layout;
    }
}
