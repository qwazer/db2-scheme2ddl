package com.googlecode.scheme2ddl.db2.listener;

import com.googlecode.scheme2ddl.db2.dao.UserObjectDao;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ar
 * @since Date: 30.05.2015
 */
@Component
public class AfterStepListener implements StepExecutionListener {

    @Autowired
    private UserObjectDao userObjectDao;


    public void beforeStep(StepExecution stepExecution) {
        //todo implement beforeStep in AfterStepListener
    }

    public ExitStatus afterStep(StepExecution stepExecution) {
        Long opToken = stepExecution.getExecutionContext().getLong("opToken");
        if (opToken != null) {
            userObjectDao.callCleanTable(opToken);
        }
        return stepExecution.getExitStatus();
    }
}
