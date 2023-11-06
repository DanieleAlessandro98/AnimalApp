package it.uniba.dib.sms222334.Presenters;

import com.google.firebase.firestore.GeoPoint;

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
import it.uniba.dib.sms222334.Models.Veterinarian;
import it.uniba.dib.sms222334.Utils.AnimalSpecies;
import it.uniba.dib.sms222334.Utils.RequestType;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Utils.Validations;

public class RequestPresenter {
    private final HomeFragment requestFragment;

    public RequestPresenter(HomeFragment requestFragment) {
        this.requestFragment = requestFragment;
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

        if (!Validations.isValidDescription(description)) {
            requestFragment.showInvalidRequestDescription();
            return;
        }

        if (type == RequestType.OFFER_BEDS && !Validations.isValidBedsRequest(beds)) {
            requestFragment.showInvalidRequestBeds();
            return;
        }

        AnimalSpecies species = AnimalSpecies.values()[selectedPositionAnimalSpecies];

        int bedsValue;
        try {
            bedsValue = Integer.parseInt(beds);
        } catch (NumberFormatException e) {
            bedsValue = 0;
        }

        GeoPoint position;
        User user = SessionManager.getInstance().getCurrentUser();

        if (user.getRole() == UserRole.VETERINARIAN)
            position = ((Veterinarian) user).getLocation();
        else if (user.getRole() == UserRole.PUBLIC_AUTHORITY)
            position = ((PublicAuthority) user).getLocation();
        else
            position = new GeoPoint(0, 0);  //Perchè privato non ha residenza?

        Request requestModel = Request.Builder.create("",
                        SessionManager.getInstance().getCurrentUser(),
                        type,
                        description,
                        position
                )
                .setAnimalSpecies(species)
                .setAnimal(animal)
                .setNBeds(bedsValue)
                .build();

        requestModel.createRequest(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                requestFragment.showRequestCreateSuccessful();
                requestFragment.loadReportsAndRequests();
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {
                requestFragment.showRequestCreateError();
            }
        });
    }

    public void getRequestList(DatabaseCallbackResult callback) {
        SessionManager session = SessionManager.getInstance();
        if (!session.isLogged() || (session.isLogged() && session.getCurrentUser().getRole() != UserRole.VETERINARIAN)) {
            boolean hideOfferBedsRequest = session.isLogged() && session.getCurrentUser().getRole() == UserRole.PUBLIC_AUTHORITY;

            RequestDao requestDao = new RequestDao();
            requestDao.getAllRequests(callback, hideOfferBedsRequest);
        }
    }

    public void delete(Request requestReport) {
        requestReport.deleteRequest(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                boolean resultDelete = (boolean) result;
                if (resultDelete)
                    requestFragment.showDocumentDeleteSuccessful(requestReport);
                else
                    requestFragment.showDocumentDeleteError(requestReport);
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {

            }
        });
    }

    public void onEdit(Request request, String description, int selectedPositionAnimalSpecies, String beds) {
        if (selectedPositionAnimalSpecies < 0 || selectedPositionAnimalSpecies >= AnimalSpecies.values().length)
            return;

        if (!Validations.isValidDescription(description)) {
            requestFragment.showInvalidRequestDescription();
            return;
        }

        if (request.getType() == RequestType.OFFER_BEDS && !Validations.isValidBedsRequest(beds)) {
            requestFragment.showInvalidRequestBeds();
            return;
        }

        AnimalSpecies species = AnimalSpecies.values()[selectedPositionAnimalSpecies];

        int bedsValue;
        try {
            bedsValue = Integer.parseInt(beds);
        } catch (NumberFormatException e) {
            bedsValue = 0;
        }

        request.setDescription(description);
        request.setAnimalSpecies(species);
        request.setNBeds(bedsValue);

        request.updateRequest(new DatabaseCallbackResult() {
            @Override
            public void onDataRetrieved(Object result) {
                requestFragment.showRequestUpdateSuccessful(request);
            }

            @Override
            public void onDataRetrieved(ArrayList results) {

            }

            @Override
            public void onDataNotFound() {

            }

            @Override
            public void onDataQueryError(Exception e) {
                requestFragment.showRequestUpdateError();
            }
        });
    }
}
