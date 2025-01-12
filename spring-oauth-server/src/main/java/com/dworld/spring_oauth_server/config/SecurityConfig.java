package com.dworld.spring_oauth_server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class SecurityConfig {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    UserDetailsService inMemoryUserDetailsManager() {
        log.info("Entered SecurityConfig");
        // Below works
        // return new InMemoryUserDetailsManager(
        //     User.withDefaultPasswordEncoder()
        //         .username("dwarika")
        //         .password("password")
        //         .roles("USER")
        //         .build()
        // );

        // Below also works
        UserBuilder userBuilder = User.builder();
        UserDetails dwarika = userBuilder
            .username("dwarika")
            .password("{noop}password")
            .roles("USER", "ADMIN")
            .build();
        return new InMemoryUserDetailsManager(dwarika);
    }
}
