package com.gymops.dto.member;

import com.gymops.entity.Gender;
import com.gymops.entity.Member;
import com.gymops.entity.MemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Member details required to create a gym member.")
public class MemberCreateRequest {

    @Schema(description = "Member's first name.", example = "Asha", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Schema(description = "Member's last name.", example = "Patel")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Schema(description = "Unique contact email address.", example = "asha@example.com")
    @Email(message = "Email must be a valid email address")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;

    @Schema(description = "Unique contact phone number.", example = "+15551234567",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Phone is required")
    @Size(max = 32, message = "Phone must not exceed 32 characters")
    private String phone;

    @Schema(description = "Member's date of birth.", example = "1995-04-12")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Schema(description = "Date the member joined.", example = "2026-10-09",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Join date is required")
    @PastOrPresent(message = "Join date cannot be in the future")
    private LocalDate joinDate;

    private MemberStatus status = MemberStatus.ACTIVE;

    public MemberCreateRequest() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public MemberStatus getStatus() {
        return status;
    }

    public void setStatus(MemberStatus status) {
        this.status = status;
    }

    public Member toEntity() {
        Member member = new Member();
        member.setFirstName(this.firstName);
        member.setLastName(this.lastName);
        member.setEmail(this.email);
        member.setPhone(this.phone);
        member.setDateOfBirth(this.dateOfBirth);
        member.setGender(this.gender);
        member.setJoinDate(this.joinDate);
        member.setStatus(this.status != null ? this.status : MemberStatus.ACTIVE);
        return member;
    }
}
