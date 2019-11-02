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
        this(mContext, modelName, null);
    }

    public Model(Context mContext, String modelName, DatabaseErrorHandler databaseErrorHandler){
        this.mContext = mContext;
        this.modelName = modelName;
        this.caller = MyUtil.caller();
        sqlite = SQLitesListSingleton.getSQLiteList().sqlites.get(modelName);
        if(sqlite == null){
            createTable(databaseErrorHandler);
        }
        App.addSQLite(modelName, sqlite);
//        initColumns();
    }

    private void createTable(DatabaseErrorHandler databaseErrorHandler) {
        sqlite = new MySqlite(mContext, databaseErrorHandler);

    }

    private void initColumns(){
        this.fields = new ArrayList<>();
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(getClass().getDeclaredFields()));
        for (Field field : fields) {
            if (field.getType().isAssignableFrom(Col.class)) {
                this.fields.add(field);
            }
        }
    }

    public List<Col> getColumns(){
        List<Col> columns = new ArrayList<>();
        Col col = new Col();
        for(Field field : fields){
            columns.add(col.getColumn(field));
        }
        return columns;
    }

}
