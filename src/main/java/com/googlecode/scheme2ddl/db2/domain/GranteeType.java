package com.googlecode.scheme2ddl.db2.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ar
 * @since Date: 01.04.2015
 */
public enum GranteeType {
    USER("U", "USER"),
    GROUP("G", "GROUP"),
    ROlE("R", "ROLE"),
    PUBLIC("", "");

    private String code;
    private String sql;

    private static Map<String, GranteeType> map;

    static {
        map = new HashMap<String, GranteeType>();

        for (GranteeType granteeType : GranteeType.values()) {
            map.put(granteeType.getCode(), granteeType);
        }
    }


    GranteeType(String code, String sql) {
        this.code = code;
        this.sql = sql;
    }

    public String getCode() {
        return code;
    }

    public String getSql() {
        return sql;
    }

    public static GranteeType mapByCode(String code) {
        return map.get(code);
    }
}
