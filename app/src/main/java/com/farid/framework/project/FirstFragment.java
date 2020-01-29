package com.farid.framework.project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.farid.framework.R;
import com.farid.framework.framework_repository.core.BaseFragment;
import com.farid.framework.framework_repository.core.Values;

import java.util.ArrayList;
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
    public int setLayout() {
        return R.layout.fragment_example;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        List<Object> valuesList = new ArrayList<>();
        Values values = new Values();
        values.put("lastName", "faridOo");
        valuesList.add(values);
        valuesList.add(values);
        valuesList.add(values);
        valuesList.add(values);/*
        CursorListAdapter adapter = new CursorListAdapter(context, null, R.layout.example_row);
        adapter.setOnViewBindListener(this);
        ListView listView = (ListView)view.findViewById(R.id.fragment_list_view);
        listView.setAdapter(adapter);
        adapter.changeCursor(new example(context).simpleSelect("", null));
        */
    }
}
