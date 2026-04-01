package com.iss.repository;

import com.iss.model.HrProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HrProfileRepository extends JpaRepository<HrProfile, Long> {
}
