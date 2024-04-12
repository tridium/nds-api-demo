/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo;

import java.util.List;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;

import com.tridiumx.ndsApiDemo.auth.NdsOAuthConfig;

//@Configuration
@Slf4j
public class NdsOAuthClient {
  private static final String NCP_API_CLIENT_REGISTRATION_ID = "nds-api-demo";
  @Getter
  private NdsOAuthConfig config;
  @Getter
  private WebClient client;


  public NdsOAuthClient(NdsOAuthConfig config) {
    this.config = config;
    try {
      ClientRegistration authClient = ClientRegistration
        .withRegistrationId(NCP_API_CLIENT_REGISTRATION_ID)
        .tokenUri(config.getTokenUri())
        .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
        .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
        .clientId(config.getClientId())
        .clientSecret(config.getClientSecret())
        .scope(config.getScope())
        .build();
      ReactiveClientRegistrationRepository repo =
        new InMemoryReactiveClientRegistrationRepository(List.of(authClient));
      InMemoryReactiveOAuth2AuthorizedClientService clientService =
        new InMemoryReactiveOAuth2AuthorizedClientService(repo);
      AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager clientManager =
        new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(repo, clientService);
      ServerOAuth2AuthorizedClientExchangeFilterFunction ndsApiFilter =
        new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientManager);
      ndsApiFilter.setDefaultClientRegistrationId(NCP_API_CLIENT_REGISTRATION_ID);
      client = WebClient.builder()
        .filter(ndsApiFilter)
        .build();
      log.info("Created NDS OAuth2 webclient filter with id {}", NCP_API_CLIENT_REGISTRATION_ID);
    } catch (Exception e) {
      log.error("Error creating NCP webclient: {}", e.getMessage());
    }
  }
}
