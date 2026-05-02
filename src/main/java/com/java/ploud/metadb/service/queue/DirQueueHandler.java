package com.java.ploud.metadb.service.queue;

import com.java.ploud.metadb.service.DirectoryService;
import com.java.ploud.metadb.service.dto.DirDto;
import com.java.ploud.metadb.service.dto.QueueMessage;
import com.java.ploud.metadb.service.entity.Directory;
import com.java.ploud.metadb.service.entity.TargetTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DirQueueHandler implements QueueHandler {
    private final DirectoryService directoryService;

    public DirQueueHandler(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @Override
    public String getType() {
        return TargetTypes.DIR.getType();
    }

    @Override
    public void handle(QueueMessage msg) {
        log.info("dir queue received: {}", msg.queueId());
        //발행된 메세지의 디렉토리 삭제 수행
        directoryService.deleteDirSoft(msg.userSeq(), new DirDto.Request(msg.targetSeq()));

        //하위 항목 조회
        List<Directory> childDir = directoryService.findChildDir(msg.userSeq(), msg.targetSeq());
        log.info("childDir = {}, size = {}", childDir, childDir.size());
        childDir.forEach(child -> {
            //하위 디렉토리 항목 큐에 등록
            directoryService.inDeleteQueue(msg.userSeq(), child.getDirSeq());
        });
        //파일 서비스에서 큐 등록 호출 해야함
    }
}
