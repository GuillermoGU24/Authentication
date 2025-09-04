package co.com.crediya.model.user.exception;

import co.com.crediya.model.user.User;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UserDomainValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+\\-\\s]{7,15}$");

    public static void validateForRegistration(User user) {
        validateName(user.getName(), "name");
        validateName(user.getLastName(), "last name");
        validateEmail(user.getEmail());
        validateSalary(user.getBaseSalary());
        validatePhone(user.getPhone());
        validateBirthDate(user.getBirthDate());
        validateAddress(user.getAddress());
    }

    private static void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        if (name.trim().length() < 2 || name.trim().length() > 50) {
            throw new IllegalArgumentException(fieldName + " must be between 2 and 50 characters");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private static void validateSalary(Long salary) {
        if (salary == null) {
            throw new IllegalArgumentException("Base salary is required");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("Base salary cannot be negative");
        }
        if (salary > 15000000) {
            throw new IllegalArgumentException("Base salary cannot exceed 15,000,000");
        }
    }

    private static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Invalid phone format");
        }
    }

    private static void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birthdate is required");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Birthdate cannot be in the future");
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("User must be at least 18 years old");
        }
        if (age > 100) {
            throw new IllegalArgumentException("Age cannot exceed 100 years");
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (address.trim().length() < 10 || address.trim().length() > 200) {
            throw new IllegalArgumentException("Address must be between 10 and 200 characters");
        }
    }
}
