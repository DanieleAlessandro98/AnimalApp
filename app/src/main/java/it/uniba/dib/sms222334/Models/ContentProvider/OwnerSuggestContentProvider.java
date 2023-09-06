package it.uniba.dib.sms222334.Models.ContentProvider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import it.uniba.dib.sms222334.Database.AnimalAppDB;
import it.uniba.dib.sms222334.Presenters.UserPresenter;
import it.uniba.dib.sms222334.Database.DatabaseCallbackResult;
import it.uniba.dib.sms222334.Models.Owner;
import it.uniba.dib.sms222334.Models.Private;
import it.uniba.dib.sms222334.Models.PublicAuthority;

public class OwnerSuggestContentProvider extends ContentProvider implements DatabaseCallbackResult<Owner>, Runnable{

    private final String TAG="OwnerSuggestContentProvider";
    public static final String PATH="owner_";
    public static final String MIME_TYPE_DIR= ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd."+PATH;

    public static final Uri CONTENT_URI=Uri.parse(ContentResolver.SCHEME_CONTENT+"://"
            +AnimalAppDB.AUTHORITY+"/"+PATH);

    private static final UriMatcher URI_MATCHER=new UriMatcher(UriMatcher.NO_MATCH);

    private static final int OWNER_SEARCH=0;

    private String emailSelection;

    static
    {
         URI_MATCHER.addURI(AnimalAppDB.AUTHORITY, PATH+"/"+SearchManager.SUGGEST_URI_PATH_QUERY,OWNER_SEARCH);
    }

    private final ArrayList<Owner> list=new ArrayList<>();

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        if (URI_MATCHER.match(uri) == OWNER_SEARCH) {

            if(selectionArgs[0].isEmpty())
                return null;

            this.emailSelection=selectionArgs[0];

            Thread getSuggestList= new Thread(this);

            getSuggestList.start();

            try {
                getSuggestList.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            MatrixCursor cursor = list.isEmpty()?null:new MatrixCursor(CursorOwnerAdapter.COLUMNS);

            int id=0;

            for (Owner owner : list) {

                if(owner instanceof Private){
                    cursor.addRow(new Object[]{id
                            ,((Private)owner).getEmail()
                            ,((Private)owner).getPhoto()});

                    Log.d(TAG,((Private)owner).getEmail());
                }
                else if(owner instanceof PublicAuthority) {
                    cursor.addRow(new Object[]{id
                            ,((PublicAuthority)owner).getEmail()
                            ,((PublicAuthority)owner).getPhoto()});

                    Log.d(TAG,((PublicAuthority)owner).getEmail());
                }

                id++;
            }

            //if(cursor!=null)
              //  cursor.setNotificationUri(getContext().getContentResolver(),CONTENT_URI);


            list.clear();

            return cursor;
        }

        throw new IllegalArgumentException("This uri "+uri+" is not accepted!");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (URI_MATCHER.match(uri) == OWNER_SEARCH) {
            return MIME_TYPE_DIR;
        }
        throw new IllegalArgumentException("This uri is not accepted!");
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public void onDataRetrieved(Owner result) {

    }

    @Override
    public void onDataRetrieved(ArrayList<Owner> results) {
        this.list.addAll(results);
    }

    @Override
    public void onDataNotFound() {

    }

    @Override
    public void onDataQueryError(Exception e) {

    }

    @Override
    public void run() {
        UserPresenter.getOwnerList(emailSelection, this);
    }
}
