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
        Values values = new Values();
        values.put("lastName", "faridOo");
        e.insert(values);
        values.put("lastName","updated");
        e.delete("id = ? ", new String[]{"3"});
        e.getRows("id = ? ", new String[]{"4"});
    }

}
