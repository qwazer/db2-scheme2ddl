package com.googlecode.scheme2ddl.db2.dao;

import com.googlecode.scheme2ddl.db2.domain.TabAuth;
import com.googlecode.scheme2ddl.db2.domain.UserObject;

import java.util.List;

/**
 * @author ar
 * @since Date: 14.12.2014
 */
public interface GrantsDao {

    List<TabAuth> findTableGrants(UserObject userObject);
}
