package com.farid.framework;

public interface DatabaseObserver {

    public interface OnDatabaseCreated{
        public String execSql();
        public void tableCreated(String tableName);
    }

    public boolean onStartedTransaction();
}
