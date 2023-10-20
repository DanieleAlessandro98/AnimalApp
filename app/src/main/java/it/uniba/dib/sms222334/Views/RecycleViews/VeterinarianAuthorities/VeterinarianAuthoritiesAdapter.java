package it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;

public class VeterinarianAuthoritiesAdapter extends RecyclerView.Adapter<VeterinarianAuthoritiesViewHolder> implements VeterinarianAuthoritiesViewHolder.OnItemClickListener{

    ArrayList<User>  veterinarianAuthoritiesList;
    Context context;

    public interface OnProfileClicked{
        void OnProfileClicked(User profile);
    }

    private OnProfileClicked onProfileClicked;

    public void setOnProfileClickListener(OnProfileClicked listener){
        this.onProfileClicked=listener;
    }


    public VeterinarianAuthoritiesAdapter(ArrayList<User> mModel,Context context){
        this. veterinarianAuthoritiesList = mModel;
        this.context=context;
    }

    @NonNull
    @Override
    public VeterinarianAuthoritiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_list_item,parent,false);

        return new VeterinarianAuthoritiesViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull VeterinarianAuthoritiesViewHolder holder, int position) {
        if(getItemViewType(position)==0){
            holder.bind((Veterinarian)  veterinarianAuthoritiesList.get(position));
        }
        else{
            holder.bind((PublicAuthority)  veterinarianAuthoritiesList.get(position));
        }

        holder.setOnItemClickListener(this);
    }

    @Override
    public int getItemCount() {
        return this. veterinarianAuthoritiesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if( veterinarianAuthoritiesList.get(position) instanceof Veterinarian){
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public void OnItemClick(int position) {
        if(this.onProfileClicked!=null){
            this.onProfileClicked.OnProfileClicked(this.veterinarianAuthoritiesList.get(position));
        }
    }
}
