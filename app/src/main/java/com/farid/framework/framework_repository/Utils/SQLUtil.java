package com.farid.framework.framework_repository.Utils;

import android.database.sqlite.SQLiteDatabase;

import com.farid.framework.framework_repository.core.Col;
import com.farid.framework.framework_repository.core.DatabaseObserver;
import com.farid.framework.framework_repository.core.Model;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SQLUtil {
    private static HashMap<String, String> sqlCreateStatement = new HashMap<>();
    private static Model model;

    public static void generateCreateStatement(Model model){
        SQLUtil.model = model;
        StringBuffer sql = new StringBuffer();
        sql.append("CREATE TABLE IF NOT EXISTS ");
        sql.append(model.getModelName());
        sql.append(" (");
        List<Col> columns = model.getColumns();
        sql.append(generateColumnStatement(columns));
        sql.append(")");
        sqlCreateStatement.put(model.getModelName(), sql.toString());
    }

    public static HashMap<String, String> getSqlCreateStatement() {
        return sqlCreateStatement;
    }

    private static String generateColumnStatement(List<Col> cols){
        StringBuffer colStatement = new StringBuffer();
        if(model.sortableColumns()){
            sortColumns(cols);
        }
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
            if (default_value != null && !default_value.toString().equals("")) {
                colStatement.append(" DEFAULT ");
                if (default_value instanceof String) {
                    colStatement.append("'").append(default_value).append("'");
                } else {
                    colStatement.append(default_value);
                }
            }
            colStatement.append(", ");
        }
        colStatement.deleteCharAt(colStatement.lastIndexOf(","));
        return colStatement.toString();
    }

    public static boolean startTransaction(SQLiteDatabase db, DatabaseObserver databaseObserver){
        boolean transactionSuccessful = false;
        db.beginTransaction();
        try {
            transactionSuccessful = databaseObserver.onStartedTransaction(db);
            if(transactionSuccessful){
                db.setTransactionSuccessful();
            }
        }catch (Exception ignored){
            ignored.printStackTrace();
        }finally {
            db.endTransaction();
        }
        return transactionSuccessful;

    }

    private static void sortColumns(final List<Col> cols) {
        Collections.sort(cols, new Comparator<Col>() {
            @Override
            public int compare(Col o1, Col o2) {
                if(o1.getSequence() > o2.getSequence())
                    return 1;
                else if (o1.getSequence() < o2.getSequence())
                    return -1;
                else return 0;
            }
        });
    }



}
