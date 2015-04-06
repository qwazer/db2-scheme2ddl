package com.googlecode.scheme2ddl.db2.dao;

import com.googlecode.scheme2ddl.db2.domain.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author ar
 * @since Date: 14.12.2014
 */
@Component(value = "grantsDao")
@StepScope
public class GrantsDaoImpl extends JdbcDaoSupport implements GrantsDao {


    @Autowired
    public GrantsDaoImpl(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public List<TabAuth> findTableGrants(UserObject userObject) {


        return getJdbcTemplate().query("select * from SYSCAT.TABAUTH where TABSCHEMA = ? and GRANTOR != 'SYSIBM' and TABNAME = ?",
                new Object[]{userObject.getSchema(), userObject.getName()},
                new RowMapper<TabAuth>() {
                    public TabAuth mapRow(ResultSet rs, int rowNum) throws SQLException {
                        TabAuth tabAuth = new TabAuth();
                        tabAuth.setGrantee(rs.getString("GRANTEE"));
                        tabAuth.setGranteeType(GranteeType.mapByCode(rs.getString("GRANTEETYPE")));
                        tabAuth.setTabSchema(rs.getString("TABSCHEMA"));
                        tabAuth.setTabName(rs.getString("TABNAME"));
                        tabAuth.setAlterAuth(GrantType.valueOf(rs.getString("ALTERAUTH")));
                        tabAuth.setControlAuth(GrantType.valueOf(rs.getString("CONTROLAUTH")));
                        tabAuth.setDeleteAuth(GrantType.valueOf(rs.getString("DELETEAUTH")));
                        tabAuth.setIndexAuth(GrantType.valueOf(rs.getString("INDEXAUTH")));
                        tabAuth.setInsertAuth(GrantType.valueOf(rs.getString("INSERTAUTH")));
                        tabAuth.setRefAuth(GrantType.valueOf(rs.getString("REFAUTH")));
                        tabAuth.setSelectAuth(GrantType.valueOf(rs.getString("SELECTAUTH")));
                        tabAuth.setUpdateAuth(GrantType.valueOf(rs.getString("UPDATEAUTH")));

                        if ("PUBLIC  ".equals(tabAuth.getGrantee())) {
                            tabAuth.setGranteeType(GranteeType.PUBLIC);
                        }
                        return tabAuth;
                    }
                });
    }

    public List<RoutineAuth> findRoutineGrants(UserObject userObject) {
        return getJdbcTemplate().query("select * from SYSCAT.ROUTINEAUTH where SCHEMA = ? and GRANTOR != 'SYSIBM' and SPECIFICNAME = ?",
                new Object[]{userObject.getSchema(), userObject.getName()},
                new RowMapper<RoutineAuth>() {
                    public RoutineAuth mapRow(ResultSet rs, int rowNum) throws SQLException {
                        RoutineAuth routineAuth = new RoutineAuth();
                        routineAuth.setGrantee(rs.getString("GRANTEE"));
                        routineAuth.setGranteeType(GranteeType.mapByCode(rs.getString("GRANTEETYPE")));
                        routineAuth.setSchema(rs.getString("SCHEMA"));
                        routineAuth.setSpecificname(rs.getString("SPECIFICNAME"));
                        routineAuth.setRoutineType(RoutineAuth.RoutineType.mapByCode(rs.getString("ROUTINETYPE")));
                        routineAuth.setExecuteAuth(GrantType.valueOf(rs.getString("EXECUTEAUTH")));

                        if  (GranteeType.GROUP.equals(routineAuth.getGranteeType())
                                && "PUBLIC  ".equals(routineAuth.getGrantee())){
                            routineAuth.setGranteeType(GranteeType.PUBLIC);
                        }

                        return routineAuth;
                    }
                });
    }
}
