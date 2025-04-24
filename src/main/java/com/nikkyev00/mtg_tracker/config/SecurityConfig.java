package com.nikkyev00.mtg_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * Security configuration for the MTG Tracker application.
 *
 * - Publicly exposes login, static assets, and card search endpoints.
 * - Secures all other endpoints behind authentication.
 * - Configures form-based login and logout handling.
 * - Defines two in-memory users: a regular user and an admin.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain, including public and protected endpoints,
     * form login, and logout behavior.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    HttpMethod.GET,
                    "/login",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/cards/search/**"
                ).permitAll()    // public endpoints
                .anyRequest().authenticated()  // all others require auth
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/cards/collection", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/cards/search/view")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    /**
     * PasswordEncoder bean using BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Registers two in-memory users:
     *  - Regular user: username="user", password="password"
     *  - Admin user:    username="admin", password="adminpassword"
     *
     * You can customize the admin credentials here.
     */
    @Bean
    public UserDetailsManager userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
            .username("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("nikkyev00")
            .password(encoder.encode("M0tionless"))  // replace with your own
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
