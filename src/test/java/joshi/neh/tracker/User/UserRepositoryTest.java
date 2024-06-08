package joshi.neh.tracker.User;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    public void setup() {
        User user1 = User.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("johndoe")
                .targetPosition("SWE-1")
                .build();
        User user2 = User.builder()
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("janesmith")
                .targetPosition("SDE-1")
                .build();
        this.user1 = user1;
        this.user2 = user2;
    }

    @Test
    public void UserRepository_SaveAll_ReturnSavedUser() {
        //Arrange
        User user = this.user1;
        //Act
        User savedUser = userRepository.save(user);

        //Assert
        assertNotNull(savedUser);
        assertNotNull(savedUser.getUserId());
    }

    @Test
    public void UserRepository_FindAll_ReturnAllUsers() {
        //Arrange
        User user1 = this.user1;
        User user2 = this.user2;

        //Act
        userRepository.save(user1);
        userRepository.save(user2);
        List<User> users = userRepository.findAll();

        //Assert
        assertNotNull(users);
        assertEquals(users.size(), 2);
    }

    @Test
    public void UserRepository_FindById_ReturnUserByIdWhenFound() {
        User user = this.user1;
        User savedUser = userRepository.save(user);
        UUID id = savedUser.getUserId();

        User dbUser = userRepository.findById(id).get();

        assertNotNull(dbUser);
        assertEquals(dbUser.getEmail(), savedUser.getEmail());
        assertEquals(dbUser.getPassword(), savedUser.getPassword());
        assertEquals(dbUser.getFirstName(), savedUser.getFirstName());
        assertEquals(dbUser.getLastName(), savedUser.getLastName());
        assertEquals(dbUser.getLastName(), savedUser.getLastName());
        assertEquals(dbUser.getTargetPosition(), savedUser.getTargetPosition());
    }

    @Test
    public void UserRepository_FindById_ReturnEmptyWhenNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        Optional<User> dbUserOptional = userRepository.findById(nonExistentId);

        assertTrue(dbUserOptional.isEmpty());
    }

    @Test
    public void UserRepository_FindByEmail_ReturnUserWhenFound() {
        User user = this.user1;
        User savedUser = userRepository.save(user);

        User dbUser = userRepository.findByEmail(user.getEmail()).get();

        assertNotNull(dbUser);
        assertEquals(dbUser.getUserId(), savedUser.getUserId());
    }

    @Test
    public void UserRepository_FindByEmail_ReturnEmptyWhenNotFound() {
        Optional<User> dbUser = userRepository.findByEmail("abcdef@gmail.com");

        assertTrue(dbUser.isEmpty());
    }

}