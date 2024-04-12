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
 * WriteCommandResponse models the response from the Command Service for
 * write command requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WriteCommandResponse
{
  @JsonProperty
  private List<WritePointDetails> pointWriteDetails;
}
