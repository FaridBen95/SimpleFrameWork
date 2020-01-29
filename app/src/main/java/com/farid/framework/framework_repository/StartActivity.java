package com.farid.framework.framework_repository;

import android.support.v7.app.ActionBar;
import android.os.Bundle;

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
    public Class<?> trackActivity() {
        return StartActivity.class;
    }

    @Override
    public List<String> trackVariables() {
        List<String> variableNames = new ArrayList<>();
        variableNames.add("");
        return super.trackVariables();
    }
}
