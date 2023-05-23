package it.uniba.dib.sms222334.Views.RecycleViews.Expences;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Models.Document;
import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Visit;
import it.uniba.dib.sms222334.R;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseViewHolder>{

    ArrayList<Expense> expenseList;
    Context context;

    public ExpenseAdapter(ArrayList<Expense> mModel,Context context){
        this.expenseList = mModel;
        this.context=context;
    }

    public void removeExpense(int pos) {
        this.expenseList.remove(pos);
        notifyItemRemoved(pos);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View layout= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.expense_list_item,parent,false);

        return new ExpenseViewHolder(layout,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        holder.bind((Expense) expenseList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.expenseList.size();
    }
}
