package com.java.ploud.metadb.service.entity;

public enum TargetTypes {
    DIR("DIR"), FILE("FILE")
    ;
    private final String type;

     TargetTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
