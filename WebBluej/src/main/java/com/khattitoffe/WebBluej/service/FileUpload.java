package com.khattitoffe.WebBluej.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.S3Client;

public class FileUpload {

    private MultipartFile file=null;
    private String email=null;
    private final S3Client s3;
    private String bucketName="myprojectjavafiles";
    
    public FileUpload(MultipartFile file, String email,S3Client s3) {
        this.file = file;
        this.s3=s3;
        this.email = email;
    }


    /* public boolean uploadJavaFile() {
        String fileDir="E:/Spring Boot/data/src/java/"+email+"/";

        String filename=file.getOriginalFilename();

        try{
            File dir=new File(fileDir);
            if(!dir.exists()) {
                if(!dir.mkdirs()) //dir made // mkdir returns boolean.. agar false aya toh ye true hoke
                {
                    return false;
                }
            }
            File javafile=new File(fileDir+filename);
            file.transferTo(javafile);  // file stored to dir

            return true;//file uploaded
        }
        catch (IOException e)
        {
            return false;//file not uploaded
        }
    }*/
    public boolean uploadJavaFile() {
       try{
            
            String key=email+file.getOriginalFilename();
              s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build(),
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                        file.getInputStream(),
                        file.getSize()
                ));

            return true;
       }
       catch(Exception e)
       {
            return false;
       }
    }
} 