package com.farid.framework;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyBaseProvider extends ContentProvider {
    public final static String KEY_MODEL = "key_model";
    private Model model;
    private Context context;

    public MyBaseProvider(){
        context = getContext();
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] baseProjection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        context = getContext();
        model = getModel(uri);
        String[] projection = validateProjection(baseProjection);
        Cursor cursor = generateSelectQuery(projection, selection, selectionArgs, sortOrder);
        cursor.setNotificationUri(context.getContentResolver(), uri);
        return cursor;
    }

    private Cursor generateSelectQuery(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder query = new SQLiteQueryBuilder();
        query.setTables(model.getModelName());
        return query.query(model.getReadableDatabase(), projection, selection, selectionArgs,
                null, null, sortOrder);
    }

    private String[] validateProjection(String[] baseProjection) {
        List<String> projection = new ArrayList<>();
        if(baseProjection == null){
            projection.add("*");
        }else{
            projection.add(Col.ID);
            if(!model.unAssigneFromModel()){
                projection.add(Col.WRITE_DATE);
                projection.add(Col.ENABLED);
                projection.add(Col.REMOVED);
            }
        }
        return projection.toArray(new String[projection.size()]);
    }

    private Model getModel(Uri uri) {
        String modelName = uri.getQueryParameter(KEY_MODEL);
        return App.getModel(context, modelName);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
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

    public static Uri buildURI(String authority, String model) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.appendPath(model);
        uriBuilder.appendQueryParameter(KEY_MODEL, model);
        uriBuilder.scheme("content");
        return uriBuilder.build();
    }
}
