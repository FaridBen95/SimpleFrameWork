package com.farid.framework.framework_repository.core;

interface DatabaseListener {
    boolean unAssigneFromModel();
    boolean sortableColumns();
    interface TransactionsListener{
        void onPreInsert(Values values);
        void onPreUpdate(Values values, String selectionConditions);
        void onPreDelete(String selectionConditions);
    }
}
