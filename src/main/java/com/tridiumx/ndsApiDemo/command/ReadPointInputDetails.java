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
 * ReadPointInputDetails models the information about a specific input slot in the read
 * inputs result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadPointInputDetails
{
  @JsonProperty
  private String value;

  @JsonProperty
  private String inputPriority;

  @JsonProperty
  private String status;

  @JsonProperty
  private String sourceId;
}
