/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReadPointDetails models the information about a specific point in the read result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadPointDetails
{
  @JsonProperty
  private String cloudId;

  @JsonProperty
  private String value;

  @JsonProperty
  private String status;
}
