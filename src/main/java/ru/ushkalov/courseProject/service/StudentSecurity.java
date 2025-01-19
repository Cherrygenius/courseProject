package ru.ushkalov.courseProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.ushkalov.courseProject.entity.User;
import ru.ushkalov.courseProject.repository.StudentRepository;

@Component
public class StudentSecurity {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserServiceImpl userService;

    public boolean isOwner(Long studentId) {
        User currentUser = userService.getCurrentUser();
        return studentRepository.findById(studentId)
                .map(student -> student.getCreatedBy().equals(currentUser))
                .orElse(false);
    }
}
