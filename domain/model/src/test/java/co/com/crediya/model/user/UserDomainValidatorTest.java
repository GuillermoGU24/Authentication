package co.com.crediya.model.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserDomainValidatorTest {

    private User buildUser() {
        return new User(
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
    void shouldValidateUserCorrectly() {
        User user = buildUser();
        assertDoesNotThrow(user::validateForRegistration);
    }

    @Test
    void shouldThrowError_WhenEmailInvalid() {
        User user = buildUser();
        user.setEmail("correo-invalido");

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, user::validateForRegistration);

        assertEquals("Invalid email format", ex.getMessage());
    }

    @Test
    void shouldThrowError_WhenSalaryNegative() {
        User user = buildUser();
        user.setBaseSalary(-5000L);

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, user::validateForRegistration);

        assertEquals("Base salary cannot be negative", ex.getMessage());
    }

    @Test
    void shouldThrowError_WhenAgeIsUnder18() {
        User user = buildUser();
        user.setBirthDate(LocalDate.now().minusYears(16));

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, user::validateForRegistration);

        assertEquals("User must be at least 18 years old", ex.getMessage());
    }

    @Test
    void shouldThrowError_WhenAddressTooShort() {
        User user = buildUser();
        user.setAddress("Calle1");

        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class, user::validateForRegistration);

        assertEquals("Address must be between 10 and 200 characters", ex.getMessage());
    }
}
