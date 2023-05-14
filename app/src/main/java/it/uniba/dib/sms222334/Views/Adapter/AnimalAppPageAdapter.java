package it.uniba.dib.sms222334.Views.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.uniba.dib.sms222334.Fragmets.RegisterFragment;

public class AnimalAppPageAdapter extends FragmentStateAdapter {


    public AnimalAppPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new RegisterFragment(RegisterFragment.Type.VETERINARIAN);
            case 1:
                return new RegisterFragment(RegisterFragment.Type.PRIVATE);
            case 2:
                return new RegisterFragment(RegisterFragment.Type.PUBLIC_AUTHORITY);
            default:
                return new RegisterFragment(RegisterFragment.Type.PRIVATE);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
