package com.khattitoffe.WebBluej.controller;
import com.khattitoffe.WebBluej.entity.JavaFileDelete;
import com.khattitoffe.WebBluej.entity.JavaFileName;
import com.khattitoffe.WebBluej.service.*;
import jakarta.servlet.http.HttpServletRequest;
import software.amazon.awssdk.services.s3.S3Client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.khattitoffe.WebBluej.entity.JavaFile;
//import com.khattitoffe.WebBluej.entity.JavaFileName;
import com.khattitoffe.WebBluej.entity.JavaFileUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
//import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.khattitoffe.WebBluej.entity.CompileRequest;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    FileGet fileGet;

    @Autowired
    FileDelete fileDelete;

    @Autowired
    JavaFileUpdateService javaFileUpdateService;

    @Autowired
    RestTemplate restTemplate;


    private final S3Client s3;

    @Autowired
    public FileController(S3Client s3) {
        this.s3 = s3;
    }

    @PostMapping("/uploadJava")
    public ResponseEntity<?> uploadJavaFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
            Map<String, String> response = new HashMap<>();
            String email=null;
            
            try{
                email=log(request.getHeader("Authorization"));
            }
            catch(Exception e)
            {
                response.put("message", "Internal Server Error");
                return ResponseEntity.badRequest().body(response);
            }


            if(file.isEmpty()) // file non empty honi chahiye
            { 
                response.put("message", "Empty File Uploaded");
                return ResponseEntity.badRequest().body(response);
            }
            try {
                if (!file.getOriginalFilename().endsWith(".java"))// ends with shayad null pointer exception dede
                    {
                        response.put("message", "Not a Java File");
                         return ResponseEntity.badRequest().body(response);
                    }
            }
            catch (NullPointerException e)
            {
                response.put("message", e.toString());
                return ResponseEntity.badRequest().body(response);
            }
            // locally storing the file

        FileUpload upload = new FileUpload(file,email,s3);
        //using fileupload service
        if(upload.uploadJavaFile())
        {
           response.put("message", "File Uploaded Successfully");
           return ResponseEntity.ok().body(response);
        }
        else
        {
            response.put("message", "Internal Server Error");
            return ResponseEntity.badRequest().body(response);
        }
            

    }

    @PostMapping("/updateJavaFile")
    public ResponseEntity<?> updateJavaFile(@RequestBody JavaFileUpdate file,HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        String email=null;

        try{
            email=log(request.getHeader("Authorization"));
        }
        catch(Exception e)
        {
            response.put("message", "Internal Server Error");
            return ResponseEntity.badRequest().body(response);
        }

        String filename = file.getClassName();
        String code = file.getCode();
        System.out.println(filename);   
        try {
            return javaFileUpdateService.updateFile(filename, code, email,s3);
        } catch (IOException e) {
            response.put("message", "Failed  to Update File");
            return ResponseEntity.badRequest().body(response);
        }   
    }

    @DeleteMapping("/deleteJavaFile")
    public ResponseEntity<?> deleteJavaFile(@RequestBody JavaFileDelete file, HttpServletRequest request) {

        String email=null;
        try{
            email=log(request.getHeader("Authorization"));
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("Internal Server Error");
        }

        String filename=file.getFileName();
        if(fileDelete.deleteFile(email,filename,s3))
        {
            return ResponseEntity.ok().body("Deleted file successfully");
        }
        else
        {
            return ResponseEntity.badRequest().body("Failed to delete file");
        }
    }


    @PostMapping("/getJavaFileInfo")
    public ResponseEntity<?> getInfo( HttpServletRequest request) {
        
        String email=null;
        try{
            email=log(request.getHeader("Authorization"));
        }
        catch(Exception e)
        {
            return ResponseEntity.badRequest().body("Internal Server Error");
        }

        ArrayList<JavaFile> infoClass = new ArrayList<>();
        String[] fileList = new JavaFileInfo(email).getFileList();

        for (String file : fileList)
        {
            JavaFileInfo info = new JavaFileInfo(email);
            info.initializeInfo(file);
            infoClass.add(new JavaFile(info.getClassName(),info.getMethodNames(),info.getSuperClassName(),info.getSuperInterfaceName(),info.getObjectReferences()));
        }

        return ResponseEntity.ok(infoClass);
        // new JavaFile(info.getClassName(),info.getMethodNames(),info.getSuperClassName(),info.getSuperInterfaceName(),info.getObjectReferences());
    }

    @GetMapping("/getClasses")
    public ResponseEntity<?> getClasses(HttpServletRequest request) {
        System.out.println("Endpoint hit");
        Map<String, String> response = new HashMap<>();
        String email=null;
        try{
            email=log(request.getHeader("Authorization"));
        }
        catch(Exception e)
        {
            response.put("message","Internal Server Error");
            return ResponseEntity.badRequest().body(response);
        }
        
        HashMap<String,String> classes=new HashMap<>();

        try {
            classes=fileGet.getClasses(email,s3);
        } catch (IOException e) {
            response.put("message",e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok().body(classes);
    }



    @PostMapping("/execute")
    public ResponseEntity<?> execute( HttpServletRequest request,@RequestBody JavaFileName file) {
        Map<String, String> response = new HashMap<>();
        String email=null;
        try{
            email=log(request.getHeader("Authorization"));
        }
        catch(Exception e)
        {
            response.put("message", "Internal Server Error");
            return ResponseEntity.badRequest().body(response);
        }
        

        // send request to other service for execution
        String url="http://3.110.68.181:8080/compiler/execute";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        CompileRequest data=new CompileRequest(file.getjavaFileName(),email);
        HttpEntity<?> entity = new HttpEntity<>(data, headers);

        ResponseEntity<Map> CompileReq = restTemplate.postForEntity(url, entity, Map.class);
        System.out.println("workling here");
        System.out.println(CompileReq);
        return ResponseEntity.ok().body(CompileReq);
    }
    

    public String log(String AuthHeader)
    {
        String email=null;

        if (AuthHeader == null || AuthHeader.startsWith("Bearer ")) {
            String token = AuthHeader.substring(7);
            email = jwtUtil.extractEmail(token);
            System.out.println("username: "+email);
        }

        return email;
    }
}
