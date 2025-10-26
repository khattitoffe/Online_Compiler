package com.khattitoffe.WebBluej.entity;

import java.util.HashMap;

public class JavaFile {
    private String filename;
    private HashMap<String,String> methods;
    private String superClasses;
    private String[] superInterfaces;
    private HashMap<String,String> objectReferences;

    public JavaFile(String filename, HashMap<String,String> methods, String superClasses, String[] superInterfaces, HashMap<String,String> objectReferences) {
        this.filename = filename;
        this.methods = methods;
        this.superClasses = superClasses;
        this.superInterfaces = superInterfaces;
        this.objectReferences = objectReferences;
    }

    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public HashMap<String,String> getMethods() {
        return methods;
    }
    public void setMethods(HashMap<String,String> methods) {
        this.methods = methods;
    }
    public String getSuperClasses() {
        return superClasses;
    }
    public void setSuperClasses(String superClasses) {
        this.superClasses = superClasses;
    }
    public String[] getSuperInterfaces() {
        return superInterfaces;
    }
    public void setSuperInterfaces(String[] superInterfaces) {
        this.superInterfaces = superInterfaces;
    }

    public HashMap<String, String> getObjectReferences() {
        return objectReferences;
    }

    public void setObjectReferences(HashMap<String,String> objectReferences) {
        this.objectReferences = objectReferences;
    }
}
