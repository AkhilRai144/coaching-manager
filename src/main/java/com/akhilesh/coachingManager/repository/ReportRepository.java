package com.akhilesh.coachingManager.repository;

import com.akhilesh.coachingManager.dto.AttendancePercent;
import com.akhilesh.coachingManager.dto.PendingFeeSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<PendingFeeSummary> pendingFeesByBatch() {

        String sql = """
                SELECT b.id AS batch_id,
                       b.name AS batch_name,
                       COUNT(fd.id) AS pending_dues,
                       SUM(fd.amount - COALESCE(p.paid, 0)) AS pending_amount
                FROM fee_due fd
                JOIN enrollment e ON fd.enrollment_id = e.id
                JOIN batch b ON e.batch_id = b.id
                JOIN student s ON e.student_id = s.id
                LEFT JOIN (
                    SELECT fee_due_id, SUM(amount_paid) AS paid
                    FROM payment
                    GROUP BY fee_due_id
                ) p ON p.fee_due_id = fd.id
                WHERE fd.status = 'PENDING'
                  AND s.deleted = 0
                GROUP BY b.id, b.name
                ORDER BY pending_amount DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new PendingFeeSummary(
                rs.getLong("batch_id"),
                rs.getString("batch_name"),
                rs.getInt("pending_dues"),
                rs.getBigDecimal("pending_amount")
        ));
    }

    public List<AttendancePercent> attendancePercentByBatch(Long batchId) {

        String sql = """
                SELECT s.id AS student_id,
                       s.name AS student_name,
                       COUNT(a.id) AS total_classes,
                       SUM(
                           CASE
                               WHEN a.status = 'PRESENT' THEN 1
                               ELSE 0
                           END
                       ) AS present_count,
                       ROUND(
                           COALESCE(
                               100.0 * SUM(
                                   CASE
                                       WHEN a.status = 'PRESENT' THEN 1
                                       ELSE 0
                                   END
                               ) / NULLIF(COUNT(a.id), 0),
                               0
                           ),
                           2
                       ) AS percentage
                FROM enrollment e
                JOIN student s ON e.student_id = s.id
                LEFT JOIN attendance a ON a.enrollment_id = e.id
                WHERE e.batch_id = ?
                  AND s.deleted = 0
                GROUP BY s.id, s.name
                ORDER BY percentage ASC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new AttendancePercent(
                        rs.getLong("student_id"),
                        rs.getString("student_name"),
                        rs.getInt("total_classes"),
                        rs.getInt("present_count"),
                        rs.getBigDecimal("percentage")
                ),
                batchId
        );
    }
}
