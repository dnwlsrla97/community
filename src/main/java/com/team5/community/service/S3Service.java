package com.team5.community.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    
    private final AmazonS3 amazonS3; // 인터페이스로 선언하여 타입 오류 방지
    private final String bucket = "team5-s3-719279823221-ap-northeast-2-an";

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata));
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        try {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            amazonS3.deleteObject(bucket, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}