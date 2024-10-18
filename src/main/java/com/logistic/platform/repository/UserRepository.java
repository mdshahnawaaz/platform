package com.logistic.platform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.logistic.platform.models.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
