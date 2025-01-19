package ru.ushkalov.courseProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Общедоступные страницы
                        .requestMatchers("/register/**", "/index").permitAll()

                        // Студенты: доступ для аутентифицированных пользователей
                        .requestMatchers("/addStudentForm", "/saveStudent").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/list").hasAnyRole("ADMIN", "USER", "READ_ONLY")
                        .requestMatchers("/deleteStudent", "/showUpdateForm").hasAnyRole("ADMIN", "USER")

                        // Управление пользователями: только для ADMIN
                        .requestMatchers("/users", "/manageRoles/**", "/editRoles", "/saveRoles").hasRole("ADMIN"))

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/list", true)
                        .permitAll())

                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());

        return http.build();
    }
}
