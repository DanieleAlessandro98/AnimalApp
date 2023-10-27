package it.uniba.dib.sms222334.Database.Dao.Animal;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Expense;

public class ExpenseDao {
    public static final CollectionReference collectionExpense= FirebaseFirestore.getInstance().collection(AnimalAppDB.Expense.TABLE_NAME);

    public void deleteExpense(Expense expense, @Nullable AnimalCallbacks.eliminationCallback callback){
        collectionExpense.document(expense.getFirebaseID()).delete()
                .addOnSuccessListener(command -> {
                    if (callback!=null)
                        callback.eliminatedSuccesfully();
                })
                .addOnFailureListener(e -> {
                    if (callback!=null)
                        callback.failedElimination();
                });
    }

    public void createExpense(Expense expense,AnimalCallbacks.creationCallback callback){
        Map<String, Object> new_expense = new HashMap<>();
        new_expense.put(AnimalAppDB.Expense.COLUMN_NAME_ANIMAL, AnimalDao.collectionAnimal.document(expense.getAnimalID()));
        new_expense.put(AnimalAppDB.Expense.COLUMN_NAME_CATEGORY, expense.getCategory().ordinal());
        new_expense.put(AnimalAppDB.Expense.COLUMN_NAME_NOTE, expense.getNote());
        new_expense.put(AnimalAppDB.Expense.COLUMN_NAME_PRICE, expense.getPrice());

        collectionExpense.add(new_expense)
                .addOnSuccessListener(snapshot -> {
                    expense.setFirebaseID(snapshot.getId());
                    callback.createdSuccesfully();
                })
                .addOnFailureListener(command -> callback.failedCreation());
    }
}
