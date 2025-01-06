package ru.ushkalov.courseProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.ushkalov.courseProject.dto.UserDto;
import ru.ushkalov.courseProject.entity.Role;
import ru.ushkalov.courseProject.entity.User;
import ru.ushkalov.courseProject.repository.RoleRepository;
import ru.ushkalov.courseProject.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository, RoleRepository roleRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }



    @Override
    public void saveUser(UserDto userDto) {
        // Создание пользователя
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Проверка и добавление ролей
        checkRolesExist(); // Теперь роли добавляются автоматически

        // Получаем роль пользователя
        Role role = roleRepository.findByName("ROLE_USER");

        // Присваиваем пользователю роль, если она существует
        if (role != null) {
            user.setRoles(Arrays.asList(role));
            userRepository.save(user);
        } else {
            // Обработка ошибки или создание роли
            // Например, можно создать роль или выбросить исключение
            throw new RuntimeException("Role 'ROLE_USER' not found");
        }
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map((user) -> mapToUserDto(user))
                .collect(Collectors.toList());
    }

    private UserDto mapToUserDto(User user){
        UserDto userDto = new UserDto();
        String[] str = user.getName().split(" ");
        userDto.setFirstName(str[0]);
        userDto.setLastName(str[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    private void checkRolesExist() {
        // Список ролей, которые должны быть созданы
        List<String> roleNames = Arrays.asList("ROLE_ADMIN", "ROLE_USER", "ROLE_READ_ONLY");

        // Проходим по списку ролей и добавляем их, если их нет в базе данных
        for (String roleName : roleNames) {
            createRoleIfNotExists(roleName);
        }
    }
    private void createRoleIfNotExists(String roleName) {
        // Проверяем, существует ли роль
        if (roleRepository.findByName(roleName) == null) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
