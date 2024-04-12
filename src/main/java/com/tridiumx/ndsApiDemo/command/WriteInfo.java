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
 * WriteInfo models information about a write to a specific point.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteInfo
{
  @JsonProperty
  private String cloudId;

  @JsonProperty
  private Object value;

  @JsonProperty
  private int duration;

  @JsonProperty
  private int inputPriority;
}
