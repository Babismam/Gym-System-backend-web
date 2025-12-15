package com.example.gymmanagement.dto;

public class RegisterDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String password;
    private String role;

    // ΑΛΛΑΓΗ: Έγιναν String για να μην έχουμε θέμα με το Jackson του Wildfly
    private String dateOfBirth;
    private String membershipType;
    private String membershipDuration;
    private String customStartDate;
    private String customEndDate;

    public RegisterDTO() {}

    // Getters & Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getMembershipType() { return membershipType; }
    public void setMembershipType(String membershipType) { this.membershipType = membershipType; }

    public String getMembershipDuration() { return membershipDuration; }
    public void setMembershipDuration(String membershipDuration) { this.membershipDuration = membershipDuration; }

    public String getCustomStartDate() { return customStartDate; }
    public void setCustomStartDate(String customStartDate) { this.customStartDate = customStartDate; }

    public String getCustomEndDate() { return customEndDate; }
    public void setCustomEndDate(String customEndDate) { this.customEndDate = customEndDate; }
}