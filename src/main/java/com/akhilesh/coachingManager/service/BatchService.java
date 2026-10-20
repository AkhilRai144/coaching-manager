package com.akhilesh.coachingManager.service;

import com.akhilesh.coachingManager.audit.Audited;
import com.akhilesh.coachingManager.dto.BatchRequest;
import com.akhilesh.coachingManager.dto.BatchResponse;
import com.akhilesh.coachingManager.entity.Batch;
import com.akhilesh.coachingManager.entity.Teacher;
import com.akhilesh.coachingManager.exception.DuplicateResourceException;
import com.akhilesh.coachingManager.exception.InvalidRequestException;
import com.akhilesh.coachingManager.exception.ResourceNotFoundException;
import com.akhilesh.coachingManager.repository.BatchRepository;
import com.akhilesh.coachingManager.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchRepository batchRepository;
    private final TeacherRepository teacherRepository;

    @Transactional
    @Audited("BATCH_CREATED")
    public BatchResponse addBatch(BatchRequest request){
        if(!request.getEndTime().isAfter(request.getStartTime())){
            throw  new InvalidRequestException("End time must be after start time");
        }

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found with id "
                +request.getTeacherId()));
        if (teacher.isDeleted()){
            throw new ResourceNotFoundException("Teacher not found with id "+request.getTeacherId());
        }
        boolean clash = batchRepository
                .existsByTeacherIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        teacher.getId(),request.getEndTime(),request.getStartTime()
                );
        if (clash){
            throw new DuplicateResourceException("Teacher already has a batch at this time");
        }
        Batch batch = new Batch();
        batch.setName(request.getName());
        batch.setStartTime(request.getStartTime());
        batch.setEndTime(request.getEndTime());
        batch.setMonthlyFee(request.getMonthlyFee());
        batch.setTeacher(teacher);

        Batch savedBatch = batchRepository.save(batch);
        return toResponse(savedBatch);
    }

    @Transactional(readOnly = true)
    public List<BatchResponse> getAllBatches(){
        return batchRepository.findAllBy().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BatchResponse getBatchById(Long id){
        Batch batch = batchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch not found with id "+id));
        return  toResponse(batch);
    }







    private BatchResponse toResponse(Batch batch){
        return new BatchResponse(batch.getId()
                ,batch.getName()
                ,batch.getStartTime()
                ,batch.getEndTime()
                ,batch.getMonthlyFee()
                ,batch.getTeacher().getId()
                ,batch.getTeacher().getName());
    }

}
