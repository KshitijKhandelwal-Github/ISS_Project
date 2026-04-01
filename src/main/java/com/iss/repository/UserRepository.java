package com.iss.repository;

import com.iss.model.Accounts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Accounts, Long> {
    Optional<Accounts> findByEmail(String email);
}
