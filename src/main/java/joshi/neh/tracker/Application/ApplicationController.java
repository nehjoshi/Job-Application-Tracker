package joshi.neh.tracker.Application;

import joshi.neh.tracker.Application.dto.AllApplicationsResponseDto;
import joshi.neh.tracker.Application.dto.ApplicationDto;
import joshi.neh.tracker.Application.dto.ApplicationResponseDto;
import joshi.neh.tracker.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/application")
@Slf4j
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @PostMapping("/new")
    public ResponseEntity<ApplicationResponseDto> saveApplication(
            @RequestBody ApplicationDto dto
    ) {
        return this.applicationService.saveApplication(dto);
    }

    @GetMapping("/all/{pageNumber}")
    public ResponseEntity<AllApplicationsResponseDto> getAllApplicationsByUserId(
            @PathVariable("pageNumber") int pageNumber
    ) {
        return this.applicationService.getAllApplicationsOfUser(pageNumber);
    }

    @PutMapping("/edit/{applicationId}")
    public ResponseEntity<Application> updateApplicationById(
            @PathVariable("applicationId") Long applicationId,
            @RequestBody ApplicationDto dto
    ) {
        return this.applicationService.updateApplicationById(applicationId, dto);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<String> deleteApplicationById(
            @PathVariable("applicationId") Long applicationId
    ) {
        return this.applicationService.deleteApplicationById(applicationId);
    }


}
