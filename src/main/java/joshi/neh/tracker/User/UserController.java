package joshi.neh.tracker.User;

import joshi.neh.tracker.User.dto.UserDto;
import joshi.neh.tracker.User.dto.UserResponseDto;
import joshi.neh.tracker.exceptions.IncorrectCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String test() {
        return "Hello World!";
    }

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponseDto> saveUser(@RequestBody UserDto userDto) {
        return this.userService.saveUser(userDto);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserDto userDto) throws IncorrectCredentialsException {
        return this.userService.login(userDto);
    }
    @PostMapping("/profile-picture")
    public ResponseEntity<String> uploadProfilePicture (
            @RequestParam("file") MultipartFile file
    ) {
        return userService.uploadProfilePicture(file);
    }

    //A route to just check if user is authenticated
    @GetMapping("/auth")
    public ResponseEntity<String> checkAuthStatus() {
        return new ResponseEntity<>("You are authenticated", HttpStatus.OK);
    }

}
