package com.khattitoffe.WebBluej.service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.springframework.stereotype.Service;

@Service
public class FileGet {

    private final String bucketName = "myprojectjavafiles"; 
    private S3Client s3=null;


    public HashMap<String, String> getClasses(String email,S3Client s3) throws IOException {
        this.s3=s3;

        HashMap<String, String> classes = new HashMap<>();


        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(email) 
                .build();

        ListObjectsV2Response listRes = s3.listObjectsV2(listReq);

        for (S3Object s3Object : listRes.contents()) {
            String key = s3Object.key();

            // check for .java files
            if (!key.endsWith(".java")) continue;

            GetObjectRequest getReq = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(s3.getObject(getReq)))) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                String filename = key.substring(email.length());
                classes.put(filename, content.toString());
            }
        }

        return classes;
    }
}

