package it.uniba.dib.sms222334.Views.RecycleViews.SimpleText;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms222334.Models.Food;
import it.uniba.dib.sms222334.Models.Pathology;
import it.uniba.dib.sms222334.Models.Relation;
import it.uniba.dib.sms222334.R;

public class SimpleTextViewHolder<T> extends RecyclerView.ViewHolder{
        private static final String TAG="SimpleTextViewHolder";

        TextView text;

        public SimpleTextViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
        }

        public void bind(T t){
            if(t instanceof Pathology){
                this.text.setText(((Pathology) t).getName());
            } else if (t instanceof Food) {
                this.text.setText(((Food) t).getName());
            }
            else{
                throw new IllegalArgumentException("Invalid Element for bind SimpleTextViewHolder");
            }
        }
}
