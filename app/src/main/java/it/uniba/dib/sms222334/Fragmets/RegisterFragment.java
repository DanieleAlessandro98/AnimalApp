package it.uniba.dib.sms222334.Fragmets;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;

import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.Adapter.AnimalAppPageAdapter;

public class RegisterFragment extends Fragment{
    public enum Type{PRIVATE,PUBLIC_AUTHORITY,VETERINARIAN}

    private int inflatedLayout;

    public RegisterFragment() {

    }

    public static RegisterFragment newInstance(Type profileType) {
        RegisterFragment myFragment = new RegisterFragment();

        Bundle args = new Bundle();
        args.putInt("profile_type", profileType.ordinal());
        myFragment.setArguments(args);

        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        switch (Type.values()[getArguments().getInt("profile_type")]){
            case PRIVATE:
                inflatedLayout=R.layout.private_register;
                break;
            case PUBLIC_AUTHORITY:
                inflatedLayout=R.layout.public_authority_register;
                break;
            case VETERINARIAN:
                inflatedLayout=R.layout.veterinarian_register;
                break;
            default:
                throw new IllegalArgumentException("Invalid registerType");
        }

        final View layout= inflater.inflate(inflatedLayout,container,false);

        if(inflatedLayout==R.layout.private_register){
            ImageButton datePickerButton=layout.findViewById(R.id.date_picker_button);
            TextView dateTextView=layout.findViewById(R.id.date_text_view);
            datePickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();

                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            getContext(),
                            new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            dateTextView.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                        }
                    }, year, month, day);

                    datePickerDialog.show();
                }
            });
        }

        Spinner prefixSpinner= layout.findViewById(R.id.prefix_spinner);
        ArrayAdapter<CharSequence> prefixAdapter= ArrayAdapter.createFromResource(getContext(),R.array.phone_prefixes,
                android.R.layout.simple_list_item_1);
        prefixAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefixSpinner.setAdapter(prefixAdapter);

        return layout;
    }
}
