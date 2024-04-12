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
 * PointData models the point data returned in the response from the Egress
 * API for the time series database (TSDB).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointData {

  /**
   * Point ID is the identifier for the point in the TSDB.
   */
  @JsonProperty
  private String pointId;

  /**
   * Pre-record is used to extrapolate values earlier than the oldest record
   * for chart drawing.
   */
  @JsonProperty
  private HistoryRecord preRecord;

  /**
   * Post-record is used to extrapolate values later than the newest record
   * for chart drawing.
   */
  @JsonProperty
  private HistoryRecord postRecord;

  /**
   * History Records is the list of history records found.
   */
  @JsonProperty
  private List<HistoryRecord> historyRecords;

}
