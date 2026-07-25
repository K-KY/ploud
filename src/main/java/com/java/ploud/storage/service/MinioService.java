package com.java.ploud.storage.service;

import com.java.ploud.storage.service.dto.FileDeleteDto;
import com.java.ploud.storage.service.dto.PreSignedUrlDto;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class MinioService {
    private static final String URL = "URL";
    private static final String STORAGE_KEY = "storageKey";

    private final MinioClient minioClient;
    private final MinioClient internalMinioClient;
    private final String bucket;
    @Value("${minio.presign.expirySeconds}")
    private int defaultExpirySeconds; // 하루


    public MinioService(@Qualifier("externalMinioClient") MinioClient minioClient,
                        @Qualifier("internalMinioClient") MinioClient internalMinioClient, @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.internalMinioClient = internalMinioClient;
        this.bucket = bucket;
    }

    public Iterable<Result<Item>> readCurrentList(Long userSeq, String location) {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(bucket)
                .delimiter("/")
                .prefix(makeStorageName(location))
                .recursive(false)
                .build());

        return results;
    }

    private void checkBucket() throws InvalidKeyException, IOException, NoSuchAlgorithmException {
        try {
            //버킷 있는지 찾기
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            //없으면 만들고
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
            //안되면 예외
        } catch (MinioException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param location
     * @return
     * @apiNote - ownerId/group/ 와 같은 "경로"를 만들어 반환
     */
    private String makeStorageName(String location) {
        String[] split = location.split("\\.");
        return makeRandomString() + "." + split[split.length - 1];
    }

    private String makeRandomString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public List<PreSignedUrlDto.Response> getPreSignedUrl(Long userSeq, List<PreSignedUrlDto.Request> fileNames) {
        return fileNames.stream()
                .map(n -> {
                    Map<String, String> preSignedUrl = getPreSignedUrl(userSeq, n.getFileName());
                    return new PreSignedUrlDto
                            .Response(
                            preSignedUrl.get(URL)
                            , String.valueOf(n.getFileId()), n.getFileName()
                            , preSignedUrl.get(STORAGE_KEY)
                    );
                })
                .toList();
    }

    public Map<String, String> getPreSignedUrl(Long userSeq, String fileName) {
        try {
            String name = makeStorageName(fileName);
            log.info("Try get Pre-signed URL: {}", name);
            String presignedObjectUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucket)
                            .object(name)
                            .method(Method.PUT)
                            .expiry(defaultExpirySeconds)
                            .build()
            );
            return Map.of(URL, presignedObjectUrl, STORAGE_KEY, name);
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

    //다운로드 용 preSignedUrl 메서드
    public PreSignedUrlDto.Response getDownloadUrl(Long userSeq, PreSignedUrlDto.Request dto) {
        try {
            String presignedObjectUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(dto.getStorageKey())
                            .extraQueryParams(Map.of(
                                    "response-content-disposition", "attachment; filename=\"" + dto.getFileName()
                            ))
                            .expiry(60 * 60 * 24) // 24시간
                            .build()
            );


            return new PreSignedUrlDto.Response(presignedObjectUrl, null, null, null);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(Long userSeq, FileDeleteDto dto) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(dto.getStorageKey())
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | XmlParserException | ServerException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

    /**
     * 스토리지 키에 매핑된 파일 조회
     * @param storageKey
     * @return
     */
    public InputStream getObject(String storageKey) {
        try {
            log.info("Get object from minio storage: {}", storageKey);
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageKey)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object from MinIO: " + storageKey, e);
        }
    }

    /**
     * 스토리지 키에 매핑된 파일의 사이즈 반환
     * @param storageKey
     * @return
     */
    public long getObjectSize(String storageKey) {
        log.info("Get object size from minio storage: {}", storageKey);
        try {
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageKey)
                            .build()
            );

            return stat.size();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get object size from MinIO: " + storageKey, e);
        }
    }

}
