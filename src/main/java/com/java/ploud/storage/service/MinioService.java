package com.java.ploud.storage.service;

import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.StringJoiner;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucket;
    @Value("${minio.presign.expirySeconds}")
    private int defaultExpirySeconds;


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
     * @apiNote ownerId/group/multipartFileName 와 같은 "파일 이름"을 만들어 반환
     * @param ownerId
     * @param group
     * @param multipartFile
     * @return
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
     * @apiNote - ownerId/group/ 와 같은 "경로"를 만들어 반환
     * @param ownerId
     * @param group
     * @return
     */
    private String makeStorageName(String ownerId, String group) {
        StringJoiner stringJoiner = new StringJoiner("/", "", "/");
        stringJoiner.add(ownerId);
        if (group != null) {
            stringJoiner.add(group);
        }
        return stringJoiner.toString();
    }
}
