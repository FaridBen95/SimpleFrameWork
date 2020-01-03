package com.farid.framework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Model implements DatabaseListener{
    private Context mContext;
    private String modelName;
    private String caller;
    protected MySqlite sqlite;
    private static String BASE_AUTHORITY = BuildConfig.APPLICATION_ID + ".main_provider";
    private boolean multi;
    private static TransactionsListener transactionsListener;


    public String getModelName() {
        return modelName;
    }

    public Col _id = new Col(Col.ColumnType.integer).setAutoIncrement(true).setSequence(-3);
    public Col enabled = new Col(Col.ColumnType.bool).setSequence(-2);
    public Col write_date = new Col(Col.ColumnType.text).setSequence(-1);
    public Col removed = new Col(Col.ColumnType.bool).setSequence(0);

    private List<Field> fields;

    public Model(Context mContext, String modelName){
        this(mContext, modelName, null, App.create);
    }

    public Model(Context mContext, String modelName, DatabaseErrorHandler databaseErrorHandler, boolean create){
        this.mContext = mContext;
        this.modelName = modelName;
        this.caller = MyUtil.caller();
        sqlite = SQLitesListSingleton.getSQLiteList().sqlites.get(modelName);
        if(sqlite == null && create){
            createTable(databaseErrorHandler);
        }
        App.addSQLite(modelName, sqlite);
//        initColumns();
    }

    public Uri createUri(String authority){
        BASE_AUTHORITY = authority;
        String path = getModelName().toLowerCase(Locale.getDefault());
        return MyBaseProvider.buildURI(BASE_AUTHORITY, path, multi);
    }

    private void createTable(DatabaseErrorHandler databaseErrorHandler) {
        sqlite = new MySqlite(mContext, databaseErrorHandler);
        sqlite.getWritableDatabase();
    }

    private void initColumns(){
        try {this.fields = new ArrayList<>();
            List<Field> fields = new ArrayList<>();
            boolean unassignFromModel = unAssigneFromModel();
            if(!unassignFromModel) {
                fields.addAll(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
            }else{
                fields.add(getClass().getSuperclass().getField("_id"));
            }
            fields.addAll(Arrays.asList(getClass().getDeclaredFields()));
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(Col.class)) {
                    Col col = (Col) field.get(this);
                    col.setName(field.getName());
                    this.fields.add(field);
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public List<Col> getColumns(){
        initColumns();
        List<Col> columns = new ArrayList<>();
        Col col = new Col();
        for(Field field : fields){
            columns.add(getColumn(field));
        }
        return columns;
    }


    public Col getColumn(Field field){
        try {
            return (Col) field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SQLiteDatabase getReadableDatabase(){
        return App.getDB(this.modelName, false);
    }

    public SQLiteDatabase getWritableDatabase(){
        return App.getDB(this.modelName, true);
    }

    public void startTransaction(DatabaseObserver databaseObserver){
        SQLUtil.startTransaction(getWritableDatabase(), databaseObserver);
    }

    //take effect after creating a table so it will be used after first run or after upgrading database
    @Override
    public boolean unAssigneFromModel() {
        return false;
    }

    //take effect after creating a table so it will be used after first run or after upgrading database
    @Override
    public boolean sortableColumns() {
        return true;
    }

    public Cursor simpleSelect(String selection, String[] selectionArgs){
        return simpleSelect(null, selection, selectionArgs, "");
    }

    public Cursor simpleSelect(String[] projections, String selection, String[] selectionArgs, String sort){
        return mContext.getContentResolver().query(createUri(BASE_AUTHORITY), projections, selection, selectionArgs, sort);
    }

    public int insert(Values values){
        Uri uri = mContext.getContentResolver().insert(createUri(BASE_AUTHORITY), values.toContentValues());
        if (uri != null) {
            return Integer.parseInt(uri.getLastPathSegment());
        }
        return -1;
    }

    public int update(int row_id, Values values){
        return update(values, Col.ID + " = ? ", new String[]{String.valueOf(row_id)});
    }

    private int update(@Nullable Values values, @Nullable String selection, @Nullable String[] selectionArgs){
        assert values != null;
        return mContext.getContentResolver().update(createUri(BASE_AUTHORITY), values.toContentValues(), selection, selectionArgs);
    }

    public int delete(@Nullable String selection, @Nullable String[] selectionArgs){
        return mContext.getContentResolver().delete(createUri(BASE_AUTHORITY), selection, selectionArgs);
    }

    //for inserting or updating multi rows at a time this should be called with multi = true and it will automatically desactivate after the transaction
    public void setMultiTransactions(boolean multi){
        this.multi = multi;
    }

    public List<Values> getRows(String selection, String[] selectionArgs){
        Cursor cursor = simpleSelect(selection, selectionArgs);
        List<Values> allValues = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                Values values = CursorUtils.toValues(cursor);
                allValues.add(values);
            }while(cursor.moveToNext());
        }
        return allValues;
    }

    public void setTransactionsListener(TransactionsListener transactionsListener) {
        this.transactionsListener = transactionsListener;
    }

    public TransactionsListener getTransactionsListener() {
        return transactionsListener;
    }

    public Model createInstance(Class<?> classType) {
        try {
            Constructor<?> constructor = classType.getConstructor(Context.class);
            return (Model) constructor.newInstance(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}