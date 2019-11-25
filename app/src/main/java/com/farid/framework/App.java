package com.farid.framework;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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

    public static SQLiteDatabase getDB( String modelName, boolean writableDatabase){
        return writableDatabase ? sqlites.get(modelName).getWritableDatabase() :
                sqlites.get(modelName).getReadableDatabase();
    }

    /**
     * Checks for network availability
     *
     * @return true, if network available
     */
    public boolean inNetwork() {
        boolean isConnected = false;
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = manager.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnectedOrConnecting()) {
            isConnected = true;
        }
        return isConnected;
    }

}
