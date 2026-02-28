package com.finance.tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.finance.tracker.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRolename(String rolename);
}