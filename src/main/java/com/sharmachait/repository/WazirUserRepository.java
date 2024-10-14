package com.sharmachait.repository;

import com.sharmachait.model.entity.WazirUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WazirUserRepository extends JpaRepository<WazirUser, Long> {
    WazirUser findByEmail(String email);
}
