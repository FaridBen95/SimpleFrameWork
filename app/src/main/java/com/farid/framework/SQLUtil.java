package com.farid.framework;

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class SQLUtil {
    private static HashMap<String, String> sqlCreateStatement = new HashMap<>();

    public static void generateCreateStatement(Model model){
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(model.getModelName());
        sql.append(" (");
        List<Col> columns = model.getColumns();
        sql.append(generateColumnStatement(columns));
        sql.deleteCharAt(sql.lastIndexOf(","));
        sql.append(")");
        sqlCreateStatement.put(model.getModelName(), sql.toString());
    }

    public static HashMap<String, String> getSqlCreateStatement() {
        return sqlCreateStatement;
    }

    public static String generateColumnStatement(List<Col> cols){
        StringBuffer colStatement = new StringBuffer();
        for(Col col : cols){
            String type = col.getType()+" ";
            String name = col.getName()+" ";
            colStatement.append(name);
            colStatement.append(type);
            if(col.isAutoIncrement()){
                colStatement.append(" PRIMARY KEY ");
                colStatement.append(" AUTOINCREMENT ");
            }
            Object default_value = col.getDefaultValue();
            if (default_value != null) {
                colStatement.append(" DEFAULT ");
                if (default_value instanceof String) {
                    colStatement.append("'" + default_value + "'");
                } else {
                    colStatement.append(default_value);
                }
            }
            colStatement.append(", ");
        }
        colStatement.deleteCharAt(colStatement.lastIndexOf(","));
        return colStatement.toString();
    }

    public static boolean startTransaction(MySqlite sqLiteDatabase, DatabaseObserver databaseObserver){
        boolean transactionSuccessful = false;
        SQLiteDatabase db = sqLiteDatabase.getReadableDatabase();
        db.beginTransaction();
        try {
            transactionSuccessful = databaseObserver.onStartedTransaction();
            if(transactionSuccessful){
                db.setTransactionSuccessful();
            }
        }catch (Exception ignored){

        }finally {
            db.endTransaction();
        }
        return transactionSuccessful;

    }


}
