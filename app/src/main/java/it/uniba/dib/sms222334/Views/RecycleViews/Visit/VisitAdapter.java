package it.uniba.dib.sms222334.Views.RecycleViews.Visit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Views.RecycleViews.Animal.AnimalAdapter;

public class VisitAdapter extends RecyclerView.Adapter<VisitViewHolder> implements VisitViewHolder.OnItemClickListener{

    ArrayList<Visit> visitList;
    Context context;
    public interface OnVisitClicked{
        void OnVisitClicked(Visit visit);
    }

    private OnVisitClicked onVisitClicked;

    public void setOnVisitClickListener(OnVisitClicked listener){
        this.onVisitClicked=listener;
    }

    public VisitAdapter(ArrayList<Visit> mModel,Context context){
        this.visitList = mModel;
        this.context=context;
    }

    public void removeVisit(int pos) {
        this.visitList.remove(pos);
        notifyItemRemoved(pos);
    }

    @NonNull
    @Override
    public VisitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.visit_list_item,parent,false);

        return new VisitViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull VisitViewHolder holder, int position) {
        holder.bind((Visit) visitList.get(position));
        holder.setOnItemClickListener(this);
    }

    @Override
    public int getItemCount() {
        return this.visitList.size();
    }

    @Override
    public void OnItemClick(int position) {
        if(this.onVisitClicked!=null){
            this.onVisitClicked.OnVisitClicked(this.visitList.get(position));
        }
    }
}
