package it.uniba.dib.sms222334.Presenters;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Database.Dao.Animal.FoodDao;
import it.uniba.dib.sms222334.Models.Food;

public class FoodPresenter {

    public boolean addFood(Food food, AnimalCallbacks.creationCallback callback){
        if(food.getName().length() <= 1)
            return false;

        new FoodDao().createFood(food, callback);

        return true;
    }
}
