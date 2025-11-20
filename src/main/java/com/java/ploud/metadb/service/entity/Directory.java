package com.java.ploud.metadb.service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Directory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long folderSeq;
    private String folderName;

    @ManyToOne(fetch = FetchType.EAGER)
    private Directory parent;
}
