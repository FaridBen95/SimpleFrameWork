package com.farid.framework;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class FirstFragment extends BaseFragment {
    @Override
    public Class<example> database() {
        return example.class;
    }

    @Override
    public String setInfo() {
        return "first_fragment";
    }

    @Override
    public Class<?> trackActivity() {
        return null;
    }

    @Override
    public void openedClass(Class opened) {
        Toast.makeText(context, "new class is opened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public String ClickedOn(int x, int y) {
        return null;
    }

    @Override
    public boolean startTrack() {
        return false;
    }

    @Override
    public boolean trackByTag() {
        return false;
    }

    @Override
    public List<Class<?>> trackByClass() {
        return null;
    }

    @Override
    public List<String> trackVariables() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    int setLayout() {
        return R.layout.activity_start;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toast.makeText(context, "finaly", Toast.LENGTH_SHORT).show();
    }
}
