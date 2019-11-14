package com.farid.framework;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.renderscript.Sampler;
import android.view.Display;

import java.lang.reflect.Field;
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


    public String getModelName() {
        return modelName;
    }

    public Col id = new Col(Col.ColumnType.integer).setAutoIncrement(true).setSequence(-3);
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
        return createUri(authority, false);
    }

    public Uri createUri(String authority, boolean multi){
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
                fields.add(getClass().getSuperclass().getField("id"));
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

    @Override
    public boolean unAssigneFromModel() {
        return false;
    }

    @Override
    public boolean sortableColumns() {
        return true;
    }

    public Cursor simpleSelect(){
        return mContext.getContentResolver().query(createUri(BASE_AUTHORITY), null, "", null, "");
    }

    public void insert(Values values, boolean multi){
        mContext.getContentResolver().insert(createUri(BASE_AUTHORITY, multi), values.toContentValues());
    }
}
