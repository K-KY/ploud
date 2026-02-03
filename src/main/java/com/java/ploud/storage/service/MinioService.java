package com.java.ploud.storage.service;

import com.java.ploud.storage.service.dto.PreSignedUrlDto;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.StringJoiner;

@Slf4j
@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucket;
    @Value("${minio.presign.expirySeconds}")
    private int defaultExpirySeconds; // 하루


    public MinioService(MinioClient minioClient, @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    public ObjectWriteResponse upload(MultipartFile multipartFile, String ownerId, String group) throws Exception {
        checkBucket();

        //스토리지 키는 사용자 명 + 그룹 + 파일 이름
        String storageKey = makeStorageName(ownerId, group, multipartFile);

        try (InputStream is = multipartFile.getInputStream()) {
            return minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageKey)
                            .stream(is, multipartFile.getSize(), -1)
                            .contentType(multipartFile.getContentType())
                            .build()
            );
        }
    }

    public Iterable<Result<Item>> readCurrentList(String ownerId, String location) {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(bucket)
                .delimiter("/")
                .prefix(makeStorageName(ownerId, location))
                .recursive(false)
                .build());

        return results;
    }

    private void checkBucket() throws InvalidKeyException, IOException, NoSuchAlgorithmException {
        try {
            //버킷 있는지 찾기
            boolean found = minioClient.bucketExists(io.minio.BucketExistsArgs.builder().bucket(bucket).build());
            //없으면 만들고
            if (!found) {
                minioClient.makeBucket(io.minio.MakeBucketArgs.builder().bucket(bucket).build());
            }
            //안되면 예외
        } catch (MinioException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ownerId
     * @param group
     * @param multipartFile
     * @return
     * @apiNote ownerId/group/multipartFileName 와 같은 "파일 이름"을 만들어 반환
     * @note group 삭제 예정
     */

    private String makeStorageName(String ownerId, String group, MultipartFile multipartFile) {
        StringJoiner stringJoiner = new StringJoiner("/", "/", "");
        stringJoiner.add(ownerId);
        if (group != null) {
            stringJoiner.add(group);
        }
        stringJoiner.add(multipartFile.getOriginalFilename());

        return stringJoiner.toString();
    }

    /**
     * @param ownerId
     * @param location
     * @return
     * @apiNote - ownerId/group/ 와 같은 "경로"를 만들어 반환
     */
    private String makeStorageName(String ownerId, String location) {
        StringJoiner stringJoiner = new StringJoiner("/", "", "/");
        stringJoiner.add(ownerId);
        if (location != null) {
            stringJoiner.add(location);
        }
        return stringJoiner.toString();
    }

    public List<PreSignedUrlDto.Response> getPreSignedUrl(String ownerId, List<PreSignedUrlDto.Request> fileNames) {
        return fileNames.stream()
                .map(n -> new PreSignedUrlDto
                        .Response(getPreSignedUrl(ownerId, n.getFileName()), n.getFileId(), n.getFileName()))
                .toList();
    }

    public String getPreSignedUrl(String ownerId, String fileName) {
        if (checkExists(fileName)) {
            throw new IllegalArgumentException("File Name already exists : [" + fileName + "]");
        }

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(makeStorageName(ownerId, fileName))
                            .method(Method.PUT)
                            .expiry(defaultExpirySeconds)
                            .build()
            );
        } catch (MinioException e) {
            log.error("스토리지에 업로드 과정 중 예외 발생");
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    public Boolean checkExists(String fileName) {
        try {
            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .build()
            );
            return true; // 존재
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false; // 없음
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
