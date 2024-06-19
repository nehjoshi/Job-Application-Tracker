package joshi.neh.tracker.Application;

import joshi.neh.tracker.Application.dto.*;
import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
import joshi.neh.tracker.exceptions.ApplicationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public Application convertToEntity(User user, ApplicationDto dto) {
        String dateString = dto.dateApplied();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateApplied = LocalDateTime.parse(dateString, formatter);
        return Application.builder()
                .companyName(dto.companyName())
                .compensation(dto.compensation())
                .status(dto.status())
                .positionTitle(dto.positionTitle())
                .location(dto.location())
                .additionalInfo(dto.additionalInfo())
                .dateApplied(dateApplied)
                .user(user)
                .build();
    }

    public ApplicationResponseDto convertToResponse(Application application) {
        return new ApplicationResponseDto(
                application.getCompanyName(),
                application.getPositionTitle()
        );
    }


    @Transactional
//    @CacheEvict(value = "applications", allEntries = true)
    public ResponseEntity<Application> saveApplication(ApplicationDto dto) {
        //Get user details
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        User user = userService.findByEmail(email);
        //Create new application
        Application newApp = convertToEntity(user, dto);
        Application savedApp = this.applicationRepository.save(newApp);
        return new ResponseEntity<>(savedApp, HttpStatus.CREATED);
    }

//    @Cacheable("applications")
    @Transactional
    public AllApplicationsResponseDto getAllApplicationsOfUser(int pageNumber) {
        //Create pageable
        Pageable query10Applications = PageRequest.of(pageNumber, 10, Sort.by("date_applied").descending().and(Sort.by("company_name").ascending()));

        //Get user details
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        UUID userId = userService.findByEmail(email).getUserId();

        //Get applications of the user
        List<Application> applications = this.applicationRepository.findAllApplicationsOfUser(userId, query10Applications).getContent();

        //Get total application count
        int count = this.applicationRepository.getApplicationCountOfUser(userId);
        //Return
        AllApplicationsResponseDto responseDto = new AllApplicationsResponseDto(
                count,
                applications
        );
        return responseDto;

    }
    //Remove existing cache
    @Transactional
//    @CacheEvict(value = "applications", allEntries = true)
    public ResponseEntity<Application> updateApplicationById(Long applicationId, ApplicationDto dto) {
        Optional<Application> application = this.applicationRepository.findById(applicationId);

        if (application.isEmpty()) throw new ApplicationNotFoundException("Application with ID " + applicationId + " not found");

        Application app = application.get();
        app.setStatus(dto.status() != null ? dto.status() : app.getStatus());
        app.setCompanyName(dto.companyName() != null ? dto.companyName() : app.getCompanyName());
        app.setLocation(dto.location() != null ? dto.location() : app.getLocation());
        app.setCompensation(dto.compensation() != null ? dto.compensation() : app.getCompensation());
        app.setAdditionalInfo(dto.additionalInfo() != null ? dto.additionalInfo() : app.getAdditionalInfo());
        String dateString = dto.dateApplied();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime dateApplied = LocalDateTime.parse(dateString, formatter);
        app.setDateApplied(dateApplied);
        this.applicationRepository.save(app);
        logger.info("UPDATED APPLICATION: ");
        logger.info(String.valueOf(app));
        return new ResponseEntity<>(app, HttpStatus.CREATED);
    }

//    @CacheEvict(value = "applications", allEntries = true)
    public ResponseEntity<String> deleteApplicationById(Long applicationId) {
        Optional<Application> app = this.applicationRepository.findById(applicationId);
        if (app.isEmpty()) throw new ApplicationNotFoundException("Application with given ID not found");
        Application application = app.get();
        this.applicationRepository.deleteById(applicationId);
        return new ResponseEntity<>("Delete successful", HttpStatus.NO_CONTENT);
    }


    public ResponseEntity<List<Application>> searchApplicationsOfUserByCompanyName(String companyName) {
        //Get user details
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        UUID userId = userService.findByEmail(email).getUserId();
        List<Application> apps = applicationRepository.searchUserApplicationsByCompanyName(userId, companyName);
        return new ResponseEntity<>(apps, HttpStatus.OK);
    }

    public ResponseEntity<List<ApplicationSocialResponseDto>> getMostRecentApplications(int pageNumber) {
        Pageable query10Applications = PageRequest.of(pageNumber, 10);
        List<Application> applications = this.applicationRepository.getRecentApplications(query10Applications).getContent();
        List<ApplicationSocialResponseDto> responseDtos = new ArrayList<>();
        for (Application application: applications) {
            String firstName = application.getUser().getFirstName();
            String lastName = application.getUser().getLastName();
            responseDtos.add(new ApplicationSocialResponseDto(firstName, lastName, application));
        }
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

//    public ResponseEntity
    public ResponseEntity<ApplicationStatisticsResponseDto> getApplicationStatistics() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        UUID userId = userService.findByEmail(email).getUserId();

        int count = applicationRepository.getApplicationCountOfUser(userId);
        int offerCount = applicationRepository.getApplicationCountWhereStatusOffer(userId);
        int appliedCount = applicationRepository.getApplicationCountWhereStatusApplied(userId);
        int rejectedCount = applicationRepository.getApplicationCountWhereStatusRejected(userId);
        int stageCount = count - (appliedCount + rejectedCount + offerCount);
        List<Object[]> topLocationsObject = applicationRepository.getTopLocations(userId);
        List<Map<String, Integer>> topLocations = new ArrayList<>();
        for (Object[] result : topLocationsObject) {
            String location = (String) result[0];
            int locationCount = ((Number) result[1]).intValue();
            Map<String, Integer> entry = new HashMap<>();
            entry.put(location, locationCount);
            topLocations.add(entry);
        }
        ApplicationStatisticsResponseDto responseDto = ApplicationStatisticsResponseDto.builder()
                .appliedCount(appliedCount)
                .totalCount(count)
                .topLocations(topLocations)
                .offerCount(offerCount)
                .rejectedCount(rejectedCount)
                .stageCount(stageCount)
                .build();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
