package com.sms;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

// @Entity = one row per student. Indexes keep programme filtering and sorting fast with 10,000+ rows.
@Entity
@Table(indexes = {
        @Index(name = "idx_student_course", columnList = "course"),
        @Index(name = "idx_student_name", columnList = "name")
})
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Registration number = YY (admission year) + BB (programme code) + SSSS (serial), e.g. 23010042
    // unique = true makes the DATABASE guarantee that no two students share a number
    @NotBlank(message = "Registration number is required.")
    @Pattern(regexp = "\\d{8}", message = "Registration number must be exactly 8 digits (YY + programme code + serial).")
    @Column(unique = true, nullable = false)
    private String regNo;

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    private String email;

    @NotBlank(message = "Programme is required.")
    private String course;

    @Min(value = 1, message = "Semester must be between 1 and 8.")
    @Max(value = 8, message = "Semester must be between 1 and 8.")
    private int sem;

    @DecimalMin(value = "0.0", message = "CGPA must be between 0 and 10.")
    @DecimalMax(value = "10.0", message = "CGPA must be between 0 and 10.")
    private double cgpa;

    @Min(value = 0, message = "Attendance must be between 0 and 100.")
    @Max(value = 100, message = "Attendance must be between 0 and 100.")
    private int attendance;

    public Student() { }

    public Student(String regNo, String name, String email, String course, int sem, double cgpa, int attendance) {
        this.regNo = regNo;
        this.name = name;
        this.email = email;
        this.course = course;
        this.sem = sem;
        this.cgpa = cgpa;
        this.attendance = attendance;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRegNo() { return regNo; }
    public void setRegNo(String regNo) { this.regNo = regNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public int getSem() { return sem; }
    public void setSem(int sem) { this.sem = sem; }
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public int getAttendance() { return attendance; }
    public void setAttendance(int attendance) { this.attendance = attendance; }
}
