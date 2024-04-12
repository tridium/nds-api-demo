/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Page models the information about a page of data returned from the API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
  @JsonProperty
  private int size;

  @JsonProperty
  private int totalElements;

  @JsonProperty
  private int totalPages;

  @JsonProperty
  private int number;
}
