package com.pe.laboratorio.security.repository;

import com.pe.laboratorio.security.entity.Session;
import com.pe.laboratorio.security.entity.SessionStatus;
import com.pe.laboratorio.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findBySessionToken(String sessionToken);

    List<Session> findByUserAndStatus(User user, SessionStatus status);

    long countByUserAndStatus(User user, SessionStatus status);

    List<Session> findByUserAndIpAddress(User user, String ipAddress);

    @Query("SELECT s FROM Session s WHERE s.user = :user ORDER BY s.loginTime DESC LIMIT 1")
    Optional<Session> findLastSessionByUser(@Param("user") User user);

    List<Session> findByIsSuspiciousAndLoginTimeBetween(
            boolean isSuspicious,
            LocalDateTime startTime,
            LocalDateTime endTime);

    List<Session> findByIpAddressAndStatus(String ipAddress, SessionStatus status);

    @Query("SELECT COUNT(s) > 0 FROM Session s WHERE s.user = :user " +
            "AND s.ipAddress != :currentIp " +
            "AND s.status = 'ACTIVE' " +
            "AND s.loginTime >= :sinceTime")
    boolean existsRecentSessionFromDifferentIp(
            @Param("user") User user,
            @Param("currentIp") String currentIp,
            @Param("sinceTime") LocalDateTime sinceTime);
}
