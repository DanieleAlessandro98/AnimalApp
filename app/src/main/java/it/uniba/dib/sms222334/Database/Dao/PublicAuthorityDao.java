package it.uniba.dib.sms222334.Database.Dao;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Models.PublicAuthority;

public class PublicAuthorityDao {
    private final String TAG="PublicAuthorityDao";
    final private CollectionReference collectionPublicAuthority = FirebaseFirestore.getInstance().collection(AnimalAppDB.PublicAuthority.TABLE_NAME);

    public PublicAuthority findPublicAuthority(DocumentSnapshot document) {
        PublicAuthority.Builder public_authority_requested_builder=PublicAuthority.Builder.
                create(
                        document.getId(),
                        document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_COMPANY_NAME),
                        document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_EMAIL),
                        document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_PASSWORD),
                        document.getLong(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHONE_NUMBER).intValue(),
                        null)  //TODO: document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_PHOTO))
                .setLegalSite(document.getString(AnimalAppDB.PublicAuthority.COLUMN_NAME_SITE))
                //.setLatitude(document.getDouble(AnimalAppDB.PublicAuthority.COLUMN_NAME_BIRTH_DATE)) // TODO: Langitude
                //.setLongitude(document.getDouble(AnimalAppDB.PublicAuthority.COLUMN_NAME_BIRTH_DATE)) // TODO: Longitude
                .setNBeds(document.getLong(AnimalAppDB.PublicAuthority.COLUMN_NAME_BEDS_NUMBER).intValue());

        return public_authority_requested_builder.build();
    }
}
