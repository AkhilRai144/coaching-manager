package com.akhilesh.coachingManager.controller;

import com.akhilesh.coachingManager.dto.BatchRequest;
import com.akhilesh.coachingManager.dto.BatchResponse;
import com.akhilesh.coachingManager.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {
    private final BatchService batchService;

    @PostMapping
    public ResponseEntity<BatchResponse> addBatch(@Valid @RequestBody BatchRequest request){
        BatchResponse response = batchService.addBatch(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BatchResponse>> getAllBatch(){
        List<BatchResponse> list = batchService.getAllBatches();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BatchResponse> getBatchById(@PathVariable Long id){
        BatchResponse batchResponse = batchService.getBatchById(id);
        return new ResponseEntity<>(batchResponse,HttpStatus.OK);
    }
}

