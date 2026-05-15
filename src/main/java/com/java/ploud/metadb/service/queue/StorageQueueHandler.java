package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.DeleteQueueService;
import com.java.ploud.metadb.service.StorageCleanService;
import com.java.ploud.metadb.service.dto.QueueMessage;
import com.java.ploud.metadb.service.entity.StorageCleanQueue;
import com.java.ploud.metadb.service.entity.TargetTypes;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class StorageQueueHandler implements QueueHandler {
    private final StorageCleanService storageCleanService;
    private final DeleteQueueService deleteQueueService;
    private final MinioClient minioClient;
    private final String bucket;

    public StorageQueueHandler(StorageCleanService storageCleanService, DeleteQueueService deleteQueueService,
                               MinioClient minioClient, @Value("${minio.bucket}") String bucket) {
        this.storageCleanService = storageCleanService;
        this.deleteQueueService = deleteQueueService;
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @Override
    public String getType() {
        return TargetTypes.DEL_STORAGE.getType();
    }

    @Override
    public void handle(QueueMessage queueMessage) {
        log.info("storage queue received: {}", queueMessage);
        deleteQueueService.execute(queueMessage.queueId());//백업 큐 수정
        //스토리지 삭제 큐 조회
        StorageCleanQueue storageCleanQueue = storageCleanService.getAndExecute(queueMessage.userSeq(), queueMessage.targetSeq());
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(storageCleanQueue.getStorageKey())
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | XmlParserException | ServerException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }
}
