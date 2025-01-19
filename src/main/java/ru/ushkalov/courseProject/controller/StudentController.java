package ru.ushkalov.courseProject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import ru.ushkalov.courseProject.entity.Student;
import ru.ushkalov.courseProject.entity.User;
import ru.ushkalov.courseProject.repository.StudentRepository;
import ru.ushkalov.courseProject.service.UserServiceImpl;

import java.util.Optional;

@Controller
@Slf4j
public class StudentController {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserServiceImpl userService;

    @GetMapping("/list")
    public ModelAndView getAllStudents() {
        User currentUser = userService.getCurrentUser(); // Получаем текущего пользователя
        log.info("User {} accessed the student list", currentUser.getEmail());
        ModelAndView mav = new ModelAndView("list-students");
        mav.addObject("students", studentRepository.findByCreatedBy(currentUser));
        return mav;
    }

    @GetMapping("/addStudentForm")
    public ModelAndView addStudentForm() {
        log.info("User {} is accessing the add student form", userService.getCurrentUser().getEmail());
        ModelAndView mav = new ModelAndView("add-student-form");
        mav.addObject("student", new Student());
        return mav;
    }

    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute Student student) {
        User currentUser = userService.getCurrentUser();
        student.setCreatedBy(currentUser);
        studentRepository.save(student);
        log.info("User {} saved a new student: {}", currentUser.getEmail(), student.getId());
        return "redirect:/list";
    }

    @GetMapping("/showUpdateForm")
    @PreAuthorize("hasRole('ADMIN') or @studentSecurity.isOwner(#studentId)")
    public ModelAndView showUpdateForm(@RequestParam Long studentId) {
        log.info("User {} is accessing the update form for student ID {}", userService.getCurrentUser().getEmail(), studentId);
        ModelAndView mav = new ModelAndView("add-student-form");
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        mav.addObject("student", optionalStudent.orElse(new Student()));
        return mav;
    }

    @GetMapping("/deleteStudent")
    @PreAuthorize("hasRole('ADMIN') or @studentSecurity.isOwner(#studentId)")
    public String deleteStudent(@RequestParam Long studentId) {
        User currentUser = userService.getCurrentUser();
        studentRepository.deleteById(studentId);
        log.info("User {} deleted student ID {}", currentUser.getEmail(), studentId);
        return "redirect:/list";
    }
}
