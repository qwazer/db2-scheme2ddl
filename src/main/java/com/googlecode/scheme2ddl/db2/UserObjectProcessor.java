package com.googlecode.scheme2ddl.db2;

import com.googlecode.scheme2ddl.db2.dao.GrantsDao;
import com.googlecode.scheme2ddl.db2.dao.UserObjectDao;
import com.googlecode.scheme2ddl.db2.domain.*;
import com.googlecode.scheme2ddl.db2.domain.Db2LookInfoComparator;
import com.googlecode.scheme2ddl.exception.NonSkippableException;
import com.googlecode.scheme2ddl.exception.CannotGetDDLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author A_Reshetnikov
 * @since Date: 17.10.2012
 */
public class UserObjectProcessor implements ItemProcessor<UserObject, UserObject> {

    private static final Log log = LogFactory.getLog(UserObjectProcessor.class);
    private UserObjectDao userObjectDao;
    @Autowired
    private GrantsDao grantsDao;
    @Autowired
    private TabAuthPrinter tabAuthPrinter;
    private DDLFormatter ddlFormatter;
    private FileNameConstructor fileNameConstructor;
    private Map<String, Set<String>> excludes;
    private Map<DB2ObjectType, Set<DB2ObjectType>> dependencies;
    private boolean stopOnWarning;

    public UserObject process(UserObject userObject) throws Exception {

        if (needToExclude(userObject)) {
            log.debug(String.format("Skipping processing of user object %s ", userObject));
            return null;
        }
        userObject.setDdl(map2Ddl(userObject));
        userObject.setFileName(fileNameConstructor.map2FileName(userObject));
        return userObject;
    }

    private boolean needToExclude(UserObject userObject) {
        if (excludes == null || excludes.size() == 0) return false;
        if (excludes.get("*") != null) {
            for (String pattern : excludes.get("*")) {
                if (matchesByPattern(userObject.getName(), pattern))
                    return true;
            }
        }
        for (String typeName : excludes.keySet()) {
            if (typeName.equalsIgnoreCase(userObject.getType())) {
                if (excludes.get(typeName) == null) return true;
                for (String pattern : excludes.get(typeName)) {
                    if (matchesByPattern(userObject.getName(), pattern))
                        return true;
                }
            }
        }
        return false;
    }

    private boolean matchesByPattern(String s, String pattern) {
        pattern = pattern.replace("*", "(.*)").toLowerCase();
        return s.toLowerCase().matches(pattern);
    }

    private String map2Ddl(UserObject userObject) throws CannotGetDDLException, NonSkippableException {
        try {

            List<Db2LookInfo> list = userObjectDao.findDDLs(userObject);

            if (userObject.getType().equals("TABLE"))

            {
                if (dependencies.get(DB2ObjectType.TABLE) != null
                        && dependencies.get(DB2ObjectType.TABLE).contains(DB2ObjectType.COLUMN)) {
                    list.addAll(userObjectDao.findTableColumns(userObject));
                }
                if (dependencies.get(DB2ObjectType.TABLE) != null
                        && dependencies.get(DB2ObjectType.TABLE).contains(DB2ObjectType.INDEX)) {
                    list.addAll(userObjectDao.findTableIndexes(userObject));
                }
                if (dependencies.get(DB2ObjectType.TABLE) != null
                        && dependencies.get(DB2ObjectType.TABLE).contains(DB2ObjectType.CHECK)) {
                    list.addAll(userObjectDao.findTableChecks(userObject));
                }
                if (dependencies.get(DB2ObjectType.TABLE) != null
                        && dependencies.get(DB2ObjectType.TABLE).contains(DB2ObjectType.PRIMARY_KEY)) {
                    list.addAll(userObjectDao.findTablePkeys(userObject));
                }
                if (dependencies.get(DB2ObjectType.TABLE) != null
                        && dependencies.get(DB2ObjectType.TABLE).contains(DB2ObjectType.UNIQUE)) {
                    list.addAll(userObjectDao.findTableUniques(userObject));
                }
            }

            String result = "";
            Collections.sort(list, new Db2LookInfoComparator());
            for (Db2LookInfo db2LookInfo : list) {
                result = result + db2LookInfo.getSqlStmt() + ";\n";  //todo config format options
            }

            if (userObject.getType().equals("TABLE")){
                if (dependencies.get(DB2ObjectType.TABLE) != null
                        && dependencies.get(DB2ObjectType.TABLE).contains(DB2ObjectType.OBJECT_GRANTS)) {
                    List<TabAuth> tabAuths = grantsDao.findTableGrants(userObject);

                    if (!tabAuths.isEmpty()){
                        result += "\n" +
                                "--------------------------------------------\n" +
                                "-- Authorization Statements on Tables/Views \n" +
                                "--------------------------------------------\n" +
                                "\n";
                    }

                    for (TabAuth tabAuth : tabAuths){
                        result += tabAuthPrinter.print(tabAuth, null) + "\n";
                    }

                }
            }

            if (userObject.getType().equals("VIEW")){
                if (dependencies.get(DB2ObjectType.VIEW) != null
                        && dependencies.get(DB2ObjectType.VIEW).contains(DB2ObjectType.OBJECT_GRANTS)) {
                    List<TabAuth> tabAuths = grantsDao.findTableGrants(userObject);

                    if (!tabAuths.isEmpty()){
                        result += "\n" +
                                "--------------------------------------------\n" +
                                "-- Authorization Statements on Tables/Views \n" +
                                "--------------------------------------------\n" +
                                "\n";
                    }
                    for (TabAuth tabAuth : tabAuths){
                        result += tabAuthPrinter.print(tabAuth, null) + "\n";
                    }

                }
            }



            return ddlFormatter.formatDDL(result);
        } catch (Exception e) {
            log.warn(String.format("Cannot get DDL for object %s with error message %s", userObject, e.getMessage()));
            if (stopOnWarning) {
                throw new NonSkippableException(e);
            } else
                throw new CannotGetDDLException(e);
        }

    }

    public void setExcludes(Map excludes) {
        this.excludes = excludes;
    }

    public void setDependencies(Map dependencies) {
        this.dependencies = dependencies;
    }

    public void setUserObjectDao(UserObjectDao userObjectDao) {
        this.userObjectDao = userObjectDao;
    }

    public void setDdlFormatter(DDLFormatter ddlFormatter) {
        this.ddlFormatter = ddlFormatter;
    }

    public void setFileNameConstructor(FileNameConstructor fileNameConstructor) {
        this.fileNameConstructor = fileNameConstructor;
    }

    public void setStopOnWarning(boolean stopOnWarning) {
        this.stopOnWarning = stopOnWarning;
    }
}
