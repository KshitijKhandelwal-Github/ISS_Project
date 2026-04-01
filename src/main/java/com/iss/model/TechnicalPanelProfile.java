package com.iss.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "technical_panel_profiles")
public class TechnicalPanelProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserAccount userAccount;

    @Column(nullable = false, unique = true, length = 30)
    private String panelCode;

    @Column(nullable = false, length = 100)
    private String expertiseArea;

    @Column(nullable = false)
    private Boolean active = true;

    @OneToMany(mappedBy = "panel", fetch = FetchType.LAZY)
    private List<Interview> interviews;
}
