package it.uniba.dib.sms222334.Models;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.LinkedList;

public interface Owner {
    void addAnimal(Animal animal);
    void removeAnimal(Animal animal);

    void updateAnimal(Animal animal, boolean profilePictureFlag);
    void addExpense(Expense Expense);
    void removeExpense(Expense Expense);

    ArrayList<Animal> getAnimalList();

    ArrayList<Expense> getExpenseList();
}
