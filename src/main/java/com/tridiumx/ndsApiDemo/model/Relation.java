/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Relation models a Niagara relation in the Entity Model Service (EMS)
 * database.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Relation {

  @JsonProperty("rId")
  private String id;

  @JsonProperty("cId")
  private String cloudId;

  private Boolean inbound;

  @JsonIgnore
  @ToString.Exclude
  private Point point;
}
