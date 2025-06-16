package com.marche.marche.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.marche.marche.modele.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
