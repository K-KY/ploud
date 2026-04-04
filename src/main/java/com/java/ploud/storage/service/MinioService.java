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
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
public class MinioService {
    private static final String URL = "URL";
    private static final String STORAGE_KEY = "storageKey";
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

    public Iterable<Result<Item>> readCurrentList(Long userSeq, String location) {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs
                .builder()
                .bucket(bucket)
                .delimiter("/")
                .prefix(makeStorageName(userSeq, location))
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
     * @param location
     * @return
     * @apiNote - ownerId/group/ 와 같은 "경로"를 만들어 반환
     */
    private String makeStorageName(Long userSeq, String location) {
        StringJoiner stringJoiner = new StringJoiner("/", "", "");
        stringJoiner.add(String.valueOf(userSeq));
        if (location != null) {
            stringJoiner.add(location);
        }
        return Normalizer.normalize(stringJoiner.toString(), Normalizer.Form.NFC);
    }

    public List<PreSignedUrlDto.Response> getPreSignedUrl(Long userSeq, List<PreSignedUrlDto.Request> fileNames) {
        return fileNames.stream()
                .map(n -> {
                    Map<String, String> preSignedUrl = getPreSignedUrl(userSeq, n.getFileName());
                    return new PreSignedUrlDto
                            .Response(
                            preSignedUrl.get(URL)
                            , n.getFileId(), n.getFileName()
                            ,preSignedUrl.get(STORAGE_KEY)
                    );
                })
                .toList();
    }

    public Map<String, String> getPreSignedUrl(Long userSeq, String fileName) {
        if (checkExists(makeStorageName(userSeq, fileName))) {
                int extensionStart = fileName.lastIndexOf('.');
                String name = fileName.substring(0, extensionStart - 1);
                String extension = fileName.substring(extensionStart);
                fileName = name + "-" + UUID.randomUUID().toString().substring(0, 8) + extension;
//            throw new IllegalArgumentException("File Name already exists : [" + fileName + "]");
        }

        try {
            String name = makeStorageName(userSeq, fileName);
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
    public String getDownloadUrl(String storageKey) throws Exception {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .object(storageKey)
                        .expiry(60 * 60 * 24) // 24시간
                        .build()
        );
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
