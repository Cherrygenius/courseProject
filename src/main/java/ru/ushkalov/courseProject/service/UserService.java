package ru.ushkalov.courseProject.service;

import ru.ushkalov.courseProject.dto.UserDto;
import ru.ushkalov.courseProject.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);
    User findUserByEmail(String email);
    List<UserDto> findAllUsers();
}
