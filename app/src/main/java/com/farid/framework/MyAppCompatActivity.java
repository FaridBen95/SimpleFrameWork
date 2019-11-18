package com.farid.framework;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MyAppCompatActivity extends AppCompatActivity implements ActivityListener, GlobalTouchListener{
    private String info = "No info set for this activity";
    private ActivityListener activityListener;
    private int activityPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListener = this;
        info = activityListener.setInfo();
        activityListener.getActivityPosition(activityPosition);
        activityPosition++;
    }

    @Override
    public String setInfo() {
        return info;
    }

    @Override
    public void getActivityPosition(int position) {
    }

    private View parentView;
    private ArrayList<Field> fieldsList;
    private ArrayList<Field> trackVarList;
    private List<View> viewsList;
    private HashMap<View, String> viewName = new HashMap<>();
    private HashMap<String, Object> currentVariables;
    private MyViewGroup mainParentView;

    @Override
    public String ClickedOn(int x, int y) {
        if (startTrack()) {
            if (trackVariables() != null) {
                detectVariables();
            }
            StringBuilder log = new StringBuilder();
            for (View view : viewsList) {
                if (view != null) {
                    int start_x = mainParentView.getRelativeLeft(view);
                    int start_y = mainParentView.getRelativeTop(view);
                    int end_x = start_x + view.getWidth();
                    int end_y = start_y + view.getHeight();
                    if (x >= start_x && y >= start_y && x <= end_x && y <= end_y) {
                        log.append("\n\r").append("Clicked on View = ").append(viewName.get(view)).append(" of the class = ").append(getClass().getSimpleName());
                    }
                }
            }
            log.append("Les Variables detectés : \n\r");
            for (String key : currentVariables.keySet()) {
                log.append(key);
                log.append(" = ");
                log.append(currentVariables.get(key));
                log.append("\n");
            }

            return log.toString();
        }
        return "";
    }

    private void detectVariables() {
        try {
            List<Field> fieldsList = new ArrayList<>(Arrays.asList(getClass().getDeclaredFields()));
            List<String> trackVariables = trackVariables();
            for (Field field : fieldsList) {
                if(trackVariables.contains(field.getName())) {
                    field.setAccessible(true);
                    currentVariables.put(field.getName(), field.get(this));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        parentView = findViewById(android.R.id.content);
        View view = parentView;
        setViewsFromXML();
        ViewGroup parentView = (ViewGroup) view.getParent();
        if(parentView != null){
            ViewGroup parentparentView = (ViewGroup) parentView.getParent();
            mainParentView = new MyViewGroup(this);
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
            mainParentView.setLayoutParams(params);
            parentparentView.removeView(parentView);
            mainParentView.addView(parentView);
            parentparentView.addView(mainParentView);
            mainParentView.setGlobalTouchListener(this);
            if(startTrack()){
                currentVariables = new HashMap<>();
            }
        }
        if(startTrack()){
            currentVariables = new HashMap<>();
        }
    }

    private void setViewsFromXML() {
        if (startTrack()) {
            fieldsList = new ArrayList<>();
            viewsList = new ArrayList<>();
            trackVarList = new ArrayList<>();
            List<Class <?>> trackClassesList = trackByClass();
            List<String> variablesToTrack = trackVariables();
            for (View view : getAllChildrenBFS(parentView)) {
                Class<?> viewClass = null;
                try {
                    viewClass = Class.forName(view.getClass().getName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (viewClass != null) {
                    if((trackClassesList == null || trackClassesList.contains(viewClass))
                            && View.class.isAssignableFrom(viewClass)){
                        if(view.getId() != -1 && (!trackByTag()
                                || ( view.getTag() != null && view.getTag().toString().equals("track")))) {
                            viewsList.add(view);
                            viewName.put(view, getResources().getResourceEntryName(view.getId()));
                        }
                    }
                }
            }
        }
    }

    private List<View> getAllChildrenBFS(View v) {
        List<View> visited = new ArrayList<View>();
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i=0; i<childCount; i++) unvisited.add(group.getChildAt(i));
        }

        return visited;
    }

    @Override
    protected void onPause() {
        MyUtil.hideKeyboard(this);
        MySharedPreferences mySharedPreferences = new MySharedPreferences(this);
        mySharedPreferences.putInt("last_activity_index", activityPosition);
        super.onPause();
    }

    private void openLastActivity(){
        int lastActivityPos;
    }
}
