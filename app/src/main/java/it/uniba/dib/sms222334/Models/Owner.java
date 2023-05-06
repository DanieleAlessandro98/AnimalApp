package it.uniba.dib.sms222334.Models;

import java.util.ArrayList;
import java.util.LinkedList;

public class Owner extends Document{
    private LinkedList<Animal> ListAnimal;
    private LinkedList<Expense> ListExpense;


    public void addAnimal(Animal animal){
        this.ListAnimal.add(animal);
    }

    public void removeAnimal(Animal animal){
        for(Animal a: ListAnimal){
            if(a.getFirebaseID().compareTo(animal.getFirebaseID())==0){
                ListAnimal.remove(a);
            }
        }
    }

    public void addExpense(Expense Expense){
        this.ListExpense.add(Expense);
    }

    public void removeExpense(Expense Expense){
        for(Expense a: ListExpense){
            if(a.getFirebaseID().compareTo(Expense.getFirebaseID())==0){
                ListExpense.remove(a);
            }
        }
    }
}
