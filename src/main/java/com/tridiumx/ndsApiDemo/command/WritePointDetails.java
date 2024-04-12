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
 * WritePointDetails models the information about a specific point in the write result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WritePointDetails
{
  @JsonProperty
  private String cloudId;

  @JsonProperty
  private String status;

  @JsonProperty
  private String writeTime;
}
