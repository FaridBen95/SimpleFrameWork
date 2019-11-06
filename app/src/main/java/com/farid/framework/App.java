package com.farid.framework;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

public class App extends Application {
    public static boolean create = false;
    private static HashMap<String, MySqlite> sqlites = new HashMap<>();
    private static ModelRegistryUtils modelRegistryUtils = new ModelRegistryUtils();


    public static ModelRegistryUtils getModelRegistryUtils() {
        return modelRegistryUtils;
    }

    public static HashMap<String, MySqlite> getSqlites() {
        return sqlites;
    }

    public static void setSqlites(HashMap<String, MySqlite> sqlites) {
        App.sqlites = sqlites;
    }

    public static void addSQLite(String modelName, MySqlite sqlite){
        App.sqlites.put(modelName, sqlite);
        SQLitesListSingleton.getSQLiteList().sqlites = sqlites;
    }

    public static <T> T getModel(Context context, String modelName ) {
        Class<? extends Model> modelCls = App.modelRegistryUtils.getModel(modelName);
        if (modelCls != null) {
            try {
                Constructor constructor = modelCls.getConstructor(Context.class);
                return (T) constructor.newInstance(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SQLitesListSingleton.getSQLiteList().sqlites = sqlites;
        modelRegistryUtils.makeReady(getApplicationContext());
        create = true;
    }
}
