package ru.ushkalov.courseProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ushkalov.courseProject.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
