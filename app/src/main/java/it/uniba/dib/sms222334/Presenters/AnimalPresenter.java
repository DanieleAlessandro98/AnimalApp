package it.uniba.dib.sms222334.Presenters;

import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Fragmets.ListFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;

public class AnimalPresenter implements AnimalCallbacks.alreadyExistCallBack {

    AnimalDao animalDao;
    AnimalAppDialog dialog;

    ListFragment listFragment;


    public AnimalPresenter(AnimalAppDialog dialog){
        animalDao= new AnimalDao();
        this.dialog=dialog;
    }

    public AnimalPresenter(ListFragment fragment){
        animalDao= new AnimalDao();
        this.listFragment=fragment;
    }

    public void addAnimal(Animal animal) {
        if (!isValidName(animal.getName())) {
            dialog.InvalidName();
            return;
        }
        if (!isValidBirthDate(animal.getBirthDate())) {
            dialog.InvalidBirthDate();
            return;
        }

        if(animal.getMicrochip().length()<15){
            dialog.InvalidMicrochip();
            return;
        }

        isValidMicroChip(animal.getMicrochip());
    }

    private void isValidMicroChip(String microchip) {
        this.animalDao.checkAnimalExist(microchip,this);
    }

    private boolean isValidBirthDate(Date birthDate) {
        if (birthDate==null){
            return false;
        }
        return DateUtilities.validateAge(birthDate,0);
    }

    private boolean isValidName(String name) {
        return !name.isEmpty();
    }


    @Override
    public void alreadyExist() {
        dialog.MicrochipAlreadyUsed();
    }

    @Override
    public void notExistYet() {
        //AnimalDao.add()
    }

    @Override
    public void queryError(Exception e) {

    }
}
