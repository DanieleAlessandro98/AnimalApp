package it.uniba.dib.sms222334.Views.RecycleViews.Animal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.Segnalation;
import it.uniba.dib.sms222334.R;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalViewHolder> implements  AnimalViewHolder.OnItemClickListener{

    ArrayList<Animal> animalList;
    Context context;

    public interface OnAnimalClicked{
        void OnAnimalClicked(Animal animal);
    }

    private OnAnimalClicked onAnimalClicked;

    public void setOnAnimalClickListener(OnAnimalClicked listener){
        this.onAnimalClicked=listener;
    }

    public AnimalAdapter(ArrayList<Animal> mModel,Context context){
        this.animalList = mModel;
        this.context=context;
    }

    @NonNull
    @Override
    public AnimalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_list_item,parent,false);

        return new AnimalViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalViewHolder holder, int position) {
        holder.bind((Animal) animalList.get(position));
        holder.setOnItemClickListener(this);
    }

    @Override
    public int getItemCount() {
        return this.animalList.size();
    }

    @Override
    public void OnItemClick(int position) {
        if(this.onAnimalClicked!=null){
            this.onAnimalClicked.OnAnimalClicked(this.animalList.get(position));
        }
    }
}
