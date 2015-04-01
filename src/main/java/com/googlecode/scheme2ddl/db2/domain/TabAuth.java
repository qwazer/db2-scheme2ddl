package com.googlecode.scheme2ddl.db2.domain;

/**
 * Content of FROM SYSCAT.TABAUTH
 * Authorization Statements on Tables/Views
 *
 * @author ar
 * @since Date: 14.12.2014
 */
public class TabAuth {

    /*
CREATE TABLE TABAUTH
(
    GRANTOR VARCHAR(128) NOT NULL,
    GRANTORTYPE CHAR(1) NOT NULL,
    GRANTEE VARCHAR(128) NOT NULL,
    GRANTEETYPE CHAR(1) NOT NULL,
    TABSCHEMA VARCHAR(128) NOT NULL,
    TABNAME VARCHAR(128) NOT NULL,
    CONTROLAUTH CHAR(1) NOT NULL,
    ALTERAUTH CHAR(1) NOT NULL,
    DELETEAUTH CHAR(1) NOT NULL,
    INDEXAUTH CHAR(1) NOT NULL,
    INSERTAUTH CHAR(1) NOT NULL,
    REFAUTH CHAR(1) NOT NULL,
    SELECTAUTH CHAR(1) NOT NULL,
    UPDATEAUTH CHAR(1) NOT NULL
);

     */

    private String grantee;
    private GranteeType granteeType;
    private String tabSchema;
    private String tabName;
    private GrantType controlAuth;
    private GrantType alterAuth;
    private GrantType deleteAuth;
    private GrantType indexAuth;
    private GrantType insertAuth;
    private GrantType refAuth;
    private GrantType selectAuth;
    private GrantType updateAuth;


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

    public String getTabSchema() {
        return tabSchema;
    }

    public void setTabSchema(String tabSchema) {
        this.tabSchema = tabSchema;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public GrantType getControlAuth() {
        return controlAuth;
    }

    public void setControlAuth(GrantType controlAuth) {
        this.controlAuth = controlAuth;
    }

    public GrantType getAlterAuth() {
        return alterAuth;
    }

    public void setAlterAuth(GrantType alterAuth) {
        this.alterAuth = alterAuth;
    }

    public GrantType getDeleteAuth() {
        return deleteAuth;
    }

    public void setDeleteAuth(GrantType deleteAuth) {
        this.deleteAuth = deleteAuth;
    }

    public GrantType getIndexAuth() {
        return indexAuth;
    }

    public void setIndexAuth(GrantType indexAuth) {
        this.indexAuth = indexAuth;
    }

    public GrantType getInsertAuth() {
        return insertAuth;
    }

    public void setInsertAuth(GrantType insertAuth) {
        this.insertAuth = insertAuth;
    }

    public GrantType getRefAuth() {
        return refAuth;
    }

    public void setRefAuth(GrantType refAuth) {
        this.refAuth = refAuth;
    }

    public GrantType getSelectAuth() {
        return selectAuth;
    }

    public void setSelectAuth(GrantType selectAuth) {
        this.selectAuth = selectAuth;
    }

    public GrantType getUpdateAuth() {
        return updateAuth;
    }

    public void setUpdateAuth(GrantType updateAuth) {
        this.updateAuth = updateAuth;
    }

}
