package joshi.neh.tracker.Application;

import joshi.neh.tracker.exceptions.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    @Query(value = "SELECT * FROM application a WHERE a.user_id=:userId", nativeQuery = true)
    public Page<Application> findAllApplicationsOfUser(
            @Param("userId") UUID userId,
            Pageable pageable
    );
}
