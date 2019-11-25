package com.farid.framework;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends DialogFragment implements BaseFragmentInterface, ChangingViewListener, GlobalTouchListener{
    public Context context;
    private ChangingViewListener changingViewListener;
    public String info;

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
}
