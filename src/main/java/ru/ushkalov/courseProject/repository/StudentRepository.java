package ru.ushkalov.courseProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ushkalov.courseProject.entity.Student;
import ru.ushkalov.courseProject.entity.User;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByCreatedBy(User user);
}
