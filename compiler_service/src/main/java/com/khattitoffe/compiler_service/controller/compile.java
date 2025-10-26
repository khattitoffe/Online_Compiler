package com.khattitoffe.compiler_service.controller;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.khattitoffe.compiler_service.entity.JavaFile;
import com.khattitoffe.compiler_service.config.S3MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.khattitoffe.compiler_service.service.compiler;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
@RestController
@RequestMapping("/compiler")
public class compile {

    private final S3Client s3;
    private String bucketName="myprojectjavafiles";

    @Autowired
    public compile(S3Client s3) {
        this.s3 = s3;
    }

    @Autowired
    compiler compilerService;
    
    @PostMapping("/execute")
    public ResponseEntity<?> execute(@RequestBody JavaFile JFile) throws IOException
    {
        Map<String,String> res=new HashMap<>();
        String fileName=JFile.getFileName();

        if (!fileName.endsWith(".java")) {
            fileName = fileName + ".java";
        }

        String key=JFile.getEmail()+fileName;
        MultipartFile file=download(key,bucketName);


        if(file.isEmpty()) // file non empty honi chahiye
            return ResponseEntity.badRequest().body("Empty file uploaded");
        try {
            if (!file.getOriginalFilename().endsWith(".java"))// ends with shayad null pointer exception dede
                return ResponseEntity.badRequest().body("The Uploaded file is not a Java File");
        }
        catch (NullPointerException e)
        {
            return ResponseEntity.badRequest().body(e.toString());
        }

        if(!compilerService.uploadFile(JFile.getEmail(), file))
            {
                res.put("message","error");
                return ResponseEntity.badRequest().body(res);
            }
        Map<String,String> response=compilerService.compile();
        if(response.get("message").equals("false"))
        {
            return ResponseEntity.ok().body(response);
        }
        if(response.get("message").equals("error"))
        {
            return ResponseEntity.badRequest().body(response);
        }

        Map<String,String> compiler=compilerService.execute(JFile.getEmail());
        return ResponseEntity.ok().body(compiler);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Compiler service is up and running 🚀");
    }


    public MultipartFile download(String key,String bucket) throws IOException
    {
        ResponseInputStream<GetObjectResponse> s3Object =
                    s3.getObject(GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build());

        
        byte[] content = toByteArray(s3Object);

        return new S3MultipartFile(
                    key,
                    key,
                    s3Object.response().contentType(),
                    content
            );
    }

     private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] tmp = new byte[4096];
        int n;
        while ((n = input.read(tmp)) != -1) {
            buffer.write(tmp, 0, n);
        }
        return buffer.toByteArray();
    }
}
