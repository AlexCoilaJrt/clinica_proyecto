package com.pe.laboratorio.reports.audit.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pe.laboratorio.reports.audit.entity.AuditLog;

@Repository
public interface AuditRepository extends JpaRepository<AuditLog, Long> {

        @Query(value = "SELECT * FROM audit_logs a WHERE " +
                        "(COALESCE(:username, '') = '' OR a.username LIKE '%' || :username || '%') AND " +
                        "(COALESCE(:action, '') = '' OR a.action = :action) AND " +
                        "(CAST(:startDate AS TEXT) IS NULL OR a.timestamp >= :startDate) AND " +
                        "(CAST(:endDate AS TEXT) IS NULL OR a.timestamp <= :endDate) " +
                        "ORDER BY a.timestamp DESC", nativeQuery = true)
        List<AuditLog> searchLogs(
                        @Param("username") String username,
                        @Param("action") String action,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        List<AuditLog> findByUsernameOrderByTimestampDesc(String username);

        List<AuditLog> findAllByOrderByTimestampDesc();
}
