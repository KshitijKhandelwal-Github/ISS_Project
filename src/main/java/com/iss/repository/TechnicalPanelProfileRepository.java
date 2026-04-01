package com.iss.repository;

import com.iss.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TechnicalPanelProfileRepository extends JpaRepository<Users, Long> {
}
