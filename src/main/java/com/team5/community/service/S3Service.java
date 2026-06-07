package com.team5.community.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID; // UUID 임포트 추가

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file) throws IOException {
        // 1. UUID를 사용해 고유한 파일명 생성 (한글 깨짐 방지 및 중복 방지)
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 2. S3에 업로드
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        // 3. S3에서 생성된 실제 URL 리턴
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        try {
            // URL에서 마지막 "/" 이후의 파일명만 추출
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}