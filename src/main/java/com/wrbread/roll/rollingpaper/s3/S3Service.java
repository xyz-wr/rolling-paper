package com.wrbread.roll.rollingpaper.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.wrbread.roll.rollingpaper.exception.BusinessLogicException;
import com.wrbread.roll.rollingpaper.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;


    /** s3에 파일 업로드 */
    public String uploadS3(String oriFileName, String folderName, byte[] fileData) throws IOException {
        if (!isAllowedImgExtension(oriFileName)) {
            throw new BusinessLogicException(ExceptionCode.UNSUPPORTED_FILE_EXTENSION);
        }

        String fileName = generateImgName(oriFileName);
        String folder = folderName + "/";
        String folderKey = folder + fileName;

        try {
            // S3 버킷 내 객체 업로드
            ByteArrayInputStream input = new ByteArrayInputStream(fileData);
            amazonS3.putObject(bucketName, folderKey, input, getObjectMetadata(fileData));

            // 업로드 된 객체의 URL 생성
            String fileUploadUrl = amazonS3.getUrl(bucketName, folderKey).toString();
            return fileUploadUrl;
        } catch (SdkClientException e) {
            throw new IOException("Error uploading file to S3", e);
        }
    }

    /** s3에 업로드된 파일 삭제 */
    public void deleteS3(String key) {
        try {
            amazonS3.deleteObject(bucketName, key);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        }
    }

    /** 파일의 메타데이터 설정 */
    private ObjectMetadata getObjectMetadata(byte[] fileData) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileData.length);
        return objectMetadata;
    }

    /** 파일명 변환 */
    private String generateImgName(String oriFileName) {
        String extension = oriFileName.substring(oriFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + "-" + extension;
    }

    /** 파일 확장자 설정 */
    public boolean isAllowedImgExtension(String fileName) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png"};
        String fileExtension = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        for (String extension : allowedExtensions) {
            if (fileExtension.equals(extension)) {
                return true;
            }
        }
        return false;
    }

}
