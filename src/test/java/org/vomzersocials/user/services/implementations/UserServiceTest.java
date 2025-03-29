package org.vomzersocials.user.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.vomzersocials.user.data.models.User;
import org.vomzersocials.user.data.repositories.UserRepository;
import org.vomzersocials.user.dtos.requests.CreatePostRequest;
import org.vomzersocials.user.dtos.requests.LoginRequest;
import org.vomzersocials.user.dtos.requests.LogoutRequest;
import org.vomzersocials.user.dtos.requests.RegisterUserRequest;
import org.vomzersocials.user.dtos.responses.CreatePostResponse;
import org.vomzersocials.user.dtos.responses.LoginResponse;
import org.vomzersocials.user.dtos.responses.LogoutUserResponse;
import org.vomzersocials.user.dtos.responses.RegisterUserResponse;
import org.vomzersocials.user.utils.Role;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    private RegisterUserRequest registerUserRequest;
    private RegisterUserResponse registerUserResponse;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private LogoutRequest logoutRequest;
    private LogoutUserResponse logoutUserResponse;
    private CreatePostRequest createPostRequest;
    private CreatePostResponse createPostResponse;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();

        registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUserName("johni");
        registerUserRequest.setPassword("password");
        registerUserRequest.setRole(Role.ADMIN);

        loginRequest = new LoginRequest();
        loginRequest.setUsername("johni");
        loginRequest.setPassword("password");

        logoutRequest = new LogoutRequest();
        logoutRequest.setUserName("johni");

        createPostRequest = new CreatePostRequest();
    }

    @Test
    public void test_thatUserCanRegister_andCreatePost() {
        registerUserResponse = userService.registerNewUser(registerUserRequest);
        assertEquals("User registered successfully.", registerUserResponse.getMessage());

        loginResponse = userService.loginUser(loginRequest);
        assertEquals("Logged in successfully", loginResponse.getMessage());

        User user = new User();
        user.setUserName(loginResponse.getUserName());

        createPostRequest.setAuthor(user);
        createPostRequest.setTitle("Title");
        createPostRequest.setContent("Content");

        createPostResponse = userService.createPost(createPostRequest);
        assertNotNull(createPostResponse);
        assertEquals("Title", createPostResponse.getTitle());
        assertEquals("Content", createPostResponse.getContent());
        assertEquals("johni", createPostResponse.getAuthor().getUserName());
    }

    @Test
    public void test_thatUserCannotPostEmptyContent() {
        registerUserResponse = userService.registerNewUser(registerUserRequest);
        loginResponse = userService.loginUser(loginRequest);

        User testUser = userRepository.findUserByUserName(loginResponse.getUserName()).orElseThrow();

        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setAuthor(testUser);
        postRequest.setTitle(" ");
        postRequest.setContent("");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createPost(postRequest));
        assertEquals("Author, title, and content are required", exception.getMessage());
    }

    @Test
    public void test_thatPostCreationFails_WhenUserNotFound() {
        CreatePostRequest postRequest = new CreatePostRequest();
        postRequest.setAuthor(new User()); // Creating a new user without saving it
        postRequest.setTitle("Test Title");
        postRequest.setContent("Test Content");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                userService.createPost(postRequest));
        assertEquals("User not found", exception.getMessage());
    }


}
