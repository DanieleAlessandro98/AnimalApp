package it.uniba.dib.sms222334.Views.RecycleViews.Relation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.R;

public class RelationAdapter extends RecyclerView.Adapter<RelationViewHolder>{

    ArrayList<Relation> relationList;
    Context context;

    public RelationAdapter(ArrayList<Relation> mModel,Context context){
        this.relationList = mModel;
        this.context=context;
    }

    public void removeRelation(int pos) {
        this.relationList.remove(pos);
        notifyItemRemoved(pos);
    }

    public ArrayList<Relation> getRelationList() {
        return relationList;
    }

    @NonNull
    @Override
    public RelationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.relation_item_list,parent,false);

        return new RelationViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull RelationViewHolder holder, int position) {
        holder.bind((Relation) relationList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.relationList.size();
    }
}
