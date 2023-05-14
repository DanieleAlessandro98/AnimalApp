package it.uniba.dib.sms222334.Database;

public interface DatabaseCallbackResult<T> {
    void onDataRetrieved(T result);
    void onDataNotFound();
    void onDataQueryError(Exception e);
}
