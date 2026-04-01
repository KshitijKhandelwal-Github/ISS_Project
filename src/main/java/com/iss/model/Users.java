package com.iss.model;

import com.iss.model.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;

@Data
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

    public void setRole(RoleType role) {
        if (this.role == RoleType.ROLE_CANDIDATE) {
            throw new IllegalArgumentException("This type of user cannot have ROLE_CANDIDATE role");
        }
        this.role = role;
    }
}
