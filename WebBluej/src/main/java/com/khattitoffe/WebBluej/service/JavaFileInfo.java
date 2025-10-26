package com.khattitoffe.WebBluej.service;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;


public class JavaFileInfo implements FileInfo{
    private String fileLoaction="E:/Spring Boot/data/src/java/";
    private File fileDir;
    private File file;
    private CompilationUnit cu;
    private String className=""; // classname
    private String parentClassName=""; // parent class name
    private HashMap<String,String> methodsName=new HashMap<>(); // all methods
    private ArrayList<String> interfaceName=new ArrayList<>(); // all interface it implements
    private HashMap<String,String> objectRefernces=new HashMap<>();
    private String email;
    private ArrayList<String> classFiles = new ArrayList<>();

    public JavaFileInfo(String email){
        this.email=email;
        fileDir=new File(fileLoaction+email+"/");
    }

    public String[] getFileList(){
        return fileDir.list();
    }


    public void initializeInfo(String className){
        System.out.println("Inside initialize info"+className);
        if (!className.endsWith(".java")) {
            className += ".java";
        }
        file=new File(fileLoaction+email+"/"+className);
        initialize();
        extractInfo();
    }


    private void initialize(){
        JavaParser parser = new JavaParser();

        String[] fileList = fileDir.list();
        if (fileList != null) {
            for (String fileName : fileList) {
                if (fileName.endsWith(".java")) {
                    classFiles.add(fileName.replace(".java", ""));
                }
            }
        }
        try {
            ParseResult<CompilationUnit> result = parser.parse(file);
            if(result.getResult().isPresent())
            {
                cu = result.getResult().get();
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void extractInfo()
    {



        for (ClassOrInterfaceDeclaration clazz : cu.findAll(ClassOrInterfaceDeclaration.class)) {

            className=clazz.getNameAsString();

            clazz.getExtendedTypes().forEach(parent ->
                    parentClassName=parent.getNameAsString()
            );
            clazz.getImplementedTypes().forEach(interf ->
                    interfaceName.add(interf.getNameAsString())
            );

            clazz.getMethods().forEach(method ->
                    methodsName.put(method.getDeclarationAsString(true,false,true),method.getNameAsString())
            );
        }



      //  HashMap<String,String> objectRefernces_new=new HashMap<>();
        for (VariableDeclarationExpr varExpr : cu.findAll(VariableDeclarationExpr.class)) {

            for (VariableDeclarator var : varExpr.getVariables()) {

                if(!var.getType().isPrimitiveType()) {
                    if (classFiles.contains((var.getType().asString()))) {
                        System.out.println(var.getType().asString());
                        objectRefernces.put(var.getNameAsString(),var.getType().asString());
                    }
                }
                /*String type = var.getType().asString();

                // If it's not a primitive type and matches another class in the directory
                if (!var.getType().isPrimitiveType() && classFiles.contains(type)) {
                    System.out.println(var.getNameAsString());
                    objectRefernces.put(var.getNameAsString(), type);
                }
                 */
            }
        }
       // objectRefernces=objectRefernces_new;

    }

    @Override
    public String getClassName() {
        return className;
    }
    @Override
    public HashMap<String,String> getMethodNames() {
        return methodsName;
       // return methodsName.toArray(String[]::new);
    }
    @Override
    public String getSuperClassName() {
        return parentClassName;
    }
    @Override
    public String[] getSuperInterfaceName(){
        return interfaceName.toArray(String[]::new);
    }
    @Override
    public HashMap<String,String> getObjectReferences() {
        return objectRefernces;
    }

    @Override
    public boolean isInterface(){

        return true; // kya interface hai ya nahie true/false returm type
    }
}
