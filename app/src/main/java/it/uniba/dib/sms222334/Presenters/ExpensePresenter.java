package it.uniba.dib.sms222334.Presenters;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.Animal.ExpenseDao;
import it.uniba.dib.sms222334.Models.Expense;

public class ExpensePresenter {

    public void addExpense(Expense expense, AnimalCallbacks.creationCallback callback){

        new ExpenseDao().createExpense(expense, callback);
    }
}
