package joshi.neh.tracker.Application;

import joshi.neh.tracker.Application.dto.AllApplicationsResponseDto;
import joshi.neh.tracker.Application.dto.ApplicationDto;
import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
class ApplicationServiceTest {

    @InjectMocks
    private ApplicationService applicationService;

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private Application application1;
    private ApplicationDto application1Dto;
    private Application application2;
    private ApplicationDto application2Dto;
    private User user;
    private Pageable pageable;
    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        User savedUser = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("johndoe")
                .targetPosition("SWE-1")
                .userId(UUID.randomUUID())
                .build();
        Application application1 = Application.builder()
                .companyName("Test Company")
                .compensation("$Example.00")
                .positionTitle("Example title")
                .additionalInfo("Example additional info")
                .dateApplied(LocalDateTime.now())
                .location("Example address")
                .status(ApplicationStatus.APPLIED)
                .user(savedUser)
                .applicationId((long) 1L)
                .build();
        ApplicationDto application1Dto = ApplicationDto.builder()
                .companyName("Test Company")
                .compensation("$Example.00")
                .positionTitle("Example title")
                .additionalInfo("Example additional info")
                .location("Example address")
                .status(ApplicationStatus.APPLIED)
                .build();
        Application application2 = Application.builder()
                .companyName("Test Company 2")
                .compensation("$Example.00 2")
                .positionTitle("Example title 2")
                .additionalInfo("Example additional info 2")
                .dateApplied(LocalDateTime.now().minusDays(2))
                .location("Example address 2")
                .status(ApplicationStatus.APPLIED)
                .user(savedUser)
                .applicationId((long) 2L)
                .build();
        ApplicationDto application2Dto = ApplicationDto.builder()
                .companyName("Test Company 2")
                .compensation("$Example.00 2")
                .positionTitle("Example title 2")
                .additionalInfo("Example additional info 2")
                .location("Example address 2")
                .status(ApplicationStatus.APPLIED)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                null
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        when(userDetails.getUsername())
                .thenReturn("john.doe@example.com");
        this.user = savedUser;
        this.application1 = application1;
        this.application1Dto = application1Dto;
        this.application2Dto = application2Dto;
        this.application2 = application2;
        this.pageable = pageable;
    }

    @Test
    public void ApplicationService_SaveApplication_ReturnsNewApplication() {
        ApplicationDto dto = this.application1Dto;
        Application savedApp = this.application1;
        User savedUser = this.user;
        when(userService.findByEmail("john.doe@example.com"))
                .thenReturn(savedUser);
        when(applicationRepository.save(any(Application.class)))
                .thenReturn(savedApp);

        ResponseEntity<Application> response = applicationService.saveApplication(dto);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertNotNull(response.getBody().getApplicationId());
        assertEquals(response.getBody().getUser().getUserId(), savedUser.getUserId());
    }

    @Test
    public void ApplicationService_GetAllApplicationsOfUser_ReturnsListOfApplications() {
        Pageable pageable = this.pageable;
        User savedUser = this.user;
        List<Application> applicationList = List.of(this.application1, this.application2);
        Page<Application> applicationPage = new PageImpl<>(applicationList, pageable, applicationList.size());
        when(userService.findByEmail(any(String.class)))
                .thenReturn(savedUser);
        when(applicationRepository.findAllApplicationsOfUser(any(UUID.class), any(Pageable.class)))
                .thenReturn(applicationPage);
        when(applicationRepository.getApplicationCountOfUser(any(UUID.class)))
                .thenReturn(2);

        AllApplicationsResponseDto response = applicationService.getAllApplicationsOfUser(0);

        assertNotNull(response.applications());
        assertEquals(response.count(), 2);
        List<Application> responseApps = response.applications();
        for (Application a: responseApps) {
            assertEquals(a.getUser().getUserId(), savedUser.getUserId());
        }
    }

}