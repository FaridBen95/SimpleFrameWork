package com.farid.framework;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

class MySqlite extends SQLiteOpenHelper{
    public static final String TAG = MySqlite.class.getSimpleName();

    private String DATABASE_NAME = MyConstants.DATABASE_NAME;
    private Context mContext;
    private int DATABASE_VERSION = MyConstants.DATABASE_VERSION;

    public DatabaseObserver getDatabaseObserver() {
        return databaseObserver;
    }

    public void setDatabaseObserver(DatabaseObserver databaseObserver) {
        this.databaseObserver = databaseObserver;
    }

    private DatabaseObserver databaseObserver;

    public DatabaseErrorHandler getDatabaseErrorHandler() {
        return databaseErrorHandler;
    }

    private DatabaseErrorHandler databaseErrorHandler = new DatabaseErrorHandler() {
        @Override
        public void onCorruption(SQLiteDatabase sqLiteDatabase) {

        }
    };

    public MySqlite(Context mContext) {
        this(mContext, null);
    }

    public MySqlite(Context mContext, DatabaseErrorHandler databaseErrorHandler){
        super(mContext, MyConstants.DATABASE_NAME, null, MyConstants.DATABASE_VERSION,
                databaseErrorHandler);
        this.DATABASE_NAME = MyConstants.DATABASE_NAME;
        this.DATABASE_VERSION = MyConstants.DATABASE_VERSION;
        this.mContext = mContext;
        this.databaseErrorHandler = databaseErrorHandler;
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.i(TAG, "Creating database.");
        ModelRegistryUtils registryUtils = App.getModelRegistryUtils();
        HashMap<String, Class<? extends Model>> models = registryUtils.getModels();
        for (String key : models.keySet()){
            Model model = App.getModel(mContext, key);
            SQLUtil.generateCreateStatement(model);
        }
        HashMap<String, String> sqlCreateStatement = SQLUtil.getSqlCreateStatement();
        for(String modelName : sqlCreateStatement.keySet()){
            final String createQuery = sqlCreateStatement.get(modelName);
            SQLUtil.startTransaction(this, new DatabaseObserver() {
                @Override
                public boolean onStartedTransaction() {
                    try {
                        db.execSQL(createQuery);
                    }catch (SQLException s){
                        return false;
                    }
                    return true;
                }
            });
        }
        Log.i(TAG, "Tables Created ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }




}
