package com.farid.framework;

public interface ActivityListener {
    String setInfo();
    Class<?> trackActivity();
    interface OnChangeView {
        void openedClass(Class opened);
    }
}
