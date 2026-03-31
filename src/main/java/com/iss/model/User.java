package com.iss.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(name = "role")
    private String role; // HR, PANEL

    @OneToMany(mappedBy = "panel", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Interview> panelInterviews;

    @OneToMany(mappedBy = "hr", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Interview> hrInterviews;
}
