package joshi.neh.tracker.User;

import joshi.neh.tracker.Application.ApplicationDto;
import joshi.neh.tracker.User.dto.UserDto;
import joshi.neh.tracker.User.dto.UserResponseDto;
import joshi.neh.tracker.config.JwtService;
import joshi.neh.tracker.exceptions.EmailAlreadyExistsException;
import joshi.neh.tracker.exceptions.IncorrectCredentialsException;
import joshi.neh.tracker.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    public User convertToUserEntity(UserDto dto) {
        return User.builder()
                .email(dto.email())
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.MEMBER)
                .targetPosition(dto.targetPosition())
                .build();
    }

    public User findById(UUID id) {
        return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with given ID not found"));
    }

    public ResponseEntity<UserResponseDto> saveUser(UserDto userDto) {
        Optional<User> userWithSameEmailExists = userRepository.findByEmail(userDto.email());
        if (userWithSameEmailExists.isPresent()) throw new EmailAlreadyExistsException("User with email: " + userDto.email() + " already exists");
        User newUser = convertToUserEntity(userDto);
        userRepository.save(newUser);
        UserResponseDto responseDto = new UserResponseDto(
                newUser.getUserId(),
                newUser.getEmail(),
                jwtService.generateToken(newUser)
        );
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    public ResponseEntity<UserResponseDto> login(UserDto userDto) throws IncorrectCredentialsException {
        //Authenticate user first
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userDto.email(),
                            userDto.password()
                    )
            );
        } catch (AuthenticationException e) {
            throw new IncorrectCredentialsException("Incorrect email or password");
        }
        //Get user details for generating a token
        User user = this.userRepository.findByEmail(userDto.email()).get();
        //We don't have to check if user is present, coz the method above will already check that
        String accessToken = jwtService.generateToken(user);
        UserResponseDto responseDto = new UserResponseDto(
                user.getUserId(),
                user.getEmail(),
                accessToken
        );
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

}
