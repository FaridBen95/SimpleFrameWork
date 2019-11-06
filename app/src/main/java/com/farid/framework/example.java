package com.farid.framework;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class example extends Model {
    Col lastName = new Col();

    public example(Context mContext ) {
        super(mContext, "example");
    }
}
