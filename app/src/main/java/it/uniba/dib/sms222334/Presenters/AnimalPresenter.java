package it.uniba.dib.sms222334.Presenters;

import android.widget.Toast;

import java.util.Date;

import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalDao;
import it.uniba.dib.sms222334.Database.Dao.Animal.AnimalCallbacks;
import it.uniba.dib.sms222334.Fragmets.AnimalFragment;
import it.uniba.dib.sms222334.Fragmets.ListFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.DateUtilities;
import it.uniba.dib.sms222334.Views.AnimalAppDialog;

public class AnimalPresenter implements AnimalCallbacks.alreadyExistCallBack,
                                        AnimalCallbacks.creationCallback ,
                                        AnimalCallbacks.eliminationCallback,
                                        AnimalCallbacks.updateCallback{

    AnimalDao animalDao;
    AnimalAppDialog dialog;

    boolean editFlag=false;
    boolean profilePictureFlag=false;
    private Animal animal;

    AnimalFragment animalFragment;


    public AnimalPresenter(AnimalAppDialog dialog){
        animalDao= new AnimalDao();
        this.dialog=dialog;
    }

    public AnimalPresenter(AnimalAppDialog dialog,AnimalFragment animalFragment){
        animalDao= new AnimalDao();
        this.animalFragment=animalFragment;
        this.dialog=dialog;
    }

    public AnimalPresenter(){
        animalDao= new AnimalDao();
    }

    public void addAnimal(Animal animal) {
        this.animal=animal;

        if(validateInput())
            validateMicroChip(animal.getMicrochip());
    }

    public void editAnimal(Animal animal, String oldMicroChip,boolean profilePictureFlag) {
        this.animal=animal;
        this.profilePictureFlag=profilePictureFlag;

        editFlag=true;

        if(validateInput()){
            if(oldMicroChip.compareTo(animal.getMicrochip()) != 0)
                validateMicroChip(animal.getMicrochip());
            else
                this.animalDao.editAnimal(animal,this,profilePictureFlag);
        }

    }

    public void deleteAnimal(Animal animal) {
        this.animalDao.deleteAnimal(animal,this);
    }

    private boolean validateInput() {
        if (!isValidName(animal.getName())) {
            dialog.InvalidName();
            return false;
        }

        if (!isValidBirthDate(animal.getBirthDate())) {
            dialog.InvalidBirthDate();
            return false;
        }

        if(animal.getMicrochip().length()<15){
            dialog.InvalidMicrochip();
            return false;
        }

        return true;
    }

    private void validateMicroChip(String microchip){
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
        if(!editFlag)
            this.animalDao.createAnimal(animal,this);
        else
            this.animalDao.editAnimal(animal,this,this.profilePictureFlag);
    }

    @Override
    public void queryError(Exception e) {

    }

    @Override
    public void createdSuccesfully() {
        if(dialog!=null)
            dialog.cancel();
    }

    @Override
    public void failedCreation() {

    }

    @Override
    public void eliminatedSuccesfully() {
        if(dialog!=null)
            dialog.cancel();
    }

    @Override
    public void failedElimination() {

    }

    @Override
    public void updatedSuccesfully() {
        if(dialog!=null)
            dialog.cancel();

        if(animalFragment!=null){
            this.animalFragment.refresh(animal);
        }


    }

    @Override
    public void failedUpdate() {

    }
}
