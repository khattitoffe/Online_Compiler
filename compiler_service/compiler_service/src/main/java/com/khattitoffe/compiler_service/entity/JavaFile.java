package com.khattitoffe.compiler_service.entity;

public class JavaFile {
    private String FileName;
    private String Email;

    public JavaFile(String FileName,String Email)
    {
        this.FileName=FileName;
        this.Email=Email;
    }

    public String getFileName()
    {
        return FileName;
    }
    public void setFileName(String FileName)
    {
        this.FileName=FileName;
    }

    public String getEmail()
    {
        return Email;
    }
    public void setEmail(String Email)
    {
        this.Email=Email;
    }
    
}
