/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.command;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReadCommandRequest models the read request sent to the Command Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadCommandRequest
{
  @JsonProperty
  private List<String> cloudIds;

  @JsonProperty
  private int requestProcessingPriority = 255;
}
