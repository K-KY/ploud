package com.java.ploud.metadb.service.entity;

public enum TargetTypes {
    DIR("DIR"), FILE("DIR")
    ;
    public String type;

     TargetTypes(String type) {
        this.type = type;
    }


}
