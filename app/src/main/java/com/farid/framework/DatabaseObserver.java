package com.farid.framework;

import android.database.sqlite.SQLiteDatabase;

public interface DatabaseObserver {

    public interface OnDatabaseCreated{
        public String execSql();
        public void tableCreated(String tableName);
    }

    public boolean onStartedTransaction(SQLiteDatabase db);
}
