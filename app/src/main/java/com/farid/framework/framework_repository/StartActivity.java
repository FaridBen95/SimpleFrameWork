package com.farid.framework.framework_repository;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.farid.framework.project.FirstFragment;
import com.farid.framework.R;
import com.farid.framework.project.example;
import com.farid.framework.framework_repository.Utils.FragmentUtils;
import com.farid.framework.framework_repository.core.MyAppCompatActivity;
import com.farid.framework.framework_repository.core.Values;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends MyAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
//        setTitleBar();
//        FragmentUtils.get(this, null).startFragment(new FirstFragment(), true, null);
    }
    @Override
    public boolean startTrack() {
        return false;
    }
    
    @Override
    public Class<?> trackActivity() {
        return StartActivity.class;
    }

    @Override
    public List<String> trackVariables() {
        List<String> variableNames = new ArrayList<>();
        variableNames.add("");
        return super.trackVariables();
    }

    @Override
    public void setTitleBar(ActionBar actionBar) {
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }


    @Override
    public Toolbar setToolBar() {
        return (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.example_menu, menu);
        return true;
    }
}
