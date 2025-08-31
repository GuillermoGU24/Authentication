package co.com.crediya.usecase.registeruser;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        validUser = new User(
                1,
                "Juan",
                "Pérez",
                "juan.perez@example.com",
                "123456789",
                "3001234567",
                2,
                2000000L,
                LocalDate.of(1995, 5, 15),
                "Calle 123 #45-67"
        );
    }

    @Test
    void save_ShouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.existsByEmail(validUser.getEmail()))
                .thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        // Act & Assert
        StepVerifier.create(registerUserUseCase.save(validUser))
                .expectNextMatches(user ->
                        user.getName().equals("Juan") &&
                                user.getLastName().equals("Pérez") &&
                                user.getEmail().equals("juan.perez@example.com")
                )
                .verifyComplete();

        // Verify interactions
        verify(userRepository).existsByEmail(validUser.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void save_ShouldFail_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(validUser.getEmail()))
                .thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(registerUserUseCase.save(validUser))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().contains("Email is already registered")
                )
                .verify();

        // Verify interactions
        verify(userRepository).existsByEmail(validUser.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void save_ShouldFail_WhenInvalidUserData() {
        // Arrange
        validUser.setBaseSalary(-5000L);

        // Act
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> registerUserUseCase.save(validUser));

        // Assert
        assertEquals("Base salary cannot be negative", ex.getMessage());

        // Verify no repository calls
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, never()).save(any());
    }
}
