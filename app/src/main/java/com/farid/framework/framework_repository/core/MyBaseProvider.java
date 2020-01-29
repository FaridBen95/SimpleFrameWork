package com.farid.framework.framework_repository.core;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.farid.framework.framework_repository.Utils.MyUtil;

import java.util.ArrayList;
import java.util.List;

public class MyBaseProvider extends ContentProvider {
    public final static String KEY_MODEL = "key_model";
    public final static String KEY_TYPE = "single";
    private static boolean multi = false;
    private Model model;
    private Context context;
    private DatabaseListener.TransactionsListener transactionsListener;

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
        Model model = App.getModel(context, modelName);
        assert model != null;
        transactionsListener = model.getTransactionsListener();
        return model;
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
        if(transactionsListener != null){
            transactionsListener.onPreInsert(new Values().getValuesFrom(validatedValues));
        }
        long new_id = db.insert(model.getModelName(), null, validatedValues);
        notifyDataChange(uri);
        return Uri.withAppendedPath(uri, new_id + "");
    }

    private ContentValues validateValues(ContentValues values) {
        ContentValues contentValues = new ContentValues();
        contentValues.putAll(values);
        List<String> baseModelsKeys = new ArrayList<>();
        baseModelsKeys.add(Col.ID);
        if(!model.unAssigneFromModel()){
            baseModelsKeys.add(Col.REMOVED);
            baseModelsKeys.add(Col.ENABLED);
            baseModelsKeys.add(Col.WRITE_DATE);
            if(!values.containsKey(Col.ENABLED)) {
                contentValues.put(Col.ENABLED, 1);
            }
            if(!values.containsKey(Col.REMOVED)) {
                contentValues.put(Col.REMOVED, 1);
            }
            if(!values.containsKey(Col.WRITE_DATE)) {
                contentValues.put(Col.WRITE_DATE, MyUtil.getCurrentDate());
            }
        }
        for(Col col : model.getColumns()){
            if(!baseModelsKeys.contains(col.getName()) && !values.containsKey(col.getName())) {
                Object object = col.getDefaultValue();
                if (object.getClass().isAssignableFrom(Integer.class)) {
                    int i = Integer.valueOf(object.toString());
                    contentValues.put(col.getName(), i);
                } else if (object.getClass().isAssignableFrom(Boolean.class)) {
                    int b = object.equals(true) ? 1 : 0;
                    contentValues.put(col.getName(), b);
                } else {
                    contentValues.put(col.getName(), object.toString());
                }
            }else{
                contentValues.remove(Col.ID);
            }
        }
        return contentValues;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        context = getContext();
        model = getModel(uri);
        SQLiteDatabase db = model.getWritableDatabase();
        notifyDataChange(uri);
        if(transactionsListener != null){
            transactionsListener.onPreDelete(generateSelectionConditions(selection, selectionArgs));
        }
        return db.delete(model.getModelName(), selection, selectionArgs);
    }

    private String generateSelectionConditions(String selection, String[] selectionArgs) {
        String selectionConditions = selection;
        int i = 0;
        while (selectionConditions.contains("?")) {
            selectionConditions = selectionConditions.replaceFirst("\\?", selectionArgs[i]);
            i++;
        }
        return selectionConditions;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        context = getContext();
        model = getModel(uri);
        ContentValues valuesToUpdate = validateValues(values);
        SQLiteDatabase db = model.getWritableDatabase();
        notifyDataChange(uri);
        if(transactionsListener != null){
            transactionsListener.onPreUpdate(new Values().getValuesFrom(valuesToUpdate), generateSelectionConditions(selection, selectionArgs));
        }
        return db.update(model.getModelName(), valuesToUpdate, selection, selectionArgs);
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


    private void notifyDataChange(Uri uri) {
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null);
    }
}
