package com.googlecode.scheme2ddl.db2.domain;

/**
 * @author ar
 * @since Date: 14.12.2014
 */
public enum DB2ObjectType {
    ALIAS(null),
    CHECK(null),
    COLUMN(null),
    FOREIGN_KEY("FKEY"),
    MODULE(null),
    MQT(null),
    INDEX(null),
    NICKNAME(null),
    PACKAGE(null),
    PRIMARY_KEY("PKEY"),
    PROCEDURE(null),
    REFERENCE(null),
    ROUTINE(null),
    SCHEMA(null),
    SEQUENCE(null),
    TABLE(null),
    TABLESPACE(null),
    TRIGGER(null),
    UDF(null),
    UDT(null),
    UNIQUE(null),
    VIEW(null),
    VARIABLE(null),
    XML_SCHEMA(null),

    OBJECT_GRANTS(null);
    
    private String internalName;

    DB2ObjectType(String internalName) {
        this.internalName = internalName;
    }

    public String getInternalName() {
        return internalName == null ? name() : internalName;
    }
}