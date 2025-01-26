package com.dworld.spring_oauth_server.config;

import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

import com.dworld.spring_oauth_server.repository.ClientRepository;

@Configuration
public class SecurityConfig {

    Logger log = LoggerFactory.getLogger(this.getClass());

    // @Bean
    // UserDetailsService inMemoryUserDetailsManager() {
    //     log.info("Entered SecurityConfig");
    //     // Below works
    //     // return new InMemoryUserDetailsManager(
    //     // User.withDefaultPasswordEncoder()
    //     // .username("dwarika")
    //     // .password("password")
    //     // .roles("USER")
    //     // .build()
    //     // );

    //     // Below also works
    //     UserBuilder userBuilder = User.builder();
    //     UserDetails dwarika = userBuilder
    //             .username("dwarika")
    //             .password("{noop}password")
    //             .roles("USER", "ADMIN")
    //             .build();
    //     return new InMemoryUserDetailsManager(dwarika);
    // }

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
        return new JdbcUserDetailsManager(dataSource);
    }
  
    // @Bean
    // RegisteredClientRepository registeredClientRepository() {
    //     return new ClientRepository();
    // }

    @Bean
    RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    @Bean
    ApplicationRunner clientsRunner(RegisteredClientRepository registeredClientRepository, UserDetailsManager userDetailsManager) {
        return args -> {
            var clientId = "demo-client";
            if(registeredClientRepository.findByClientId(clientId) == null) {
                registeredClientRepository.save(RegisteredClient
                .withId(UUID.randomUUID().toString())
                    .clientId(clientId)
                    .clientSecret("{noop}ourtopsecretsecret")
                    .authorizationGrantTypes(agt -> agt.addAll(Set.of(AuthorizationGrantType.CLIENT_CREDENTIALS, AuthorizationGrantType.AUTHORIZATION_CODE, AuthorizationGrantType.REFRESH_TOKEN)))
                    .redirectUris(uris -> uris.addAll(Set.of("http://localhost:8082/login/oauth2/code/reg-client", "http://127.0.0.1:8082/login/oauth2/code/reg-client")))
                    .scopes(scopes -> scopes.addAll(Set.of("api.read","api.write","user.read", "openid")))
                    .clientAuthenticationMethods(cam -> cam.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC))
                    .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build()
            );
            }

            if(!userDetailsManager.userExists("dwarika")) {
                var userBuilder = User.builder();
                UserDetails dwarika = userBuilder.username("dwarika")
                    .password("{noop}password")
                    .roles("USER", "ADMIN")
                    .build();
                    userDetailsManager.createUser(dwarika);
            }
        };
    }
}

