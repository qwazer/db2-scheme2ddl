package com.googlecode.scheme2ddl.db2;

import com.googlecode.scheme2ddl.db2.dao.UserObjectDao;
import com.googlecode.scheme2ddl.db2.domain.Db2LookInfo;
import com.googlecode.scheme2ddl.db2.domain.Db2LookInfoComparator;
import com.googlecode.scheme2ddl.db2.domain.UserObject;
import com.googlecode.scheme2ddl.exception.NonSkippableException;
import com.googlecode.scheme2ddl.exception.CannotGetDDLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ItemProcessor;

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
    private DDLFormatter ddlFormatter;
    private FileNameConstructor fileNameConstructor;
    private Map<String, Set<String>> excludes;
    private Map<String, Set<String>> dependencies;
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

            List<Db2LookInfo> list = userObjectDao.findDDL(userObject);

            String result = "";
            list.sort(new Db2LookInfoComparator());
            for (Db2LookInfo db2LookInfo : list){
                result = result + db2LookInfo.getSqlStmt() + "\n;";  //todo config format options
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