package com.khattitoffe.WebBluej.service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import org.springframework.stereotype.Service;

import java.io.File;
@Service
public class FileDelete {

    private final String bucketName = "myprojectjavafiles";

    public boolean deleteFile(String email,String fileName,S3Client s3) {
        String key=email+fileName;
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key) // key = "email/Filename.java"
                    .build();

            s3.deleteObject(deleteObjectRequest);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /*
     * public boolean deleteFile(String email,String fileName) {
        String dir="E:/Spring Boot/data/src/java/"+email+"/";
        File fileDir=new File(dir);
        if(fileDir.exists()) {
            File file=new File(dir+fileName);
            if(file.exists()) {
                try{
                    if(file.delete())
                        return true;
                    else
                        return false;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    return false;
                }

            }
        }
        return false;
    }
     */
}
