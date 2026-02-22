package com.java.ploud.auth.repository;

import com.java.ploud.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUserEmail(String userEmail);

    User findByUserEmail(String userEmail);
}
