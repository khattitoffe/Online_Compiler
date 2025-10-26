package com.khattitoffe.WebBluej.service;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.core.sync.RequestBody;
import javax.mail.Multipart;

import java.io.*;

@Service
public class JavaFileUpdateService {

    private String bucketName="myprojectjavafiles";

   
    public ResponseEntity<?> updateFile(String fileName, String code, String email,S3Client s3)  throws IOException {
        Map<String, String> response = new HashMap<>();

        String dir=createJavaFile(code,fileName,email);
        if(dir=="")
        {
            response.put("message", "Internal Server Error");
            return ResponseEntity.badRequest().body(response);
        }
        
        File file=new File(dir);
        if (!fileName.endsWith(".java")) {
            fileName = fileName + ".java";
        }
        String key=email+fileName;
        try{
            s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType("text/x-java-source") // Java MIME type
                        .build(),
                RequestBody.fromFile(file)
            );
            response.put("message", "File Uploaded");
            return ResponseEntity.ok().body(response);
        }
        catch(Exception e)
        {
            response.put("message", "Internal Server Error");
            return ResponseEntity.badRequest().body(response);
        }

    }

    public static String createJavaFile(String code, String filename,String email) {
        // Ensure the filename ends with .java
        if (!filename.endsWith(".java")) {
            filename = filename + ".java";
        }
        String dir="E:/Spring Boot/data/src/java/"+email+"/";
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs(); // Create directories if not exist
        }

        dir=dir+filename;
        File file = new File(dir);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
            writer.flush();
            return dir;
        } catch (IOException e) {
            e.printStackTrace();
            return ""; // Failure
        }

    }
}
    /*
     * public ResponseEntity<String> updateFile(String fileName, String code, String username)  throws IOException {
        String fileDir="E:/Spring Boot/data/src/java/"+username+"/";
        //String filename=fileName+".java";
        File file=new File(fileDir+fileName);

        Path filePath = Path.of(fileDir+fileName);
        if (file.exists()){
            try{
                Files.writeString(filePath,code,StandardOpenOption.TRUNCATE_EXISTING);
                return ResponseEntity.ok("File updated successfully");
            } catch (IOException e) {
                return ResponseEntity.badRequest().body("Unable to Save File");
            }
        }

        else{
            if(file.createNewFile()){
                try{
                    Files.writeString(filePath,code,StandardOpenOption.TRUNCATE_EXISTING);
                    return ResponseEntity.ok("Updated successfully");
                } catch (IOException e) {
                    return ResponseEntity.badRequest().body("Unable to Save File");
                }
            }

        }
        return ResponseEntity.badRequest().body("");
    }
     */

