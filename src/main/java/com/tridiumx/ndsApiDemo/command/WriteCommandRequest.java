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
 * WriteCommandRequest models the write request sent to the Command Service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteCommandRequest
{
  @JsonProperty
  private List<WriteInfo> points;

  @JsonProperty(defaultValue = "255")
  private int requestProcessingPriority;
}
