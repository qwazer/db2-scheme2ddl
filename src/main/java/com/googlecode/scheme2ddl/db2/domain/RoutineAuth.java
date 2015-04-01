package com.googlecode.scheme2ddl.db2.domain;

import java.util.HashMap;
import java.util.Map;

/**
 *
 *  * Content of FROM SYSCAT.TABAUTH
 * Authorization Statements on routines
 *
 * @author ar
 * @since Date: 01.04.2015
 */
public class RoutineAuth {
    /*
    CREATE TABLE "ROUTINEAUTH"
(
    "GRANTEE" VARCHAR(128) NOT NULL,
    "GRANTEETYPE" CHAR(1) NOT NULL,
    "SCHEMA" VARCHAR(128) NOT NULL,
    "SPECIFICNAME" VARCHAR(128),
    "ROUTINETYPE" CHAR(1) NOT NULL,
    "EXECUTEAUTH" CHAR(1) NOT NULL,
);

     */

    private String grantee;
    private GranteeType granteeType;
    private String schema;
    private String specificname;
    private RoutineType routineType;
    private GrantType executeAuth;


    /**
     * @author ar
     * @since Date: 01.04.2015
     */
    public enum RoutineType {
        FUNCTION("F"),
        METHOD("M"),
        PROCEDURE("P");

        private String code;

        private static Map<String, RoutineType> map;

        static {
            map = new HashMap<String, RoutineType>();

            for (RoutineType routineType : RoutineType.values()) {
                map.put(routineType.getCode(), routineType);
            }
        }

        RoutineType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static RoutineType mapByCode(String code) {
            return map.get(code);
        }
    }

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    public GranteeType getGranteeType() {
        return granteeType;
    }

    public void setGranteeType(GranteeType granteeType) {
        this.granteeType = granteeType;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSpecificname() {
        return specificname;
    }

    public void setSpecificname(String specificname) {
        this.specificname = specificname;
    }

    public RoutineType getRoutineType() {
        return routineType;
    }

    public void setRoutineType(RoutineType routineType) {
        this.routineType = routineType;
    }

    public GrantType getExecuteAuth() {
        return executeAuth;
    }

    public void setExecuteAuth(GrantType executeAuth) {
        this.executeAuth = executeAuth;
    }
}
