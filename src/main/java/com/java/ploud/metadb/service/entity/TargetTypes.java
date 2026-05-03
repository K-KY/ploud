package com.java.ploud.metadb.service.entity;

public enum TargetTypes {
    DEL_DIR("DIR", "DEL_DIR:"), DEL_FILE("FILE", "DEL_FILE:")
    ;
    private final String type;
    private final String prefix;

     TargetTypes(String type, String prefix) {
        this.type = type;
        this.prefix = prefix;
    }

    public String getType() {
        return type;
    }

    public String getPrefix() {
         return prefix;
    }

    public boolean isDir() {
         return this == DEL_DIR;
    }

    public boolean isFile() {
         return this == DEL_FILE;
    }
}
