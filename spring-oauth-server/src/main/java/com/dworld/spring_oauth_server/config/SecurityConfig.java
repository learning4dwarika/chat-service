package com.dworld.spring_oauth_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    @Bean
    UserDetailsService inMemoryUserDetailsManager() {
        UserBuilder userBuilder = User.builder();
        UserDetails dwarika = userBuilder
            .username("dwarika")
            .password("{noop}password")
            .roles("USER", "ADMIN")
            .build();
        return new InMemoryUserDetailsManager(dwarika);
    }
}
