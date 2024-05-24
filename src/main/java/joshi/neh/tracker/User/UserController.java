package joshi.neh.tracker.User;

import joshi.neh.tracker.User.dto.UserDto;
import joshi.neh.tracker.User.dto.UserResponseDto;
import joshi.neh.tracker.exceptions.IncorrectCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
