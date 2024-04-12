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
 * ReadCommandResponse models the response from the Command Service for
 * read command requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadCommandResponse
{
  @JsonProperty
  private List<ReadPointDetails> pointReadDetails;
}
