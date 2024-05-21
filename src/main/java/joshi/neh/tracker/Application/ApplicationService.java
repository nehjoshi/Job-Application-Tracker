package joshi.neh.tracker.Application;

import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
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

    public ResponseEntity<?> getAllApplicationsOfUser(UUID id) {
        Pageable query50Applications = PageRequest.of(0,50, Sort.by("date_applied").reverse());
        List<Application> applications = this.applicationRepository.findAllApplicationsOfUser(id, query50Applications).getContent();
        if (applications.isEmpty()){
            return new ResponseEntity<>("No applications yet. Start applying now!", HttpStatus.OK);
        }
        return new ResponseEntity<>(applications, HttpStatus.OK);

    }

}
