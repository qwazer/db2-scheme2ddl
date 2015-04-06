package com.googlecode.scheme2ddl.db2;

import com.googlecode.scheme2ddl.db2.dao.UserObjectDao;
import com.googlecode.scheme2ddl.db2.domain.UserObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author A_Reshetnikov
 * @since Date: 17.10.2012
 */
@Component (value = "reader")
@StepScope
public class UserObjectReader implements ItemReader<UserObject> {

    private static final Log log = LogFactory.getLog(UserObjectReader.class);
    private List<UserObject> list;

    @Autowired
    private UserObjectDao userObjectDao;


    @Value("#{jobParameters['schemaName']}")
    private String schemaName;


    public synchronized UserObject read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (list == null) {
            fillList();
            log.info(String.format("Found %s items for processing in schema %s", list.size(), schemaName));
        }
        if (list.size() == 0) {
            return null;
        } else
            return list.remove(0);
    }

    private synchronized void fillList() {
        log.info(String.format("Start getting of user object list in schema %s for processing", schemaName));
        list = userObjectDao.findListForProccessing();

    }

    public void setUserObjectDao(UserObjectDao userObjectDao) {
        this.userObjectDao = userObjectDao;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
