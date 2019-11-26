package com.farid.framework;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class BaseFragment extends DialogFragment implements BaseFragmentInterface, ChangingViewListener, GlobalTouchListener{
    public Context context;
    private ChangingViewListener changingViewListener;
    public String info;
    private View parentView;
    private ArrayList<Field> fieldsList;
    private ArrayList<Field> trackVarList;
    private List<View> viewsList;
    private HashMap<View, String> viewName = new HashMap<>();
    private MyViewGroup fragmentParentView;
    private HashMap<String, Object> currentVariables;

    private void setChangingViewListener(ChangingViewListener changingViewListener){
        this.changingViewListener = changingViewListener;
    }

    public void setTitle(String title) {
        getActivity().setTitle(title);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        info = setInfo();
    }

    public Model db(){
        if(context != null){
            Class<?> model = database();
            if(model != null){
                return new Model(context, null).createInstance(model);
            }
        }
    return null;
    }

    public boolean inNetwork() {
        App app = (App) context.getApplicationContext();
        return app.inNetwork();
    }

    public void startFragment(Fragment fragment, Boolean addToBackState, Bundle extra) {
        parent().loadFragment(fragment, addToBackState, extra);
        if(changingViewListener != null){
            changingViewListener.openedClass(fragment.getClass());
        }
    }

    public App app() {
        return (App) context.getApplicationContext();
    }


    public MyAppCompatActivity parent() {
        return (MyAppCompatActivity) context;
    }

    abstract int setLayout();

    void onCreateView(View view){

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(setLayout(), container, false);
        onCreateView(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        parentView = view;
        setViewsFromXML();
        ViewGroup parentView = (ViewGroup) view.getParent();
        if(parentView != null){
            ViewGroup parentparentView = (ViewGroup) parentView.getParent();
            fragmentParentView = new MyViewGroup(getActivity());
            ViewGroup.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            fragmentParentView.setLayoutParams(params);
            parentparentView.removeView(parentView);
            fragmentParentView.addView(parentView);
            parentparentView.addView(fragmentParentView);
            fragmentParentView.setGlobalTouchListener(this);
            if(startTrack()){
                currentVariables = new HashMap<>();
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    private void setViewsFromXML() {
        if (startTrack()) {
            fieldsList = new ArrayList<>();
            viewsList = new ArrayList<>();
            trackVarList = new ArrayList<>();
            List<Class <?>> trackClassesList = trackByClass();
            List<String> variablesToTrack = trackVariables();
//                for (Field field : getClass().getDeclaredFields()) {
            for (View view : getAllChildrenBFS(parentView)) {
                Class<?> viewClass = null;
                try {
                    viewClass = Class.forName(view.getClass().getName());
                } catch (ClassNotFoundException e) {
                    //                    e.printStackTrace();
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
    public String ClickedOn(int x, int y) {
        if(trackVariables() != null){
            detectVariables();
        }
        StringBuilder log = new StringBuilder();
        for(View view : viewsList){
            if (view != null) {
                int start_x = fragmentParentView.getRelativeLeft(view);
                int start_y = fragmentParentView.getRelativeTop(view);
                int end_x = start_x + view.getWidth();
                int end_y = start_y + view.getHeight();
                if (x >= start_x && y >= start_y && x <= end_x && y <= end_y) {
                    log.append("\n\r").append("Clicked on View = ").append(viewName.get(view)).append(" of the class = ").append(getClass().getSimpleName());
                }
            }
        }
        log.append("Les Variables detectés : \n\r");
        for(String key : currentVariables.keySet()){
            log.append(key);
            log.append(" = ");
            log.append(currentVariables.get(key));
            log.append("\n");
        }

        return log.toString();
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


}
