package ru.ushkalov.courseProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ushkalov.courseProject.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
