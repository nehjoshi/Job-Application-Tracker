package joshi.neh.tracker.Application;

import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/application")
@Slf4j
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);

    @PostMapping("/new/{userId}")
    public ResponseEntity<ApplicationResponseDto> saveApplication(
            @PathVariable("userId") UUID userId,
            @RequestBody ApplicationDto dto
    ) {
        return this.applicationService.saveApplication(userId, dto);
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<?> getAllApplicationsByUserId(
            @PathVariable("userId") UUID userId
    ) {
        //Check if this ID exists, if not it'll raise an exception
        logger.info("Request received in GET Controller");
        User user = this.userService.findById(userId);
        return this.applicationService.getAllApplicationsOfUser(userId);
    }


}
