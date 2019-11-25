package com.farid.framework;

import android.content.Context;

import java.util.List;

interface BaseFragmentInterface {
    public <T> Class<T> database();
    String setInfo();
    Class<?> trackActivity();
}
