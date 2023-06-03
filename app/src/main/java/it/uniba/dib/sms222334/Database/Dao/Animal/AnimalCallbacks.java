package it.uniba.dib.sms222334.Database.Dao.Animal;

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
}
