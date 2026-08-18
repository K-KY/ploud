package com.java.ploud.metadb.service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class SharedFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sharedFileSeq;
}
