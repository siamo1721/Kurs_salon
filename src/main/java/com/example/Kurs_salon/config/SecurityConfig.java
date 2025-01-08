//package com.example.Kurs_salon.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import com.example.Kurs_salon.model.UserAuthority;
//
//@Slf4j
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                );
//        http.authorizeHttpRequests(auth -> auth
//                        // Публичные ресурсы - разрешаем доступ всем
//                        .requestMatchers(
//                                "/",
//                                "/index.html",
//                                "/services.html",
//                                "/masters.html",
//                                "/reviews.html",
//                                "/css/**",
//                                "/js/**",
//                                "/images/**",
//                                "/login.html",
//                                "/register.html"
//                        ).permitAll()
//
//                        // Публичные API - разрешаем доступ всем
//                        .requestMatchers(
//                                "/api/auth/**",
//                                "/api/services/**",
//                                "/api/masters/**",
//                                "/api/reviews/**",
//                                "/api/appointments/my/**"
//                        ).permitAll()
//
//                        // Защищенные страницы
//                        .requestMatchers(
//                                "/profile.html",
//                                "/appointments.html"
//                        ).authenticated()
//
//                        // Защищенные API
//                        .requestMatchers("/api/appointments/**").hasAnyAuthority(
//                                UserAuthority.CLIENT.getAuthority(),
//                                UserAuthority.ADMIN.getAuthority()
//                        )
//                        .requestMatchers(HttpMethod.POST, "/api/reviews/**").hasAuthority(
//                                UserAuthority.CLIENT.getAuthority()
//                        )
//                        .anyRequest().permitAll()
//                )
//
//                .logout(logout -> logout
//                        .logoutUrl("/api/auth/logout")
//                        .logoutSuccessUrl("/")
//                        .invalidateHttpSession(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                )
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
package com.example.Kurs_salon.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
        @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Публичные ресурсы
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/login.html",
                                "/register.html",
                                "/api/auth/**",
                                "/appointments.html"
                        ).permitAll()

                        // Админ панель
                        .requestMatchers("/admin/**", "/api/admin/**").hasAuthority("ADMIN")

                        // Системный админ
                        .requestMatchers("/system-admin/**", "/api/system-admin/**").hasAnyAuthority("SYSTEM_ADMIN","ADMIN")

                        // Мастер
                        .requestMatchers("/master/**", "/api/master/**").hasAnyAuthority("MASTER", "SYSTEM_ADMIN","ADMIN")

                        // Клиент
                        .requestMatchers("/profile/**").hasAnyAuthority("CLIENT","MASTER", "SYSTEM_ADMIN","ADMIN")

                        // Общие разделы для авторизованных пользователей
                        .requestMatchers("/services.html", "/masters.html","/reviews.html","/api/masters").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/masters/**", "api/services/**" ,"/api/reviews").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/appointments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/appointments/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/**").hasAnyAuthority("CLIENT","MASTER", "SYSTEM_ADMIN","ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasAnyAuthority("CLIENT","MASTER", "SYSTEM_ADMIN","ADMIN")

                        .anyRequest().authenticated()
                )

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}