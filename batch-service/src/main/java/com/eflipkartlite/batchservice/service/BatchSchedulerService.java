package com.eflipkartlite.batchservice.service;

import com.eflipkartlite.batchservice.entity.OrderStatus;
import com.eflipkartlite.batchservice.repository.OrderArchiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class BatchSchedulerService {

    private final JobLauncher jobLauncher;
    private final Job orderMaintenanceJob;
    
    public BatchSchedulerService(JobLauncher jobLauncher, Job orderMaintenanceJob) {
        this.jobLauncher = jobLauncher;
        this.orderMaintenanceJob = orderMaintenanceJob;
    }

    @Scheduled(cron = "0 0 1 * * *")
    public void runScheduledJob() throws Exception {
        triggerJob();
    }

    public void triggerJob() throws Exception {
        jobLauncher.run(orderMaintenanceJob, new JobParametersBuilder()
                .addLong("run.id", System.currentTimeMillis())
                .toJobParameters());
    }
}
