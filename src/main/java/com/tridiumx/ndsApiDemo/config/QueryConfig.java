/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.tridiumx.ndsApiDemo.model.SearchCriteria;

/**
 * Query Configuration Holder.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryConfig {
  private String systemGuid;
  private int customerId;
  private SearchCriteria.SearchType searchType;
  private List<String> searchItems;
  private SearchCriteria.ComparisonType comparisonType;
  private String startTimeStr;
  private String endTimeStr;
}
