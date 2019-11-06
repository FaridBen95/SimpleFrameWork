package com.farid.framework;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.view.Display;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Model {
    private Context mContext;
    private String modelName;
    private String caller;
    protected MySqlite sqlite;


    public String getModelName() {
        return modelName;
    }

    Col id = new Col(Col.ColumnType.integer);
    Col enabled = new Col(Col.ColumnType.bool);
    Col removed = new Col(Col.ColumnType.bool);

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

    private void createTable(DatabaseErrorHandler databaseErrorHandler) {
        sqlite = new MySqlite(mContext, databaseErrorHandler);
        sqlite.getWritableDatabase();
    }

    private void initColumns(){
        try {this.fields = new ArrayList<>();
            List<Field> fields = new ArrayList<>();
            fields.addAll(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
            fields.addAll(Arrays.asList(getClass().getDeclaredFields()));
            for (Field field : fields) {
                if (field.getType().isAssignableFrom(Col.class)) {
                    Col col = (Col) field.get(this);
                    col.setName(field.getName());
                    this.fields.add(field);
                }
            }
        } catch (IllegalAccessException e) {
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
}
