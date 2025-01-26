package com.dworld.spring_oauth_server.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

public class ClientRepository implements RegisteredClientRepository {

    Map<String, RegisteredClient> clients = new HashMap<>();

    @Override
    public void save(RegisteredClient registeredClient) {
        clients.put(registeredClient.getClientId(), registeredClient);
    }

    @Override
    @Nullable
    public RegisteredClient findById(String id) {
        return clients.get(id);
    }

    @Override
    @Nullable
    public RegisteredClient findByClientId(String clientId) {
        return clients.get(clientId);
    }

}
