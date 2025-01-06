package ru.ushkalov.courseProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ushkalov.courseProject.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
