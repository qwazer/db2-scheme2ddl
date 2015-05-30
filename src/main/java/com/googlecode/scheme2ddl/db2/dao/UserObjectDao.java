package com.googlecode.scheme2ddl.db2.dao;

import com.googlecode.scheme2ddl.db2.domain.Db2LookInfo;
import com.googlecode.scheme2ddl.db2.domain.UserObject;

import java.util.List;

/**
 * @author A_Reshetnikov
 * @since Date: 17.10.2012
 */
public interface UserObjectDao {

    List<UserObject> findListForProccessing();

    List<Db2LookInfo> findDDLs(UserObject userObject);

    List<Db2LookInfo> findTableColumns(UserObject userObject);

    List<Db2LookInfo> findTableIndexes(UserObject userObject);

    List<Db2LookInfo> findTableChecks(UserObject userObject);

    List<Db2LookInfo> findTableFkeys(UserObject userObject);

    List<Db2LookInfo> findTablePkeys(UserObject userObject);

    List<Db2LookInfo>  findTableUniques(UserObject userObject);

    void callCleanTable(Long opToken);


}
