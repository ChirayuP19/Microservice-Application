package com.chirayu.ecom.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers("/api/v1/products/**").hasAnyRole("CUSTOMER","ADMIN")
                                .pathMatchers("/api/v1/orders/**").hasAnyRole("CUSTOMER","ADMIN")
                                .pathMatchers("/api/v1/cart/**").hasAnyRole("CUSTOMER","ADMIN")
                                .pathMatchers("/api/v1/users/**").permitAll()
                                .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt->jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor())))
                .build();
    }

//    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor(){
//        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter=
//                new ReactiveJwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt->{
//            List<String> roles=jwt.getClaimAsMap("resource_access").entrySet().stream()
//                    .filter(entry->entry.getKey().equals("oauth-2-pkce"))
//                    .flatMap(entry->((Map<String,List<String>>) entry.getValue())
//                            .get("roles").stream())
//                    .toList();
//
//            return Flux.fromIterable(roles)
//                    .map(role->new SimpleGrantedAuthority("ROLE_" +role));
//        });
//        return jwtAuthenticationConverter;
//    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        ReactiveJwtAuthenticationConverter jwtAuthenticationConverter =
                new ReactiveJwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

            if (realmAccess == null || !realmAccess.containsKey("roles")) {
                return Flux.empty();
            }

            List<String> roles = (List<String>) realmAccess.get("roles");

            return Flux.fromIterable(roles)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role));
        });

        return jwtAuthenticationConverter;
    }
}
