package joshi.neh.tracker.Application;

import joshi.neh.tracker.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
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

}
