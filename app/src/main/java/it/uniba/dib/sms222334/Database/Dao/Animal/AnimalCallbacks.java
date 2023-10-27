package it.uniba.dib.sms222334.Database.Dao.Animal;

import javax.annotation.Nullable;

import it.uniba.dib.sms222334.Models.Expense;
import it.uniba.dib.sms222334.Models.Food;

public final class AnimalCallbacks {

    public interface alreadyExistCallBack{
        void alreadyExist();
        void notExistYet();
        void queryError(Exception e);
    }

    public interface inputValidate{
        void InvalidName();
        void InvalidBirthDate();
        void InvalidMicrochip();

        void MicrochipAlreadyUsed();
    }

    public interface creationCallback{
        void createdSuccesfully();

        void failedCreation();
    }

    public interface updateCallback{
        void updatedSuccesfully();

        void failedUpdate();
    }

    public interface eliminationCallback{
        void eliminatedSuccesfully();

        void failedElimination();
    }

    public interface foodCallback{
        void notifyFoodLoaded();

        void notifyFoodRemoved(int position);
    }

    public interface expensesCallback{
        void notifyExpensesLoaded();

        void notifyExpensesRemoved(@Nullable int position);
    }
}
