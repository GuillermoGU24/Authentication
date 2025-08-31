package co.com.crediya.model.user;


import co.com.crediya.model.user.exception.UserDomainValidator;

import java.time.LocalDate;

public class User {

    private int idUser;
    private String name;
    private String lastName;
    private String email;
    private String document;
    private String phone;
    private int idRol;
    private Long baseSalary;
    private LocalDate birthDate;
    private String addres;

    public User() {
    }

    public User(int idUser, String name, String lastName, String email, String document,
                String phone, int idRol, Long baseSalary, LocalDate birthDate, String addres) {
        this.idUser = idUser;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.document = document;
        this.phone = phone;
        this.idRol = idRol;
        this.baseSalary = baseSalary;
        this.birthDate = birthDate;
        this.addres = addres;
    }

    public void validateForRegistration() {
        UserDomainValidator.validateForRegistration(this);
    }

    // Getters y setters existentes...
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public Long getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(Long baseSalary) {
        this.baseSalary = baseSalary;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return addres;
    }

    public void setAddress(String addres) {
        this.addres = addres;
    }
}