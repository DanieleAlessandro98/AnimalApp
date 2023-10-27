package it.uniba.dib.sms222334.Database.Dao.Animal;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Food;

public class FoodDao {
    public static final CollectionReference collectionFood= FirebaseFirestore.getInstance().collection(AnimalAppDB.Food.TABLE_NAME);

    public void deleteFood(Food food,@Nullable AnimalCallbacks.eliminationCallback callback){
        collectionFood.document(food.getFirebaseID()).delete()
                .addOnSuccessListener(command -> {
                    if(callback!=null)
                        callback.eliminatedSuccesfully();
                })
                .addOnFailureListener(e -> {
                    if(callback!=null)
                        callback.failedElimination();
                });
    }

    public void createFood(Food food,AnimalCallbacks.creationCallback callback){
        Map<String, Object> new_food = new HashMap<>();
        new_food.put(AnimalAppDB.Food.COLUMN_NAME_ANIMAL, AnimalDao.collectionAnimal.document(food.getAnimalID()));
        new_food.put(AnimalAppDB.Food.COLUMN_NAME_NAME, food.getName());

        collectionFood.add(new_food)
                .addOnSuccessListener(snapshot -> {
                    food.setFirebaseID(snapshot.getId());
                    callback.createdSuccesfully();
                })
                .addOnFailureListener(command -> callback.failedCreation());
    }
}
