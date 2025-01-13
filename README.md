# chat-service
A chat service for learning Spring Boot

# based on -
* https://www.youtube.com/watch?v=0C9S1yBSSO8
* https://www.youtube.com/watch?v=IIFAiNMiywQ


# Steps: -

1. Setup Chat Server
2. Setup Chat Client 
3. Setup OAuth Server -
    
    a. Create a  config/Security Config 
    
    b. Create a UserDetailsService in it. Pass a UserBuilder with encoding

    c. Add the `client-id` and `secret to` to application.yml.

    ```
    security:
        oauth2:
            authorizationserver:
                client:
                demo-client:
                    require-authorization-consent: true
                    registration:
                    client-id: demo-client
                    client-secret: "{noop}ourtopsecretsecret"
                    authorization-grant-types:
                        - client_credentials
                        - authorization_code
                        - refresh_token
                    redirect-uris: 
                        - "http://127.0.0.1:8082/login/oauth2/code/reg-client"
                        - "http://localhost:8082/login/oauth2/code/reg-client"
                    scopes: 
                        - "api.read"
                        - "api.write"
                        - "user.read"
                        - openid
                    client-authentication-methods:
                        - client_secret_basic
    ```

4. Secure the Chat Server:
    
    a.  Add a dependency -

        
            <dependency>
			    <groupId>org.springframework.boot</groupId>
			    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
		    </dependency>
        
    b. Add a `SecurityConfig` Bean with a `SecurityFilterChain` into it.
    ```
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        log.info("Entered securityFilterChain.");
        httpSecurity.authorizeHttpRequests(authz -> authz.anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        log.info("httpSecurity in request %s  $".formatted(httpSecurity.toString()));
        return httpSecurity.build();
    }
    ```

    c. Add `jwt.issuer-uri` to `application.yml`

    ```
    spring:
        security:
            oauth2:
                resourceserver:
                    jwt:
                        issuer-uri: http://localhost:8080
    ```

5. Add the `Authorized` entry for chat client to the oauth Server.

    a. Add the security attribute to both the client and oauth server about the `registration` with the `  client_id` in oAuth server and `reg-client` in `client serer.

    ```
    security:
        oauth2:
            client:
                provider:
                    spring:
                        issuer-uri: http://localhost:8080
                    registration:
                        reg-client:
                            provider: spring
                            client-id: demo-client
                            # client-secret: "{noop}ourtopsecretsecret"
                            client-secret: "ourtopsecretsecret"
                            authorization-grant-type: authorization_code
                            # authorization-grant-type: client_credentials
                            client-authentication-method: client_secret_basic
                            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
                            scope: user.read,openid
    ```
    
    b. Add a reference to oAuth2AuthorizedClient in the  chat-client to  validate the token.

    ```
    @GetMapping("/message")
    public String message(Principal principal ) {
        
        var restTemplate = new RestTemplate();
        String accessToken = authorizedClientService.loadAuthorizedClient("reg-client", principal.getName()).getAccessToken().getTokenValue();
        
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        
        HttpEntity<Object> httpEntity = new HttpEntity<>(httpHeaders);
        var response =  restTemplate.exchange("http://localhost:8081/hello", HttpMethod.GET, httpEntity, String.class);
        return "Success ::" + response.getBody();
    }
    ```



># How to test-
### Client Credentials flow -

In this case the client has a client secret and a registerded client id stored somewhere in its's code (application.yml).

1. The chat client requests a token -
```
curl --location 'http://localhost:8080/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--header 'Authorization: Basic base64(demo-client:ourtopsecretsecret)' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'scope=user.read'
```

2. Invoke the /message uri from chat server with the jwt token received in step 1.
```
curl --location 'http://localhost:8081/hello' \
--header 'Authorization: Bearer jwt_token' \
```

### From Browser

1. Invoke http://127.0.0.1:8082/message from browser.
2. It gets redirected to oauth server at http://localhost:8080/login asking for user credentials
3. Enter the username and password.
4. Consent screen appears. allow it.
5. The request is sent to resource server which returns the response
6. Invoke http://localhost:8082/message again, no authentication screen is invoked as the JSESSIONID in cookie is included.
7. Delete the cookie in browser and hit the same /message again.
8. The request is redirected to `
http://127.0.0.1:8082/oauth2/authorization/reg-client` -- 302 Found
9. Then redirected to 

```
http://localhost:8080/oauth2/authorize?response_type=code&client_id=demo-client&scope=user.read%20openid&state=YDV72MpRGUbj61BF3zQNZZy03Yalr1RDX6JUytKwHrc%3D&redirect_uri=http://127.0.0.1:8082/login/oauth2/code/reg-client&nonce=bjv5uiBK56GB8dN5HvRmTp8H7rIfIrKzdJZBrjhsHFc -- 302 Found
``` 

This returns the redirect url as `Location` response header. 

10. Then the above `Location` response header url is used for redirection
 to 
 ```
 http://127.0.0.1:8082/login/oauth2/code/reg-client?code=E9TAk4T8hY9LpG2CGJTJd1fixzvkXNM53h8SpnqPnWz_AxHdBwppwq3sd9xvYfcl4Uae-WP2r3t-o5PIgHfW-Gun70btYz2nm2YRAi_7WgZKzg2lZhvLZvrEngTu2D8z&state=YDV72MpRGUbj61BF3zQNZZy03Yalr1RDX6JUytKwHrc%3D` -- 302 Found.
 ```

> Note the `code` in the above redirect url.

This returns a `JSESSIONID` in response `Cookie`.

11. The same `JSESSIONID` is included with the call to http://127.0.0.1:8082/message which is works and brings the valid response.


