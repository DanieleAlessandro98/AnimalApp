package it.uniba.dib.sms222334.Views.RecycleViews.SimpleText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.R;

public class SimpleTextAdapter<T> extends RecyclerView.Adapter<SimpleTextViewHolder<T>>{

    ArrayList<T> simpleItemList;

    public SimpleTextAdapter(ArrayList<T> mModel){
        this.simpleItemList = mModel;
    }

    public void removeSimpleElement(int pos) {
        this.simpleItemList.remove(pos);
        notifyItemRemoved(pos);
    }

    @NonNull
    @Override
    public SimpleTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_text_item_list,parent,false);

        return new SimpleTextViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleTextViewHolder holder, int position) {
        holder.bind((T) simpleItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.simpleItemList.size();
    }
}
