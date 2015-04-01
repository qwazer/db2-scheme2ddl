package com.googlecode.scheme2ddl.db2.domain;

/**
 * @author ar
 * @since Date: 01.04.2015
 */
public enum GrantType {
    G("WITH GRANT OPTION"),
    N(""),
    Y("");

    private String sql;

    GrantType(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
