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
 * ReadPointInputAllDetails models the information about a specific point's inputs in the read
 * inputs result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadPointInputAllDetails
{
  @JsonProperty
  private String cloudId;

  @JsonProperty
  private List<ReadPointInputDetails> inputValues;
}
