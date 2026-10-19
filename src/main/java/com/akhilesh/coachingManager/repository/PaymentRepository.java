package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("select sum(p.amountPaid) from Payment p where p.feeDue.id = :feeDueId")
    BigDecimal sumPaidByFeeDueId(@Param("feeDueId") Long feeDueId);

    List<Payment> findByFeeDueId(Long feeDueId);
}
