package co.com.crediya.model.user;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class UserDomainValidator {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+\\-\\s]{7,15}$");

    public static void validateForRegistration(User user) {
        validateName(user.getName(), "nombre");
        validateName(user.getLastName(), "apellido");
        validateEmail(user.getEmail());
        validateSalary(user.getBaseSalary());
        validatePhone(user.getPhone());
        validateBirthDate(user.getFechaNacimiento());
        validateAddress(user.getDireccion());
    }

    private static void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El " + fieldName + " es obligatorio");
        }
        if (name.trim().length() < 2 || name.trim().length() > 50) {
            throw new IllegalArgumentException("El " + fieldName + " debe tener entre 2 y 50 caracteres");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("El formato del correo electrónico es inválido");
        }
    }

    private static void validateSalary(Long salary) {
        if (salary == null) {
            throw new IllegalArgumentException("El salario base es obligatorio");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("El salario base no puede ser negativo");
        }
        if (salary > 15000000) {
            throw new IllegalArgumentException("El salario base no puede exceder $15,000,000");
        }
    }

    private static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("El formato del teléfono es inválido");
        }
    }

    private static void validateBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser futura");
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < 18) {
            throw new IllegalArgumentException("El usuario debe ser mayor de edad (18 años)");
        }
        if (age > 100) {
            throw new IllegalArgumentException("La edad no puede ser superior a 100 años");
        }
    }

    private static void validateAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria");
        }
        if (address.trim().length() < 10 || address.trim().length() > 200) {
            throw new IllegalArgumentException("La dirección debe tener entre 10 y 200 caracteres");
        }
    }

}

