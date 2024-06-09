package joshi.neh.tracker.User;
import joshi.neh.tracker.User.dto.UserDto;
import joshi.neh.tracker.User.dto.UserResponseDto;
import joshi.neh.tracker.config.JwtService;
import joshi.neh.tracker.exceptions.EmailAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Autowired
    private User user;

    @Autowired
    private UserDto userDto;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        User user1 = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("johndoe")
                .userId(UUID.randomUUID())
                .targetPosition("SWE-1")
                .build();
        this.user = user1;

        UserDto dto = UserDto.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("johndoe")
                .targetPosition("SWE-1")
                .build();
        this.userDto = dto;
    }

    @Test
    public void UserService_SaveUser_ReturnsResponseDtoUserIfNoExistingUser () {
        UserDto userDto = this.userDto;
        User newUser = this.user;
        when(userRepository.findByEmail(userDto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        ResponseEntity<UserResponseDto> response = userService.saveUser(userDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        System.out.println(response.getBody());
        assertEquals(response.getBody().email(), userDto.email());
        assertNotNull(response.getBody().userId());
        assertEquals(response.getBody().accessToken(), "jwtToken");
    }

    @Test
    public void UserService_SaveUser_ReturnsExceptionIfUserExists () {
        UserDto userDto = this.userDto;
        User newUser = this.user;
        when(userRepository.findByEmail(userDto.email()))
                .thenReturn(Optional.of(newUser));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.saveUser(userDto)
        );

        assertEquals("User with email: john.doe@example.com already exists", exception.getMessage());
    }

}