package com.java.ploud.metadb.service.dto;

public record QueueMessage(String queueId, String type, Long userSeq, Long targetSeq) {
}
