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
 * EmsQueryResponse models the response format from the Entity Model
 * Service (EMS).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmsQueryResponse {
  @JsonProperty("_embedded")
  private EmsQueryResults content;

  @JsonProperty("_links")
  private Object links;

  @JsonProperty
  private Page page;
}
