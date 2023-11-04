package it.uniba.dib.sms222334.Views.RecycleViews.Visit;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.icu.text.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.R;

public class VisitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private static final String TAG="VisitViewHolder";

        TextView animalName,visitType,visitDate,visitState;

        Context context;

        String[] visitTypeArray,visitStateArray;

        public interface OnItemClickListener{
            void OnItemClick(int position);
        }

        OnItemClickListener onItemClickListener;

        public VisitViewHolder(@NonNull View itemView,Context context) {
            super(itemView);

            this.context=context;

            visitTypeArray = context.getResources().getStringArray(R.array.exam_type);
            visitStateArray = context.getResources().getStringArray(R.array.visit_state);

            animalName=itemView.findViewById(R.id.animal_name);
            visitType=itemView.findViewById(R.id.visit_type);
            visitDate=itemView.findViewById(R.id.visit_date);
            visitState=itemView.findViewById(R.id.visit_state);

            itemView.setOnClickListener(this);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.onItemClickListener= listener;
        }

        public void bind(Visit visit){
             this.animalName.setText(visit.getAnimal().getName());

             this.visitType.setText(visitTypeArray[visit.getType().ordinal()]);


             Date visitDate= visit.getDate().toDate();
             Calendar calendar=Calendar.getInstance();
             calendar.setTime(visitDate);
             this.visitDate.setText(calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.YEAR));


             this.visitState.setText(visitStateArray[visit.getState().ordinal()]);

             switch (visit.getState()){
                 case EXECUTED:
                     this.visitState.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.main_green)));
                     break;
                 case BE_REVIEWED:
                     this.visitState.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.soft_yellow)));
                     break;
                 case NOT_EXECUTED:
                     this.visitState.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.soft_black)));
             }
        }


        @Override
        public void onClick(View view) {
            if(this.onItemClickListener!=null){
                this.onItemClickListener.OnItemClick(getLayoutPosition());
            }
        }
}
