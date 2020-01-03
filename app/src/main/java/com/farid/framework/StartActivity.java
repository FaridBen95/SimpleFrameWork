package com.farid.framework;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends MyAppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        setTitleBar();
        example e = new example(this);
        Values values = new Values();
        values.put("lastName", "faridOo");
        e.insert(values);
        e.getRows("_id = ? ", new String[]{"4"});
        FragmentUtils.get(this, null).startFragment(new FirstFragment(), true, null);
    }

    public void setTitleBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.hide();
        }
    }
    @Override
    public boolean startTrack() {
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public Class<?> trackActivity() {
        return StartActivity.class;
    }
}
