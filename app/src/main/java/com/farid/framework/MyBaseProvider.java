package com.farid.framework;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyBaseProvider extends ContentProvider {
    public final static String KEY_MODEL = "key_model";
    public final static String KEY_TYPE = "single";
    private static boolean multi = false;
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
        String type = uri.getQueryParameter(KEY_TYPE);
        assert type != null;
        multi = type.equals("multi");
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
        context = getContext();
        model = getModel(uri);
        ContentValues validatedValues = validateValues(values);
        SQLiteDatabase db = model.getWritableDatabase();
        long new_id = db.insert(model.getModelName(), null, validatedValues);
        return Uri.withAppendedPath(uri, new_id + "");
    }

    private ContentValues validateValues(ContentValues values) {
        ContentValues contentValues = values;
        if(!model.unAssigneFromModel()){
            if(!values.containsKey("enabled")){
                contentValues.put("enabled", true);
                contentValues.put("removed", false);
                contentValues.put("write_date", MyUtil.getCurrentDate());
            }
        }
        return contentValues;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    public static Uri buildURI(String authority, String model, boolean multi) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.appendPath(model);
        uriBuilder.appendQueryParameter(KEY_MODEL, model);
        String type = multi? "multi" : "single";
        uriBuilder.appendQueryParameter(KEY_TYPE, type);
        uriBuilder.scheme("content");
        return uriBuilder.build();
    }
}
