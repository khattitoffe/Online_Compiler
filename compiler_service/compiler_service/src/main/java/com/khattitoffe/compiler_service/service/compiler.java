package com.khattitoffe.compiler_service.service;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class compiler {
    private String filePath="";
    private String className="";//filename without .java

    public boolean uploadFile(String email,MultipartFile file)
    {
        String fileDir="E:\\Spring Boot\\data\\src\\java\\"+email+"\\";

        String filename=file.getOriginalFilename();
        filename=FileNameFix(filename);
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

            filePath=fileDir+filename;
            className = filename.replace(".java", "");
            return true;//file uploaded
        }
        catch (IOException e)
        {
            return false;//file not uploaded
        }
    }

    public String FileNameFix(String filename)
    {
        String marker = ".com";
        int dotComIndex = filename.indexOf(marker);

        if (dotComIndex != -1) {
            // Skip past ".com"
            return filename.substring(dotComIndex + marker.length());
        }

        // If no ".com" found, return original filename
        return filename;
    }

    public Map<String,String> compile() {
        Map<String, String> response = new HashMap<>();
        try {
            ProcessBuilder pb = new ProcessBuilder("javac", filePath);
            pb.redirectErrorStream(true); // combine stdout and stderr
            Process process = pb.start();

            String output = readProcessOutput(process.getInputStream());
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                response.put("message","true");
                response.put("output",output);
                return response;
            } else {
                response.put("message","false");
                response.put("output",output);
                return response;
            }
        } catch (Exception e) {
            response.put("message","error");
                return response;
        }
    }

    public Map<String,String> execute(String email) {
        Map<String, String> response = new HashMap<>();
        try {
            String fileDir = "E:/Spring Boot/data/src/java/" + email + "/";
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", fileDir, className);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            String output = readProcessOutput(process.getInputStream());
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                response.put("message","true");
                response.put("output",output);
                return response;
            } else {
                response.put("message","false");
                response.put("output",output);
                return response;
            }
        } catch (Exception e) {
            response.put("message","error");
            return response;
        }
    }

    private String readProcessOutput(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
