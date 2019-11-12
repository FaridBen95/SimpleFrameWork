package com.farid.framework;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        example e = new example(this);
        e.startTransaction(new DatabaseObserver() {
            @Override
            public boolean onStartedTransaction(SQLiteDatabase db) {
                db.execSQL("INSERT into example(enabled, removed, lastName) values (1, 1, 'farid')");
                return true;
            }
        });
        e.simpleSelect();
    }

}
