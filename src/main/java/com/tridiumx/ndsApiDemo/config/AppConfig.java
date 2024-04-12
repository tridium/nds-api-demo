/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.tridiumx.ndsApiDemo.auth.NdsOAuthConfig;
import com.tridiumx.ndsApiDemo.model.SearchCriteria;

/**
 * AppConfig manages the configuration of the demo application defined from
 * the properties files.  It supplies {@link Bean}s to the main application.
 */
@Configuration
public class AppConfig {
  @Value("${nds.idp.tokenUri}")
  private String ndsIdpTokenUri;

  @Value("${nds.idp.clientId}")
  private String ndsIdpClientId;

  @Value("${nds.idp.clientSecret}")
  private String ndsIdpClientSecret;

  @Value("#{'${nds.idp.scope}'.split(' ')}")
  private List<String> ndsIdpScope;

  @Value("${demo.systemGuid}")
  private String systemGuid;

  @Value("${demo.customerId}")
  private int customerId;

  @Value("${demo.searchCriteria.searchType}")
  private SearchCriteria.SearchType searchType;

  @Value("#{'${demo.searchCriteria.searchItems}'.split(' ')}")
  private List<String> searchItems;

  @Value("${demo.searchCriteria.comparisonType}")
  private SearchCriteria.ComparisonType comparisonType;

  @Value("${demo.startTimeStr}")
  private String startTimeStr;

  @Value("${demo.endTimeStr}")
  private String endTimeStr;


  /** OAuth Configuration Bean */
  @Bean(name = "ndsAppConfig")
  @Profile("local")
  public NdsOAuthConfig ndsOAuthConfig() {
    NdsOAuthConfig config = NdsOAuthConfig.builder()
      .tokenUri(ndsIdpTokenUri)
      .clientId(ndsIdpClientId)
      .clientSecret(ndsIdpClientSecret)
      .scope(ndsIdpScope)
      .build();
    return config;
  }

  /** EMS/Egress Query Configuration Bean */
  @Bean(name = "ndsQueryConfig")
  @Profile("local")
  public QueryConfig QueryConfig() {
    QueryConfig config = QueryConfig.builder()
      .systemGuid(systemGuid)
      .customerId(customerId)
      .searchType(searchType)
      .searchItems(searchItems)
      .comparisonType(comparisonType)
      .startTimeStr(startTimeStr)
      .endTimeStr(endTimeStr)
      .build();
    return config;
  }

}
