/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.tsdb;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * HistoryRecord models a history record returned from the Egress API from
 * the NDS time series database (TSDB).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIncludeProperties({ "time", "value", "properties" })
public class HistoryRecord {

  @JsonProperty
  private String time;

  @JsonProperty
  private Object value;

  @JsonProperty
  private String properties;
}
