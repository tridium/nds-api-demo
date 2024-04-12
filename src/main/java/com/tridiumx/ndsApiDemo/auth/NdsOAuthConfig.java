/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.auth;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OAuth Configuration Holder.  This manages the configuration for OAuth
 * requests for an access token.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NdsOAuthConfig {
  private String tenantId;
  private String tokenUri;
  private String jwksUri;
  private String clientId;
  private String clientSecret;
  private List<String> scope;
}
