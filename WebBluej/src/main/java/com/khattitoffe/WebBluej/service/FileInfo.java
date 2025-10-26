package com.khattitoffe.WebBluej.service;

import java.util.HashMap;

public interface FileInfo {
    public abstract String getClassName();
    public abstract HashMap<String,String> getMethodNames();
    public abstract String getSuperClassName();
    public abstract String[] getSuperInterfaceName();
    public abstract boolean isInterface();
    public abstract HashMap<String,String> getObjectReferences();

}
