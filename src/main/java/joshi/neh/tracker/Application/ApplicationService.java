package joshi.neh.tracker.Application;

import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
import joshi.neh.tracker.exceptions.ApplicationNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    public Application convertToEntity(User user, ApplicationDto dto) {
        return Application.builder()
                .companyName(dto.companyName())
                .compensation(dto.compensation())
                .status(dto.status())
                .positionTitle(dto.positionTitle())
                .location(dto.location())
                .additionalInfo(dto.additionalInfo())
                .dateApplied(LocalDate.now())
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
    public ResponseEntity<ApplicationResponseDto> saveApplication(UUID id, ApplicationDto dto) {
        User user = this.userService.findById(id);
        Application newApp = convertToEntity(user, dto);
        this.applicationRepository.save(newApp);
        return new ResponseEntity<>(convertToResponse(newApp), HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<?> getAllApplicationsOfUser() {
        //Create pageable
        Pageable query50Applications = PageRequest.of(0,50, Sort.by("date_applied").reverse());

        //Get user details
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        UUID userId = userService.findByEmail(email).getUserId();

        //Get applications of the user
        List<Application> applications = this.applicationRepository.findAllApplicationsOfUser(userId, query50Applications).getContent();

        //Return
        if (applications.isEmpty()){
            return new ResponseEntity<>("No applications yet. Start applying now!", HttpStatus.OK);
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);

    }

    @Transactional
    public ResponseEntity<Application> updateApplicationById(Long applicationId, ApplicationDto dto) {
        Optional<Application> application = this.applicationRepository.findById(applicationId);

        if (application.isEmpty()) throw new ApplicationNotFoundException("Application with ID " + applicationId + " not found");

        Application app = application.get();
        app.setStatus(dto.status() != null ? dto.status() : app.getStatus());
        app.setCompanyName(dto.companyName() != null ? dto.companyName() : app.getCompanyName());
        app.setLocation(dto.location() != null ? dto.location() : app.getLocation());
        app.setCompensation(dto.compensation() != null ? dto.compensation() : app.getCompensation());
        app.setAdditionalInfo(dto.additionalInfo() != null ? dto.additionalInfo() : app.getAdditionalInfo());
        this.applicationRepository.save(app);
        logger.info("UPDATED APPLICATION: ");
        logger.info(String.valueOf(app));
        return new ResponseEntity<>(app, HttpStatus.CREATED);
    }
}
