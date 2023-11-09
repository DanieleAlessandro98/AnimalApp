package it.uniba.dib.sms222334.Views.RecycleViews.VeterinarianAuthorities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import it.uniba.dib.sms222334.Fragmets.ProfileFragment;
import it.uniba.dib.sms222334.Fragmets.SearchFragment;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Report;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.R;

public class VeterinarianAuthoritiesAdapter extends RecyclerView.Adapter<VeterinarianAuthoritiesViewHolder> implements VeterinarianAuthoritiesViewHolder.OnItemClickListener{

    private ArrayList<User>  veterinarianAuthoritiesList;
    private ArrayList<User> arraylist;

    private Context context;

    public interface OnProfileClicked{
        void OnProfileClicked(User profile);
    }

    public enum viewType{VETERINARIAN,PUBLIC_AUTHORITY,PROGRESS_BAR};

    private OnProfileClicked onProfileClicked;

    public void setOnProfileClickListener(OnProfileClicked listener){
        this.onProfileClicked=listener;
    }


    public VeterinarianAuthoritiesAdapter(ArrayList<User> mModel,Context context){
        this.veterinarianAuthoritiesList = mModel;
        this.arraylist = new ArrayList<>();

        this.context=context;
    }

    public void addUser(User user) {
        arraylist.add(user);
    }

    public void removeUser(int position) {
        if (position >= 0 && position < arraylist.size())
            arraylist.remove(position);
    }

    public void sortByDistance() {
        Comparator<User> distanceComparator = new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                if (user1 instanceof Private)
                    return 1;
                else if (user2 instanceof Private)
                    return -1;

                float distance1 = (user1 instanceof PublicAuthority) ? ((PublicAuthority) user1).getDistance() : ((Veterinarian) user1).getDistance();
                float distance2 = (user2 instanceof PublicAuthority) ? ((PublicAuthority) user2).getDistance() : ((Veterinarian) user2).getDistance();

                return Float.compare(distance1, distance2);
            }
        };

        Collections.sort(veterinarianAuthoritiesList, distanceComparator);
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                ArrayList<User> filteredList = new ArrayList<>();
                for (User user : arraylist) {
                    String companyName = user.getName().toLowerCase();
                    if (companyName.contains(filterPattern))
                        filteredList.add(user);
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                veterinarianAuthoritiesList.clear();

                if (charSequence.length() == 0)
                    veterinarianAuthoritiesList.addAll(arraylist);
                else
                    veterinarianAuthoritiesList.addAll((ArrayList<User>) filterResults.values);

                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public VeterinarianAuthoritiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout;

        if(viewType==2){
            layout= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_bar_list_item,parent,false);
        }
        else{
            layout= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.profile_list_item,parent,false);
        }

        return new VeterinarianAuthoritiesViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull VeterinarianAuthoritiesViewHolder holder, int position) {
        if(getItemViewType(position)==0){
            holder.bind((Veterinarian)  veterinarianAuthoritiesList.get(position));
        }
        else if(getItemViewType(position)==1){
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
            return viewType.VETERINARIAN.ordinal();
        }
        else if(veterinarianAuthoritiesList.get(position) instanceof PublicAuthority){
            return viewType.PUBLIC_AUTHORITY.ordinal();
        }
        else{
            return viewType.PROGRESS_BAR.ordinal(); //little trick for the progress bar ;)
        }
    }

    @Override
    public void OnItemClick(int position) {
        if(this.onProfileClicked!=null){
            this.onProfileClicked.OnProfileClicked(this.veterinarianAuthoritiesList.get(position));
        }
    }
}
