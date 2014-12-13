package com.googlecode.scheme2ddl.db2.dao;

import com.googlecode.scheme2ddl.db2.domain.Db2LookInfo;
import com.googlecode.scheme2ddl.db2.domain.UserObject;
import com.googlecode.scheme2ddl.db2.domain.Db2LookInfoComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Set;

/**
 * @author A_Reshetnikov
 * @since Date: 17.10.2012
 */
@Component (value = "userObjectDao")
@Scope(value = "step")
public class UserObjectDaoDb2Impl extends JdbcDaoSupport implements UserObjectDao {

    private static final Log log = LogFactory.getLog(UserObjectDaoDb2Impl.class);
    @Value("#{jobParameters['schemaName']}")
    private String schemaName;

    @Resource(name="nonPrimaryTypes")
    private List<String> nonPrimaryTypes;


    @Autowired
    public UserObjectDaoDb2Impl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<UserObject> findListForProccessing() {


        long opToken = call_DB2LK_GENERATE_DDL("-e -z " + schemaName);

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("opToken", opToken);
        parameters.addValue("schemaName", schemaName);
        parameters.addValue("nonPrimaryTypes", nonPrimaryTypes);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getJdbcTemplate());

        final String sql;
           // sql = "select OBJECT_NAME, OBJECT_TYPE from SYSIBMADM.ALL_OBJECTS where OBJECT_SCHEMA = '"+schemaName+"' ";
            sql = "SELECT DISTINCT OBJ_TYPE, OBJ_NAME, OP_TOKEN  " +
                    "FROM SYSTOOLS.DB2LOOK_INFO WHERE OP_TOKEN=:opToken AND OBJ_SCHEMA=:schemaName and OBJ_TYPE not in (:nonPrimaryTypes) ";
        return namedParameterJdbcTemplate.query(sql,  parameters, new UserObjectRowMapper());
    }


    private long call_DB2LK_GENERATE_DDL(String db2lookinfoParams){   //todo rename
        long opToken = 0;
        Connection con =null;

        try {
            con = getDataSource().getConnection();
            CallableStatement cstmt;
            ResultSet rs;
            cstmt = con.prepareCall("CALL SYSPROC.DB2LK_GENERATE_DDL(?, ?)");
            cstmt.setString(1, db2lookinfoParams);
            cstmt.registerOutParameter(2, Types.BIGINT);
            cstmt.executeUpdate();
            opToken = cstmt.getLong(2);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (con!=null){
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return opToken;

    }

    public String findPrimaryDDL(final String type, final String name) {


        int opToken = 0;
        Connection con =null;

        String result = "";

        try {
             con= getDataSource().getConnection();
            CallableStatement cstmt;
            ResultSet rs;
            String db2lookinfoParams;
            if (type.equals("TABLE")) db2lookinfoParams = "-e -t " + schemaName + "." + name +" -xd";
            else if (type.equals("VIEW")) db2lookinfoParams = "-e -v " + schemaName + "." + name +" -xd";
            else  db2lookinfoParams = "-e -xd";
            cstmt = con.prepareCall("call SYSPROC.DB2LK_GENERATE_DDL(?, ?)");
            cstmt.setString(1, db2lookinfoParams);
            cstmt.registerOutParameter (2, Types.INTEGER);
            cstmt.executeUpdate();
            opToken = cstmt.getInt(2);

            List<Db2LookInfo> list = null;
            if (type.equals("TABLE") || type.equals("VIEW"))   {
                list = getJdbcTemplate().query("select OP_SEQUENCE, SQL_STMT, OBJ_SCHEMA, OBJ_TYPE, OBJ_NAME, SQL_OPERATION " +
                                "FROM SYSTOOLS.DB2LOOK_INFO where OP_TOKEN=? and OBJ_SCHEMA=? ",
                        new Object[]{opToken, schemaName},
                        new RowMapper<Db2LookInfo>() {
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
                        });
            }   else {
                list = getJdbcTemplate().query("select OP_SEQUENCE, SQL_STMT, OBJ_SCHEMA, OBJ_TYPE, OBJ_NAME, SQL_OPERATION " +
                                "FROM SYSTOOLS.DB2LOOK_INFO where OP_TOKEN=? and OBJ_SCHEMA=? and OBJ_TYPE=? and OBJ_NAME=?",
                        new Object[]{opToken, schemaName, type, name},
                        new RowMapper<Db2LookInfo>() {
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
                        });
            }



            list.sort(new Db2LookInfoComparator());
            for (Db2LookInfo db2LookInfo : list){
                result = result + db2LookInfo.getSqlStmt() + "\n;";  //todo config format options
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (con!=null){
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

         return result ;

    }

    public List<Db2LookInfo> findDDL(UserObject userObject) {
        return  getJdbcTemplate().query("select OP_SEQUENCE, SQL_STMT, OBJ_SCHEMA, OBJ_TYPE, OBJ_NAME, SQL_OPERATION " +
                        "FROM SYSTOOLS.DB2LOOK_INFO where OP_TOKEN=? and OBJ_SCHEMA=? and OBJ_TYPE=? and OBJ_NAME=?",
                new Object[]{userObject.getOpToken(), schemaName, userObject.getType(), userObject.getName()},
                new RowMapper<Db2LookInfo>() {
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
                });

    }

    public String findDependentDLLByTypeName(final String type, final String name) {


        if (type.equals("VIEW"))   {
            int opToken = 0;
            Connection con =null;

            String result = "";

            try {
                con= getDataSource().getConnection();
                CallableStatement cstmt;
                ResultSet rs;
                String db2lookinfoParams = "-e -t " + schemaName + "." + name +" -xd";

                cstmt = con.prepareCall("call SYSPROC.DB2LK_GENERATE_DDL(?, ?)");
                cstmt.setString(1, db2lookinfoParams);
                cstmt.registerOutParameter (2, Types.INTEGER);
                cstmt.executeUpdate();
                opToken = cstmt.getInt(2);

                List<Db2LookInfo> list = null;

                    list = getJdbcTemplate().query("select OP_SEQUENCE, SQL_STMT, OBJ_SCHEMA, OBJ_TYPE, OBJ_NAME, SQL_OPERATION " +
                                    "FROM SYSTOOLS.DB2LOOK_INFO where OP_TOKEN=? and OBJ_SCHEMA=? ",
                            new Object[]{opToken, schemaName},
                            new RowMapper<Db2LookInfo>() {
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
                            });


                list.sort(new Db2LookInfoComparator());
                for (Db2LookInfo db2LookInfo : list){
                    result = result + db2LookInfo.getSqlStmt() + "\n;";  //todo config format options
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                if (con!=null){
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

            return result ;
        }


        else
            return "";


    }

    public String findDDLInPublicScheme(String type, String name) {
       throw new NotImplementedException();
    }

    public String findDbmsJobDDL(String name) {

        String    sql = "DECLARE\n" +
                    " callstr VARCHAR2(4096);\n" +
                    "BEGIN\n" +
                    "  dbms_job.user_export(" + name + ", callstr);\n" +
                    ":done := callstr; " +
                    "END;";

        return (String) getJdbcTemplate().execute(sql, new CallableStatementCallbackImpl());
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }


    private class CallableStatementCallbackImpl implements CallableStatementCallback {
        public Object doInCallableStatement(CallableStatement callableStatement) throws SQLException, DataAccessException {
            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.executeUpdate();
            return callableStatement.getString(1);
        }
    }

    private class UserObjectRowMapper implements RowMapper {
        public UserObject mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserObject userObject = new UserObject();
            userObject.setName(rs.getString("OBJ_NAME"));
            userObject.setType(rs.getString("OBJ_TYPE"));
            userObject.setOpToken(rs.getLong("OP_TOKEN"));
            userObject.setSchema(schemaName == null ? "" : schemaName);
            return userObject;
        }
    }

    public void setNonPrimaryTypes(List<String> nonPrimaryTypes) {
        this.nonPrimaryTypes = nonPrimaryTypes;
    }
}
