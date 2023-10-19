package it.uniba.dib.sms222334.Presenters;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222334.Database.Dao.RequestDao;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Fragmets.HomeFragment;
import it.uniba.dib.sms222334.Models.Animal;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;
import it.uniba.dib.sms222334.Models.Request;
import it.uniba.dib.sms222334.Models.SessionManager;
import it.uniba.dib.sms222334.Models.User;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.RequestType;

public class RequestPresenter {
    private final HomeFragment reportFragment;

    public RequestPresenter(HomeFragment reportFragment) {
        this.reportFragment = reportFragment;
    }

    public List<Animal> getMyAnimalNames() {
        User user = SessionManager.getInstance().getCurrentUser();
        ArrayList<Animal> myAnimalNames = new ArrayList<>();

        if (!SessionManager.getInstance().isLogged())
            return myAnimalNames;

        switch (user.getRole()) {
            case PRIVATE:
                for (Animal animal : ((Private) user).getAnimalList())
                    myAnimalNames.add(animal);
                break;

            case PUBLIC_AUTHORITY:
                for (Animal animal : ((PublicAuthority) user).getAnimalList())
                    myAnimalNames.add(animal);
                break;

            default:
                break;
        }

        return myAnimalNames;
    }

    public void onAdd(RequestType type, String description, int selectedPositionAnimalSpecies, Animal animal, String beds) {
        if (selectedPositionAnimalSpecies < 0 || selectedPositionAnimalSpecies >= AnimalSpecies.values().length)
            return;

        AnimalSpecies species = AnimalSpecies.values()[selectedPositionAnimalSpecies];

        int bedsValue;
        try {
            bedsValue = Integer.parseInt(beds);
        } catch (NumberFormatException e) {
            bedsValue = 0;
        }

        Request requestModel = Request.Builder.create("", SessionManager.getInstance().getCurrentUser(), type, description)
                .setAnimalSpecies(species)
                .setAnimal(animal)
                .setNBeds(bedsValue)
                .build();
        requestModel.createRequest(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                reportFragment.showRequestCreateSuccessful();
                reportFragment.loadReportsAndRequests();
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {
                reportFragment.showRequestCreateError();
            }
        });
    }

    public void getRequestList(DatabaseCallbackResult callback) {
        RequestDao requestDao = new RequestDao();
        requestDao.getAllRequests(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {

            }

            @Override
            public void onDataRetrieved(ArrayList results) {
                callback.onDataRetrieved(results);
            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {

            }
        });
    }

}
