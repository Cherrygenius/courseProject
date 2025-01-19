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
import ru.ushkalov.courseProject.service.StudentSecurity;
import ru.ushkalov.courseProject.service.UserService;

import java.util.Optional;

@Controller
@Slf4j
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentSecurity studentSecurity;

    /**
     * Просмотр списка студентов.
     * ADMIN видит всех студентов.
     * USER видит только своих студентов.
     * READ_ONLY видит всех студентов, но не может редактировать.
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER') or hasRole('READ_ONLY')")
    public ModelAndView getAllStudents() {
        User currentUser = userService.getCurrentUser(); // Текущий пользователь
        ModelAndView mav = new ModelAndView("list-students");

        // Если ADMIN, возвращаем всех студентов
        if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"))) {
            mav.addObject("students", studentRepository.findAll());
        }
        // Если USER, возвращаем только своих студентов
        else if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_USER"))) {
            mav.addObject("students", studentRepository.findAll().stream()
                    .filter(student -> student.getCreatedBy().equals(currentUser))
                    .toList());
        }
        // Если READ_ONLY, также возвращаем всех студентов
        else if (currentUser.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_READ_ONLY"))) {
            mav.addObject("students", studentRepository.findAll());
        }

        return mav;
    }

    /**
     * Показ формы для добавления студента.
     * Только ADMIN и USER имеют доступ.
     */
    @GetMapping("/addStudentForm")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ModelAndView addStudentForm() {
        ModelAndView mav = new ModelAndView("add-student-form");
        Student student = new Student();
        mav.addObject("student", student);
        return mav;
    }

    /**
     * Сохранение студента.
     * Только ADMIN и USER могут сохранять студентов.
     */
    @PostMapping("/saveStudent")
    public String saveStudent(@ModelAttribute Student student) {
        User currentUser = userService.getCurrentUser();

        if (student.getId() != null) {
            // Если студент уже существует, то оставляем прежнего владельца
            Optional<Student> existingStudent = studentRepository.findById(student.getId());
            existingStudent.ifPresent(value -> student.setCreatedBy(value.getCreatedBy()));
        } else {
            // Новый студент — устанавливаем текущего пользователя как создателя
            student.setCreatedBy(currentUser);
        }

        studentRepository.save(student);
        return "redirect:/list";
    }

    /**
     * Показ формы для обновления студента.
     * ADMIN может редактировать всех.
     * USER может редактировать только своих.
     * READ_ONLY не имеет доступа.
     */
    @GetMapping("/showUpdateForm")
    @PreAuthorize("hasRole('ADMIN') or @studentSecurity.isOwner(#studentId)")
    public ModelAndView showUpdateForm(@RequestParam Long studentId) {
        ModelAndView mav = new ModelAndView("add-student-form");
        Optional<Student> optionalStudent = studentRepository.findById(studentId);

        if (optionalStudent.isPresent()) {
            mav.addObject("student", optionalStudent.get());
        } else {
            mav.addObject("student", new Student());
        }
        return mav;
    }

    /**
     * Удаление студента.
     * ADMIN может удалять всех.
     * USER может удалять только своих.
     * READ_ONLY не имеет доступа.
     */
    @GetMapping("/deleteStudent")
    @PreAuthorize("hasRole('ADMIN') or @studentSecurity.isOwner(#studentId)")
    public String deleteStudent(@RequestParam Long studentId) {
        studentRepository.deleteById(studentId);
        return "redirect:/list";
    }
}
