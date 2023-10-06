package it.uniba.dib.sms222334.Views.Carousel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms222334.R;

public class AddMediaPageFragment extends Fragment {

    public static Fragment newInstance() throws IllegalAccessException, java.lang.InstantiationException {
        return new AddMediaPageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LinearLayout linearLayout = (LinearLayout)
                inflater.inflate(R.layout.carousel_add_media_item, container, false);

        ImageButton mediaButton= linearLayout.findViewById(R.id.add_media_button);

        mediaButton.setOnClickListener(v -> {

        });

        return linearLayout;
    }
}
