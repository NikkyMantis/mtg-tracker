package com.nikkyev00.mtg_tracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public GET endpoints
                .requestMatchers(HttpMethod.GET,
                    "/",                  // home
                    "/login",             
                    "/css/**",            
                    "/js/**",             
                    "/images/**",
                    "/cards",             // search list
                    "/cards/search/**",   // any search URLs
                    "/cards/*"            // card detail (one segment under /cards)
                ).permitAll()

                // Collection and mutation require auth
                .requestMatchers(HttpMethod.GET, "/cards/collection").authenticated()
                .requestMatchers(HttpMethod.POST, "/cards/add", "/cards/remove").authenticated()

                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                // After login, go back to home so they'll see the dropdown + Collection button
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                // After logout, send them back to home (with Sign In button only)
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
            .username("user")
            .password(encoder.encode("password"))
            .roles("USER")
            .build();

        UserDetails admin = User.builder()
            .username("nikkyev00")
            .password(encoder.encode("M0tionless"))
            .roles("ADMIN")
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}
