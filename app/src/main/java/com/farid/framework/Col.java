package com.farid.framework;

import java.lang.reflect.Field;

public class Col {
    private String currentModel;
    public enum ColumnType {
        varchar, text, integer, real, bool, relation
    }
    private ColumnType columnType;
    private String type;
    private String name;
    private Class relationalModel;
    private boolean autoIncrement;
    private Object defaultValue;

    public Col(){
    }

    public Col(ColumnType columnType){
        this.columnType = columnType;
        setType(columnType);
    }

    public Col(ColumnType columnType, Class relationalModel){
        this.columnType = columnType;
        setType(columnType);
        this.relationalModel = relationalModel;
        try{
            throw new Exception("the caller is");
        }
        catch (Exception e){
            currentModel = e.getStackTrace()[0].getFileName().replace(".java","");
        }

    }

    public Col getColumn(Field field){
        try {
            return (Col) field.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Col setDefaultValue(Object defaultValue){
        this.defaultValue = defaultValue;
        return this;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public String getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(String currentModel) {
        this.currentModel = currentModel;
    }

    public ColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(ColumnType columnType) {
        this.columnType = columnType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getRelationalModel() {
        return relationalModel;
    }

    public void setRelationalModel(Class relationalModel) {
        this.relationalModel = relationalModel;
    }

    public void setType(ColumnType columnType) {
        String type = "TEXT";
        Object defaultValue = "";
        switch (columnType){
            case bool:
                type = "BOOLEAN";
                break;
            case real:
                type = "REAL";
                defaultValue = 0;
                break;
            case varchar:
                type = "VARCHAR";
                break;
            case integer:
            case relation:
                type = "INTEGER";
                defaultValue = 0;
                break;
        }
        this.defaultValue = defaultValue;
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
}
