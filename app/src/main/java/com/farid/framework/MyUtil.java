package com.farid.framework;

public class MyUtil {

    public static String caller(){
        try{
            throw new Exception("the caller is");
        }
        catch (Exception e){
            return e.getStackTrace()[0].getFileName().replace(".java","");
        }
    }
}
