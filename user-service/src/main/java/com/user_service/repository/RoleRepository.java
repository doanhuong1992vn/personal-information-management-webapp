package com.user_service.repository;

import com.user_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(Role.RoleName roleName);
}
