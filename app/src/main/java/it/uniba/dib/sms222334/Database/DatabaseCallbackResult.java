package it.uniba.dib.sms222334.Database;

import java.util.ArrayList;

public interface DatabaseCallbackResult<T> {
    void onDataRetrieved(T result);

    void onDataRetrieved(ArrayList<T> results);
    void onDataNotFound();
    void onDataQueryError(Exception e);
}
