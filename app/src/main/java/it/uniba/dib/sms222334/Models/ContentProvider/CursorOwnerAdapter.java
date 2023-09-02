package it.uniba.dib.sms222334.Models.ContentProvider;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.BitmapFactory;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;

import it.uniba.dib.sms222334.R;

public class CursorOwnerAdapter extends CursorAdapter {
    private final String TAG="CursorOwnerAdapter";
    public static final String[] COLUMNS = new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,SearchManager.SUGGEST_COLUMN_ICON_1};

    public CursorOwnerAdapter(Context context){
        super(context,null,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.owner_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name=view.findViewById(R.id.name);
        ImageView photo=view.findViewById(R.id.profile_photo);

        name.setText(cursor.getString(cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1)));
        photo.setImageBitmap(BitmapFactory.decodeByteArray(cursor.getBlob(
                cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_ICON_1)
        ),0,cursor.getBlob(2).length));
    }
}
