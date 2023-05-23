package it.uniba.dib.sms222334.Models;

import java.util.ArrayList;
import java.util.LinkedList;

public class Owner extends Document{
    private LinkedList<Animal> listAnimal;
    private LinkedList<Expense> listExpense;

    public Owner() {
        listAnimal = new LinkedList<>();
        listExpense = new LinkedList<>();
    }

    public void addAnimal(Animal animal){
        this.listAnimal.add(animal);
    }

    public void removeAnimal(Animal animal){
        for(Animal a: listAnimal){
            if(a.getFirebaseID().compareTo(animal.getFirebaseID())==0){
                listAnimal.remove(a);
            }
        }
    }

    public void addExpense(Expense Expense){
        this.listExpense.add(Expense);
    }

    public void removeExpense(Expense Expense){
        for(Expense a: listExpense){
            if(a.getFirebaseID().compareTo(Expense.getFirebaseID())==0){
                listExpense.remove(a);
            }
        }
    }
}
