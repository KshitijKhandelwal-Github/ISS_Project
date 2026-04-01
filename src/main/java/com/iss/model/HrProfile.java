package com.iss.model;
import com.iss.model.enums.RoleType;
import com.iss.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "hr_profiles")
public class HrProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @Column(nullable = false, unique = true, length = 30)
    private String employeeCode;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "hr", fetch = FetchType.LAZY)
    private List<Interview> interviews;
}

