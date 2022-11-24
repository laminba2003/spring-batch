package com.spring.training.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobNotificationListener extends JobExecutionListenerSupport {

    public void beforeJob(JobExecution execution) {
        switch (execution.getStatus()) {
            case STARTED:
                log.info("JOB STARTED!");
                break;
        }
    }

    @Override
    public void afterJob(JobExecution execution) {
        switch (execution.getStatus()) {
            case COMPLETED:
                log.info("JOB FINISHED!");
                break;
        }
    }
}
