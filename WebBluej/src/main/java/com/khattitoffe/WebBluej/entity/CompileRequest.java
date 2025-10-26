package com.khattitoffe.WebBluej.entity;

public class CompileRequest {
    private String FileName;
    private String Email;

    public CompileRequest(String FileName,String Email)
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
