package joshi.neh.tracker.Application;

import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
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
    public ResponseEntity<?> getAllApplicationsOfUser(UUID id) {
        Pageable query50Applications = PageRequest.of(0,50, Sort.by("date_applied").reverse());
        logger.info("Request received in application service!!!");
        List<Application> applications = this.applicationRepository.findAllApplicationsOfUser(id, query50Applications).getContent();
        logger.info("FOUND ALL APPLICATIONS");
        if (applications.isEmpty()){
            return new ResponseEntity<>("No applications yet. Start applying now!", HttpStatus.OK);
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);

    }

}
