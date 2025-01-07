package ru.ushkalov.courseProject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.ushkalov.courseProject.entity.Role;
import ru.ushkalov.courseProject.entity.User;
import ru.ushkalov.courseProject.repository.RoleRepository;
import ru.ushkalov.courseProject.repository.UserRepository;

import java.util.List;

@Controller
@RequestMapping("/manageRoles")
public class RoleController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public RoleController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    public String showRoleManagement(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "role-management";
    }

    @GetMapping("/editRoles")
    public String editUserRoles(@RequestParam Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User not found with id: " + userId));
        List<Role> allRoles = roleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("allRoles", allRoles);

        return "edit-roles";
    }

    @PostMapping("/saveRoles")
    public String saveUserRoles(@ModelAttribute User user) {
        User existingUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new IllegalArgumentException("User not found with id: " + user.getId()));

        existingUser.setRoles(user.getRoles());
        userRepository.save(existingUser);

        return "redirect:/manageRoles?success";
    }
}
