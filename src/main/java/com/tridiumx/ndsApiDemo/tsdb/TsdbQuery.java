/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.tsdb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TsdbQuery models the query sent to the Data Egress Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TsdbQuery {

  public static final int RECORD_LIMIT_DEFAULT = 500;
  public static final String RECORD_LIMIT_DEFAULT_STR = ""+RECORD_LIMIT_DEFAULT;

  @JsonProperty
  private String systemGuid;

  @JsonProperty
  private List<String> cloudId;

  @JsonProperty(value = "startTime", defaultValue = "1970-01-01T00:00:00.000Z")
  private String startTimeStr;

  @JsonProperty(value = "endTime", defaultValue = "2038-01-19T03:14:07.000Z")
  private String endTimeStr;

  @JsonProperty(value = "recordLimit", defaultValue = RECORD_LIMIT_DEFAULT_STR)
  private String recordLimitStr;

  @JsonProperty(defaultValue = "true")
  private boolean includePreRecord;

  @JsonProperty(defaultValue = "true")
  private boolean includePostRecord;

  @JsonProperty(defaultValue = "false")
  private boolean sortAscending;
}
