package joshi.neh.tracker.Application;

import joshi.neh.tracker.Application.dto.AllApplicationsResponseDto;
import joshi.neh.tracker.Application.dto.ApplicationDto;
import joshi.neh.tracker.Application.dto.ApplicationSocialResponseDto;
import joshi.neh.tracker.Application.dto.ApplicationStatisticsResponseDto;
import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
import joshi.neh.tracker.exceptions.ApplicationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
                .dateApplied(String.valueOf(LocalDateTime.now()))
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
        this.user = savedUser;
        this.application1 = application1;
        this.application1Dto = application1Dto;
        this.application2Dto = application2Dto;
        this.application2 = application2;
        this.pageable = pageable;
    }

    private void securitySetup() {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                null
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
        when(userDetails.getUsername())
                .thenReturn("john.doe@example.com");
    }

    @Test
    public void ApplicationService_SaveApplication_ReturnsNewApplication() {
        this.securitySetup();
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
        this.securitySetup();
        Pageable pageable = this.pageable;
        User savedUser = this.user;
        List<Application> applicationList = List.of(this.application1, this.application2);
        Page<Application> applicationPage = new PageImpl<>(applicationList, pageable, applicationList.size());
        when(userService.findByEmail("john.doe@example.com"))
                .thenReturn(savedUser);
        when(applicationRepository.findAllApplicationsOfUser(any(UUID.class), any(Pageable.class)))
                .thenReturn(applicationPage);
        when(applicationRepository.getApplicationCountOfUser(any(UUID.class)))
                .thenReturn(2);

        AllApplicationsResponseDto response = applicationService.getAllApplicationsOfUser(0, 10);

        assertNotNull(response.applications());
        assertEquals(response.count(), 2);
        List<Application> responseApps = response.applications();
        for (Application a: responseApps) {
            assertEquals(a.getUser().getUserId(), savedUser.getUserId());
        }
    }

    @Test
    public void ApplicationService_UpdateApplicationById_ReturnsUpdatedApplicationIfFound() {
        Long id = 1L;
        Application savedApplication = this.application1;
        when(applicationRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(savedApplication));
        ApplicationDto updateDto = ApplicationDto.builder()
                .companyName("Updated")
                .location("Updated")
                .additionalInfo("Updated")
                .compensation("Updated")
                .positionTitle("Updated")
                .dateApplied(String.valueOf(LocalDateTime.now().minusDays(5)))
                .status(ApplicationStatus.REJECTED)
                .build();
        savedApplication.setPositionTitle("Updated");
        savedApplication.setCompanyName("Updated");
        savedApplication.setLocation("Updated");
        savedApplication.setCompensation("Updated");
        savedApplication.setAdditionalInfo("Updated");
        savedApplication.setStatus(ApplicationStatus.REJECTED);
        savedApplication.setDateApplied(LocalDateTime.now().minusDays(5));
        when(applicationRepository.save(any(Application.class)))
                .thenReturn(savedApplication);

        ResponseEntity<Application> response = applicationService.updateApplicationById(id, updateDto);

        assertNotNull(response.getBody());
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        assertEquals(response.getBody().getApplicationId(), id);
        assertEquals(response.getBody().getCompanyName(), updateDto.companyName());
        assertEquals(response.getBody().getAdditionalInfo(), updateDto.additionalInfo());
        assertEquals(response.getBody().getStatus(), updateDto.status());
        assertEquals(response.getBody().getLocation(), updateDto.location());
        assertEquals(response.getBody().getCompensation(), updateDto.compensation());
        assertEquals(response.getBody().getPositionTitle(), updateDto.positionTitle());
        assertEquals(
                response.getBody().getDateApplied(),
                LocalDateTime.parse(updateDto.dateApplied(), DateTimeFormatter.ISO_DATE_TIME)
        );
    }

    @Test
    public void ApplicationService_UpdateApplicationById_ReturnsExceptionIfNotFound() {
        Long id = 1L;
        when(applicationRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());

        Exception exception = assertThrows(ApplicationNotFoundException.class,
                () -> applicationService.updateApplicationById(id, this.application1Dto));
        assertEquals("Application with ID " + id + " not found", exception.getMessage());
    }

    @Test
    public void ApplicationService_DeleteApplicationById_ReturnsNoContentIfFound() {
        Application savedApplication = this.application1;
        when(applicationRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(savedApplication));
        doNothing().when(applicationRepository).deleteById(1L);

        ResponseEntity<String> response = applicationService.deleteApplicationById(1L);

        assertEquals(response.getStatusCode(), HttpStatus.NO_CONTENT);
        assertEquals(response.getBody(), "Delete successful");
        Mockito.verify(applicationRepository, Mockito.times(1)).deleteById(1L);
        Mockito.verify(applicationRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    public void ApplicationService_DeleteApplicationById_ReturnsExceptionIfNotFound() {
        when(applicationRepository.findById(any(Long.class)))
                .thenReturn(Optional.empty());
        Exception exception = assertThrows(
                ApplicationNotFoundException.class,
                () -> applicationService.deleteApplicationById(1L)
        );
        assertEquals(exception.getMessage(), "Application with given ID not found");
    }

    @Test
    public void ApplicationService_SearchApplicationsByCompanyName_ReturnsListOfMatchingApplicationsIfFound() {
        this.securitySetup();
        User savedUser = this.user;
        String nameToSearch = "test";
        List<Application> applicationList = List.of(this.application1, this.application2);
        when(userService.findByEmail("john.doe@example.com"))
                .thenReturn(savedUser);
        when(applicationRepository.searchUserApplicationsByCompanyName(any(UUID.class), any(String.class)))
                .thenReturn(applicationList);

        ResponseEntity<List<Application>> response = applicationService.searchApplicationsOfUserByCompanyName(nameToSearch);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        for (Application a: response.getBody()) {
            assertTrue(a.getCompanyName().toLowerCase().startsWith(nameToSearch));
        }
    }

    @Test
    public void ApplicationService_SearchApplicationsByCompanyName_ReturnsEmptyListIfNoneFound() {
        this.securitySetup();
        User savedUser = this.user;
        String nameToSearch = "#8djm";
        String email = "john.doe@example.com";
        when(userService.findByEmail(email))
                .thenReturn(savedUser);
        when(applicationRepository.searchUserApplicationsByCompanyName(any(UUID.class), any(String.class)))
                .thenReturn(new ArrayList<>());

        ResponseEntity<List<Application>> response = applicationService.searchApplicationsOfUserByCompanyName(nameToSearch);

        assertTrue(response.getBody().isEmpty());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void ApplicationService_GetMostRecentApplications_ReturnsListOfApplications() {
        Pageable pageable = this.pageable;
        List<Application> applicationList = List.of(this.application1, this.application2);
        Page<Application> applicationPage = new PageImpl<>(applicationList, pageable, applicationList.size());
        when(applicationRepository.getRecentApplications(any(Pageable.class)))
                .thenReturn(applicationPage);

        ResponseEntity<List<ApplicationSocialResponseDto>> response = applicationService.getMostRecentApplications(0);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        for (ApplicationSocialResponseDto a: response.getBody()) {
            assertNotNull(a.firstName());
            assertNotNull(a.lastName());
            assertNotNull(a.application());
        }
    }

    @Test
    public void ApplicationService_GetAppStatistics_ReturnsAppStatisticsDto() {
        this.securitySetup();
        UUID id = this.user.getUserId();
        List<Object[]> topLocation = new ArrayList<>(Arrays.asList(
                new Object[]{"New York", 1},
                new Object[]{"Remote", 1}
        ));
        when(userService.findByEmail(any(String.class)))
                .thenReturn(this.user);
        when(applicationRepository.getApplicationCountOfUser(any(UUID.class)))
                .thenReturn(2);
        when(applicationRepository.getApplicationCountWhereStatusOffer(any(UUID.class)))
                .thenReturn(0);
        when(applicationRepository.getApplicationCountWhereStatusRejected(any(UUID.class)))
                .thenReturn(0);
        when(applicationRepository.getApplicationCountWhereStatusApplied(any(UUID.class)))
                .thenReturn(2);
        when(applicationRepository.getTopLocations(any(UUID.class)))
                .thenReturn(topLocation);

        ResponseEntity<ApplicationStatisticsResponseDto> response = applicationService.getApplicationStatistics();
        List<Map<String, Integer>> topLocationsResponse = response.getBody().topLocations();

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        for (Map<String, Integer> l: topLocationsResponse) {
            if (l.containsKey("New York")) {
                assertEquals(l.get("New York"), 1);
            }
            if (l.containsKey("Remote")) {
                assertEquals(l.get("Remote"), 1);
            }
        }

    }


}