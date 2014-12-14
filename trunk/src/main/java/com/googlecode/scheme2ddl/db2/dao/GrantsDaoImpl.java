package com.googlecode.scheme2ddl.db2.dao;

import com.googlecode.scheme2ddl.db2.domain.TabAuth;
import com.googlecode.scheme2ddl.db2.domain.UserObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ar
 * @since Date: 14.12.2014
 */
@Component(value = "grantsDao")
@Scope(value = "step")
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
                        tabAuth.setGranteeType(TabAuth.GranteeType.mapByCode(rs.getString("GRANTEETYPE")));
                        tabAuth.setTabSchema(rs.getString("TABSCHEMA"));
                        tabAuth.setTabName(rs.getString("TABNAME"));
                        tabAuth.setAlterAuth(TabAuth.GrantType.valueOf(rs.getString("ALTERAUTH")));
                        tabAuth.setControlAuth(TabAuth.GrantType.valueOf(rs.getString("CONTROLAUTH")));
                        tabAuth.setDeleteAuth(TabAuth.GrantType.valueOf(rs.getString("DELETEAUTH")));
                        tabAuth.setIndexAuth(TabAuth.GrantType.valueOf(rs.getString("INDEXAUTH")));
                        tabAuth.setInsertAuth(TabAuth.GrantType.valueOf(rs.getString("INSERTAUTH")));
                        tabAuth.setRefAuth(TabAuth.GrantType.valueOf(rs.getString("REFAUTH")));
                        tabAuth.setSelectAuth(TabAuth.GrantType.valueOf(rs.getString("SELECTAUTH")));
                        tabAuth.setUpdateAuth(TabAuth.GrantType.valueOf(rs.getString("UPDATEAUTH")));

                        if ("PUBLIC  ".equals(tabAuth.getGrantee())) {
                            tabAuth.setGranteeType(TabAuth.GranteeType.PUBLIC);
                        }
                        return tabAuth;
                    }
                });
    }
}
