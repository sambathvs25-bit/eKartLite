package com.eflipkartlite.batchservice.config;

import com.eflipkartlite.batchservice.entity.OrderStatus;
import com.eflipkartlite.batchservice.repository.OrderArchiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Slf4j
public class BatchJobConfig {

    private final OrderArchiveRepository orderRepository;
    
    public BatchJobConfig(OrderArchiveRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Bean("orderMaintenanceJob")
    Job orderMaintenanceJob(JobRepository jobRepository,
                            @Qualifier("archiveOrdersStep") Step archiveOrdersStep,
                            @Qualifier("salesReportStep") Step salesReportStep) {
        return new JobBuilder("orderMaintenanceJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(archiveOrdersStep)
                .next(salesReportStep)
                .build();
    }

    @Bean("archiveOrdersStep")
    Step archiveOrdersStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("archiveOrdersStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDateTime cutoff = LocalDate.now().minusDays(1).atStartOfDay();
                    var readyOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.READY_FOR_SHIPPING, cutoff);
                    readyOrders.forEach(order -> order.setStatus(OrderStatus.ARCHIVED));
                    orderRepository.saveAll(readyOrders);
                    log.info("Archived {} orders", readyOrders.size());
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }

    @Bean("salesReportStep")
    Step salesReportStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("salesReportStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    LocalDate today = LocalDate.now();
                    LocalDateTime start = today.atStartOfDay();
                    LocalDateTime end = today.plusDays(1).atStartOfDay().minusNanos(1);
                    var sales = orderRepository.totalSalesBetween(start, end, List.of(OrderStatus.PAID, OrderStatus.READY_FOR_SHIPPING, OrderStatus.ARCHIVED));
                    log.info("Daily sales report for {} => totalSales={}", today, sales);
                    return RepeatStatus.FINISHED;
                }, txManager)
                .build();
    }
}
