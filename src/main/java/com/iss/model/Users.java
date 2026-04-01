package com.iss.model;

import com.iss.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {

    @Id
    private Long id;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Accounts accounts;

    @Column(nullable = false, length = 100)
    private String department;

    @Column(nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column(nullable = false)
    private Boolean active = true;
}
