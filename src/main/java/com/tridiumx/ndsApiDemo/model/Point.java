/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Point represents a Niagara point in the EMS model.
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Point {
  /** System Guid of the containing device. */
  private String systemGuid;

  /** Cloud Id of the point, by which it is referenced in TSDB. */
  private String cloudId;

  /** Point Friendly Name. */
  private String name;

  /** Tags associated to the Point. */
  private List<Tag> tags;

  /** Relations associated to the Point. */
  private List<Relation> relations;
}
