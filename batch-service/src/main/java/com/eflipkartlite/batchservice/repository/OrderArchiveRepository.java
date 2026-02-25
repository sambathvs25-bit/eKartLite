package com.eflipkartlite.batchservice.repository;

import com.eflipkartlite.batchservice.entity.OrderArchive;
import com.eflipkartlite.batchservice.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderArchiveRepository extends JpaRepository<OrderArchive, Long> {
    List<OrderArchive> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime before);

    @Query("select coalesce(sum(o.totalAmount),0) from OrderArchive o where o.createdAt between :start and :end and o.status in :statuses")
    BigDecimal totalSalesBetween(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 @Param("statuses") List<OrderStatus> statuses);
}
