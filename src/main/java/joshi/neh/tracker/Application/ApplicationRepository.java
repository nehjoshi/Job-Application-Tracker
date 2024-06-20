package joshi.neh.tracker.Application;

import joshi.neh.tracker.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query(value = "SELECT * FROM application a WHERE a.user_id=:userId", nativeQuery = true)
    public Page<Application> findAllApplicationsOfUser(
            @Param("userId") UUID userId,
            Pageable pageable
    );
    @Query(value = "SELECT COUNT(*) FROM application a WHERE a.user_id = :userId", nativeQuery = true)
    public int getApplicationCountOfUser(@Param("userId") UUID userId);

    @Query(value = "SELECT * " +
            "FROM application a " +
            "WHERE a.status='APPLIED' " +
            "ORDER BY a.date_applied DESC",
    nativeQuery = true)
    public Page<Application> getRecentApplications(
            Pageable pageable
    );

    @Query(value = "SELECT * " +
            "FROM application a " +
            "WHERE a.user_id = :userId " +
            "AND a.company_name ILIKE CONCAT(:companyName, '%') " +
            "ORDER BY a.company_name",
    nativeQuery = true)
    public List<Application> searchUserApplicationsByCompanyName(
            @Param("userId") UUID userId,
            @Param("companyName") String companyName
    );

    @Query(value = "SELECT COUNT(*) " +
            "FROM application a " +
            "WHERE a.user_id = :userId " +
            "AND a.status = 'APPLIED'",
    nativeQuery = true)
    public int getApplicationCountWhereStatusApplied(
            @Param("userId") UUID userId
    );

    @Query(value = "SELECT COUNT(*) " +
            "FROM application a " +
            "WHERE a.user_id = :userId " +
            "AND a.status = 'REJECTED'",
            nativeQuery = true)
    public int getApplicationCountWhereStatusRejected(
            @Param("userId") UUID userId
    );

    @Query(value = "SELECT COUNT(*) " +
            "FROM application a " +
            "WHERE a.user_id = :userId " +
            "AND a.status = 'OFFER'",
          nativeQuery = true)
    public int getApplicationCountWhereStatusOffer(
            @Param("userId") UUID userId
    );

    @Query(value = "SELECT location, COUNT(*) AS location_count " +
            "FROM application a " +
            "WHERE a.user_id = :userId " +
            "GROUP BY location " +
            "ORDER BY location_count DESC " +
            "LIMIT 5",
            nativeQuery = true)
    public List<Object[]> getTopLocations(
            @Param("userId") UUID userId
    );

    @Query(value = "SELECT DATE(date_applied) as date, COUNT(*) as count " +
            "FROM application a " +
            "WHERE a.user_id = :userId " +
            "AND date_applied >= :date " +
            "GROUP BY DATE(date_applied) " +
            "ORDER BY DATE(date_applied)",
    nativeQuery = true)
    public List<Object[]> getCountApplications5Days(
            @Param("userId") UUID userId,
            @Param("date") LocalDate date
    );
}
