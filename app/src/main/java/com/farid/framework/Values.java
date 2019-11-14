package com.farid.framework;

import android.content.ContentValues;

import java.util.HashMap;

public class Values extends HashMap<String, Object> {

    public ContentValues toContentValues(){
        ContentValues contentValues = new ContentValues();
        for(String key : keySet()){
            Object val = get(key);
            if (val instanceof byte[]) {
                contentValues.put(key, (byte[]) val);
            } else if (val != null) {
                contentValues.put(key, val.toString());
            }
        }
        return contentValues;
    }
}
