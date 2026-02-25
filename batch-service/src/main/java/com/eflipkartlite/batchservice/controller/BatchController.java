package com.eflipkartlite.batchservice.controller;

import com.eflipkartlite.batchservice.dto.MessageResponse;
import com.eflipkartlite.batchservice.service.BatchSchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent/batch")
public class BatchController {

    private final BatchSchedulerService schedulerService;
    
    public BatchController(BatchSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/run")
    public ResponseEntity<MessageResponse> run() throws Exception {
        schedulerService.triggerJob();
        return ResponseEntity.ok(new MessageResponse("Batch job triggered"));
    }
}
