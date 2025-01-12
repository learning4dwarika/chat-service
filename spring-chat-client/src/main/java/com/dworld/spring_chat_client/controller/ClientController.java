package com.dworld.spring_chat_client.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.security.Principal;

@RestController
public class ClientController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/message")
    public String message(Principal principal ) {
        log.info("Received request to /message with Principal %s".formatted(principal.toString()));
        var restTemplate = new RestTemplate();
        String accessToken = authorizedClientService.loadAuthorizedClient("reg-client", principal.getName()).getAccessToken().getTokenValue();
        log.info("Fetched accessToken is: %s".formatted(accessToken));
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        // httpHeaders.setBearerAuth(accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
        var response =  restTemplate.exchange("http://localhost:8081/hello", HttpMethod.GET, httpEntity, String.class);
        return "Success ::" + response.getBody();
    }
}
