package com.chirayu.ecommerce.service;

import com.chirayu.ecommerce.entity.UserRole;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KeycloakService {

    @Value("${keycloak.admin.server-url}")
    private String serverUrl;

    @Value("${keycloak.admin.realm}")
    private String realm;

    @Value("${keycloak.admin.client-id}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;


    private Keycloak getKeycloakClient(){
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .clientId(clientId)
                .username(adminUsername)
                .password(adminPassword)
                .build();
    }

    public String createUser(String firstName, String lastName,
                             String email, String password, UserRole role){
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(email);
        user.setEnabled(true);
        user.setCredentials(List.of(credential));

        Keycloak keycloak=getKeycloakClient();
        Response response=keycloak.realm(realm).users().create(user);

        switch (response.getStatus()) {
            case 201 -> log.info("User created in Keycloak successfully");
            case 409 -> throw new RuntimeException("Email already exists: " + email);
            case 400 -> throw new RuntimeException("Bad request — check user data");
            default  -> throw new RuntimeException("Keycloak error: " + response.getStatus());
        }

        if (response.getStatus() != 201) {
            log.error("Failed to create user in Keycloak: {}", response.getStatusInfo());
            throw new RuntimeException("Failed to create user in Keycloak: "
                    + response.getStatusInfo());
        }

        String location = response.getLocation().toString();
        String keycloakId = location.substring(location.lastIndexOf("/") + 1);
        log.info("User created in Keycloak with id: {}", keycloakId);

        assignRole(keycloak, keycloakId, role);

        return keycloakId;
    }

    private void assignRole(Keycloak keycloak, String keycloakId, UserRole role) {
        String roleName = role.name();

        RoleRepresentation roleRepresentation = keycloak.realm(realm)
                .roles()
                .get(roleName)
                .toRepresentation();

        keycloak.realm(realm)
                .users()
                .get(keycloakId)
                .roles()
                .realmLevel()
                .add(List.of(roleRepresentation));

        log.info("Role {} assigned to keycloakId {}", roleName, keycloakId);
    }

    public void deleteUser(String keycloakId) {
        getKeycloakClient().realm(realm).users().delete(keycloakId);
        log.info("User deleted from Keycloak: {}", keycloakId);
    }
}
