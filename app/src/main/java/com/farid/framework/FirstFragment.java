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
    int setLayout() {
        return R.layout.activity_start2;
    }
}
