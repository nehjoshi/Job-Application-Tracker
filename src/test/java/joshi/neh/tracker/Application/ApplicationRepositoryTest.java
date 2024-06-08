package joshi.neh.tracker.Application;

import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ApplicationRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Pageable pageable;
    private Application application1;
    private Application application2;

    @BeforeEach
    public void setup() {
        User user = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("johndoe")
                .targetPosition("SWE-1")
                .build();
        User savedUser = this.userRepository.save(user);
        Application application1 = Application.builder()
                .companyName("Test Company")
                .compensation("$Example.00")
                .positionTitle("Example title")
                .additionalInfo("Example additional info")
                .dateApplied(LocalDateTime.now())
                .location("Example address")
                .status(ApplicationStatus.APPLIED)
                .user(user)
                .build();
        Application application2 = Application.builder()
                .companyName("Test Company 2")
                .compensation("$Example.00 2")
                .positionTitle("Example title 2")
                .additionalInfo("Example additional info 2")
                .dateApplied(LocalDateTime.now().minusDays(2))
                .location("Example address 2")
                .status(ApplicationStatus.APPLIED)
                .user(user)
                .build();
        Pageable pageable = PageRequest.of(0, 10);
        this.user = savedUser;
        this.application1 = application1;
        this.application2 = application2;
        this.pageable = pageable;
    }

    @Test
    public void ApplicationRepository_SaveApplication_ReturnSavedApplication() {
        Application application = this.application1;

        Application savedApplication = applicationRepository.save(application);

        assertNotNull(savedApplication);
        assertNotNull(savedApplication.getApplicationId());
        assertEquals(savedApplication.getUser().getUserId(), this.user.getUserId());
    }

    @Test
    public void ApplicationRepository_GetRecentApplications_ReturnOrderedByDateApplications(){
        Application application1 = this.application1;
        Application application2 = this.application2;
        Pageable pageable = this.pageable;
        applicationRepository.save(application1);
        applicationRepository.save(application2);

        List<Application> applicationList = applicationRepository.getRecentApplications(pageable).getContent();

        assertNotNull(applicationList);
        assertEquals(applicationList.size(), 2);
        for (Application a: applicationList) {
            assertEquals(a.getStatus(), ApplicationStatus.APPLIED);
        }
        assertTrue(applicationList.get(0).getDateApplied().isAfter(applicationList.get(1).getDateApplied()));
    }

    @Test
    public void ApplicationRepository_findAllApplicationsOfUser_ReturnUserApplicationsUsingId() {
        UUID id = this.user.getUserId();
        Application application1 = this.application1;
        Application application2 = this.application2;
        Pageable pageable = this.pageable;
        applicationRepository.save(application1);
        applicationRepository.save(application2);

        List<Application> applications = applicationRepository.findAllApplicationsOfUser(id, pageable).getContent();

        assertNotNull(applications);
        assertEquals(applications.size(), 2);
        for (Application a: applications) {
            assertEquals(a.getUser().getUserId(), id);
        }
    }

    @Test
    public void ApplicationRepository_getApplicationCountOfUser_ReturnNumberOfUserApplications() {
        UUID id = this.user.getUserId();
        Application application1 = this.application1;
        Application application2 = this.application2;
        applicationRepository.save(application1);
        applicationRepository.save(application2);

        int count = applicationRepository.getApplicationCountOfUser(id);

        assertEquals(count, 2);
    }

    @Test
    public void ApplicationRepository_searchUserApplicationsByCompanyName_ReturnMatchingApplications() {
        UUID id = this.user.getUserId();
        Application application1 = this.application1;
        Application application2 = this.application2;
        applicationRepository.save(application1);
        applicationRepository.save(application2);
        String search = "tes";

        List<Application> applications = applicationRepository.searchUserApplicationsByCompanyName(id, search);

        assertFalse(applications.isEmpty());
        assertEquals(applications.size(), 2);
        for (Application a: applications) {
            assertEquals(a.getUser().getUserId(), id);
            assertTrue(a.getCompanyName().toLowerCase().startsWith(search));
        }
    }

    @Test
    public void ApplicationRepository_searchUserApplicationsByCompanyName_ReturnNoApplications() {
        UUID id = this.user.getUserId();
        Application application1 = this.application1;
        Application application2 = this.application2;
        applicationRepository.save(application1);
        applicationRepository.save(application2);
        String search = "se";

        List<Application> applications = applicationRepository.searchUserApplicationsByCompanyName(id, search);

        assertTrue(applications.isEmpty());
    }

    @Test
    public void ApplicationRepository_findById_ReturnApplicationById() {
        Application application1 = this.application1;

        Application application = applicationRepository.save(application1);
        Optional<Application> dbApplication = applicationRepository.findById(application.getApplicationId());

        assertTrue(dbApplication.isPresent());
        assertEquals(dbApplication.get().getApplicationId(), application.getApplicationId());
    }

    @Test
    public void ApplicationRepository_updateById_ReturnUpdatedApplication() {
        Application application1 = this.application1;
        Application savedApplication = applicationRepository.save(application1);
        String newCompanyName = "Updated Company name";
        String newTitle = "Updated title";
        savedApplication.setCompanyName(newCompanyName);
        savedApplication.setPositionTitle(newTitle);

        Application updatedApplication = applicationRepository.save(savedApplication);

        assertNotNull(updatedApplication);
        assertEquals(updatedApplication.getApplicationId(), savedApplication.getApplicationId());
        assertEquals(updatedApplication.getCompanyName(), newCompanyName);
        assertEquals(updatedApplication.getPositionTitle(), newTitle);
    }

    @Test
    public void ApplicationRepository_deleteById_ReturnNull() {
        Application application1 = this.application1;
        Application application2 = this.application2;
        Application savedApplication = applicationRepository.save(application1);
        applicationRepository.save(application2);
        Long id = savedApplication.getApplicationId();

        applicationRepository.deleteById(id);

        Optional<Application> deletedApplication = applicationRepository.findById(id);

        assertTrue(deletedApplication.isEmpty());
    }
}