package it.uniba.dib.sms222334.Views.RecycleViews.Expences;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;

import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.R;

public class ExpenseViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG="ExpencesViewHolder";

        TextView expenseType,note,price;

        Context context;

        String[] expenseTypeArray;


        public ExpenseViewHolder(@NonNull View itemView,Context context) {
            super(itemView);

            this.context=context;

            expenseTypeArray = context.getResources().getStringArray(R.array.expense_categories);

            expenseType=itemView.findViewById(R.id.expense_type);
            note=itemView.findViewById(R.id.note);
            price=itemView.findViewById(R.id.price);
        }

        public void bind(Expense expense){
             this.expenseType.setText(expenseTypeArray[expense.getCategory().ordinal()]);

             this.note.setText(expense.getnote());

             this.price.setText(expense.getPrice()+"€");
        }
}
