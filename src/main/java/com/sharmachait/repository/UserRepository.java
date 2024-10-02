package com.sharmachait.repository;

import com.sharmachait.model.WazirUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface UserRepository extends JpaRepository<WazirUser, Long> {
}
