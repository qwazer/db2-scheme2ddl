package com.googlecode.scheme2ddl.db2.dao;

import com.googlecode.scheme2ddl.db2.domain.DB2ObjectType;
import com.googlecode.scheme2ddl.db2.domain.Db2LookInfo;
import com.googlecode.scheme2ddl.db2.domain.UserObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author A_Reshetnikov
 * @since Date: 17.10.2012
 */
@Component(value = "userObjectDao")
@Scope(value = "step")
public class UserObjectDaoDb2Impl extends JdbcDaoSupport implements UserObjectDao {

    private static final Log log = LogFactory.getLog(UserObjectDaoDb2Impl.class);
    @Value("#{jobParameters['schemaName']}")
    private String schemaName;

    private Set<String> nonPrimaryTypes;

    @Resource(name = "dependencies")
    private Map<DB2ObjectType, Set<DB2ObjectType>> dependencies;


    @Autowired
    public UserObjectDaoDb2Impl(DataSource dataSource) {
        setDataSource(dataSource);

    }

    @PostConstruct
    public void setNonPrimaryTypesFromDependencies() {
        nonPrimaryTypes = new HashSet<String>();
        for (DB2ObjectType key : dependencies.keySet()) {
            for (DB2ObjectType depended : dependencies.get(key)) {
                nonPrimaryTypes.add(depended.getInternalName());
            }
        }
    }

    public List<UserObject> findListForProccessing() {


        long opToken = call_DB2LK_GENERATE_DDL("-e -z " + schemaName);

        log.debug("findListForProccessing opToken is " + opToken);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("opToken", opToken);
        parameters.addValue("schemaName", schemaName);


        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());

        String sql = "SELECT DISTINCT OBJ_TYPE, OBJ_NAME, OP_TOKEN  " +
                "FROM SYSTOOLS.DB2LOOK_INFO WHERE OP_TOKEN=:opToken AND OBJ_SCHEMA=:schemaName " ;
        if (nonPrimaryTypes != null && !nonPrimaryTypes.isEmpty()) {
            parameters.addValue("nonPrimaryTypes", nonPrimaryTypes);
            sql += " and OBJ_TYPE not in (:nonPrimaryTypes) ";
        }

        return namedParameterJdbcTemplate.query(sql, parameters, new UserObjectRowMapper());
    }


    private long call_DB2LK_GENERATE_DDL(String db2lookinfoParams) {   //todo rename
        long opToken = 0;
        Connection con = null;
        //todo rewrite with spring

        try {
            con = getDataSource().getConnection();
            CallableStatement cstmt;
            ResultSet rs;
            cstmt = con.prepareCall("CALL SYSPROC.DB2LK_GENERATE_DDL(?, ?)");
            cstmt.setString(1, db2lookinfoParams);
            cstmt.registerOutParameter(2, Types.BIGINT);
            cstmt.executeUpdate();
            opToken = cstmt.getLong(2);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return opToken;

    }

    public List<Db2LookInfo> findDDLs(UserObject userObject) {
        return getJdbcTemplate().query("select OP_SEQUENCE, SQL_STMT, OBJ_SCHEMA, OBJ_TYPE, OBJ_NAME, SQL_OPERATION " +
                        "FROM SYSTOOLS.DB2LOOK_INFO where OP_TOKEN=? and OBJ_SCHEMA=? and OBJ_TYPE=? and OBJ_NAME=?",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getType(), userObject.getName()},
                new Db2LookInfoRowMapper());

    }

    public List<Db2LookInfo> findTableColumns(UserObject userObject) {
        if (!"TABLE".equals(userObject.getType())) {
            throw new IllegalArgumentException();
        }

        List<Db2LookInfo> list = getJdbcTemplate().query(
                "SELECT * " +
                        " FROM SYSTOOLS.DB2LOOK_INFO t " +
                        " WHERE OBJ_TYPE = 'COLUMN' " +
                        "      AND OP_TOKEN = ? " +
                        "      and OBJ_SCHEMA=? " +
                        "      and OBJ_NAME like ? " +
                        "     ",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getName() + "\".\"%"},
                new Db2LookInfoRowMapper());

        return list;
    }

    public List<Db2LookInfo> findTableIndexes(UserObject userObject) {

        if (!"TABLE".equals(userObject.getType())) {
            throw new IllegalArgumentException();
        }

        List<Db2LookInfo> list = getJdbcTemplate().query(
                "SELECT * " +
                        " FROM SYSTOOLS.DB2LOOK_INFO t " +
                        " WHERE OBJ_TYPE = 'INDEX' " +
                        "      AND OP_TOKEN = ? " +
                        "      AND exists( " +
                        "    SELECT 1 " +
                        "    FROM SYSCAT.INDEXES i " +
                        "    WHERE TABSCHEMA = ? AND TABNAME = ? AND i.INDNAME = t.OBJ_NAME ) ",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getName()},

                new Db2LookInfoRowMapper());


        return list;
    }

    public List<Db2LookInfo> findTableFkeys(UserObject userObject) {
        List<Db2LookInfo> list = getJdbcTemplate().query(
                "SELECT * " +
                        " FROM SYSTOOLS.DB2LOOK_INFO t " +
                        " WHERE OBJ_TYPE = 'FKEY' " +
                        "      AND OP_TOKEN = ? " +
                        "      and OBJ_SCHEMA=? " +
                        "      and OBJ_NAME=? " +
                        "     ",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getName()},

                new Db2LookInfoRowMapper());


        return list;
    }

    public List<Db2LookInfo> findTableChecks(UserObject userObject) {
        List<Db2LookInfo> list = getJdbcTemplate().query(
                "SELECT * " +
                        " FROM SYSTOOLS.DB2LOOK_INFO t " +
                        " WHERE OBJ_TYPE = 'CHECK' " +
                        "      AND OP_TOKEN = ? " +
                        "      and OBJ_SCHEMA=? " +
                        "      and OBJ_NAME=? " +
                        "     ",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getName()},

                new Db2LookInfoRowMapper());


        return list;
    }

    public List<Db2LookInfo> findTablePkeys(UserObject userObject) {
        List<Db2LookInfo> list = getJdbcTemplate().query(
                "SELECT * " +
                        " FROM SYSTOOLS.DB2LOOK_INFO t " +
                        " WHERE OBJ_TYPE = 'PKEY' AND OP_TOKEN = ?  AND t.OBJ_SCHEMA = ? AND OBJ_NAME = ?" +
                        "      AND exists( " +
                        "    SELECT 1 " +
                        "    FROM SYSCAT.REFERENCES R " +
                        "    WHERE R.TABSCHEMA = t.OBJ_SCHEMA " +
                        "          AND R.TABNAME = t.OBJ_NAME)",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getName()},

                new Db2LookInfoRowMapper());


        return list;
    }

    public List<Db2LookInfo> findTableUniques(UserObject userObject) {
        List<Db2LookInfo> list = getJdbcTemplate().query(
                "SELECT * " +
                        " FROM SYSTOOLS.DB2LOOK_INFO t " +
                        " WHERE OBJ_TYPE = 'UNIQUE' AND OP_TOKEN = ?  AND t.OBJ_SCHEMA = ? AND OBJ_NAME = ?",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getName()},
                new Db2LookInfoRowMapper());
        return list;
    }

    public void callCleanTable(Long opToken) {

        getJdbcTemplate().execute(String.format("call SYSPROC.DB2LK_CLEAN_TABLE(%d)", opToken));
        //todo rewrite with ps
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }


    private class UserObjectRowMapper implements RowMapper<UserObject> {
        public UserObject mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserObject userObject = new UserObject();
            userObject.setName(rs.getString("OBJ_NAME"));
            userObject.setType(rs.getString("OBJ_TYPE"));
            userObject.setOpToken(rs.getLong("OP_TOKEN"));
            userObject.setSchema(schemaName == null ? "" : schemaName);
            return userObject;
        }
    }

    private class Db2LookInfoRowMapper implements RowMapper<Db2LookInfo> {
        public Db2LookInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Db2LookInfo db2LookInfo = new Db2LookInfo();
            db2LookInfo.setObjName(rs.getString("OBJ_NAME"));
            db2LookInfo.setObjType(rs.getString("OBJ_TYPE"));
            db2LookInfo.setObjSchema(rs.getString("OBJ_SCHEMA").trim());
            db2LookInfo.setOpSequence(rs.getLong("OP_SEQUENCE"));
            db2LookInfo.setSqlOperation(rs.getString("SQL_OPERATION"));
            db2LookInfo.setSqlStmtClob(rs.getClob("SQL_STMT"));

            if (db2LookInfo.getSqlStmtClob() != null) {

                if ((int) db2LookInfo.getSqlStmtClob().length() > 0) {
                    String s = db2LookInfo.getSqlStmtClob().getSubString(1, (int) db2LookInfo.getSqlStmtClob().length());
                    db2LookInfo.setSqlStmt(s);
                }
            }

            return db2LookInfo;
        }
    }

}
