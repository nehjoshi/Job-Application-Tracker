package joshi.neh.tracker.Application;

import joshi.neh.tracker.User.User;
import joshi.neh.tracker.User.UserService;
import joshi.neh.tracker.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserService userService;

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
        User user = this.userService.findById(userId);
        return this.applicationService.getAllApplicationsOfUser(userId);
    }


}
