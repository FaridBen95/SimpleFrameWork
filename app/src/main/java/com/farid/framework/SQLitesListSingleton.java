package com.farid.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLitesListSingleton {

    public HashMap<String, MySqlite> sqlites = new HashMap<String, MySqlite>();

    private static SQLitesListSingleton sqLitesListSingleton;

    private SQLitesListSingleton(){
    }

    public static SQLitesListSingleton getSQLiteList(){
        if(sqLitesListSingleton == null){
            sqLitesListSingleton = new SQLitesListSingleton();
        }
        return sqLitesListSingleton;
    }
}
