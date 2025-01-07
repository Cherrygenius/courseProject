package ru.ushkalov.courseProject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.ushkalov.courseProject.entity.Role;
import ru.ushkalov.courseProject.entity.User;
import ru.ushkalov.courseProject.repository.RoleRepository;
import ru.ushkalov.courseProject.repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/manageRoles")
@Slf4j
public class RoleController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public RoleController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Страница управления ролями
    @GetMapping
    public ModelAndView showRoleManagement() {
        log.info("/manageRoles -> showRoleManagement");
        ModelAndView mav = new ModelAndView("role-management");
        List<User> users = userRepository.findAll();
        mav.addObject("users", users);
        return mav;
    }

    // Страница редактирования ролей пользователя
    @GetMapping("/editRoles")
    public ModelAndView editUserRoles(@RequestParam Long userId) {
        log.info("/editRoles -> editUserRoles for userId: {}", userId);
        ModelAndView mav = new ModelAndView("edit-roles");

        // Получаем пользователя по id
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found with id: {}", userId);
            return new IllegalArgumentException("User not found with id: " + userId);
        });

        log.info("User found: {}", user.getEmail());

        // Получаем все роли
        List<Role> allRoles = roleRepository.findAll();

        mav.addObject("user", user); // Передаем пользователя в модель
        mav.addObject("allRoles", allRoles); // Передаем все роли в модель

        return mav;
    }

    // Сохранение ролей пользователя
    @PostMapping("/saveRoles")
    public String saveUserRoles(@ModelAttribute User user) {
        log.info("/saveRoles -> saveUserRoles for userId: {}", user.getId());

        // Получаем существующего пользователя
        User existingUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new IllegalArgumentException("User not found with id: " + user.getId()));

        // Обновляем роли
        existingUser.setRoles(user.getRoles());
        userRepository.save(existingUser);

        log.info("Roles for user {} saved successfully", user.getEmail());
        return "redirect:/manageRoles?success";
    }
}
