/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo.model;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * SearchCriteria contains all the information a caller may or must supply to query the Entity
 * Model Service for points.
 */
@SuppressWarnings("unused")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SearchCriteria {

  /** SearchType defines the different ways of searching for points. */
  public enum SearchType {
    pointName,
    tagName,
    tagValue
  }

  /** ComparisonType defines the options for point name comparison. */
  public enum ComparisonType {
    any,
    exact,
    startswith,
    endswith,
    contains,
    equals
  }

  @JsonProperty
  private SearchType searchType;

  @JsonProperty
  private String systemGuid;

  @JsonProperty
  private List<String> searchItems;

  @JsonProperty
  private ComparisonType comparisonType;

  /**
   * Generate a criteria for getting all the points for a system.
   *
   * @param systemGuid systemGuid
   * @return criteria
   */
  public static SearchCriteria allPointsForSystem(String systemGuid) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        Collections.emptyList(),
        ComparisonType.any);
  }

  /**
   * Generate a criteria for querying by exact point name match.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameExact(String systemGuid, String... pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        List.of(pointNames),
        ComparisonType.exact);
  }

  /**
   * Generate a criteria for querying by exact point name match.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameExact(String systemGuid, List<String> pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        pointNames,
        ComparisonType.exact);
  }

  /**
   * Generate a criteria for querying by case-insensitive point name match.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameEquals(String systemGuid, String... pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        List.of(pointNames),
        ComparisonType.equals);
  }

  /**
   * Generate a criteria for querying by case-insensitive point name match.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameEquals(String systemGuid, List<String> pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        pointNames,
        ComparisonType.equals);
  }

  /**
   * Generate a criteria for querying by point name start.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameStart(String systemGuid, String... pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        List.of(pointNames),
        ComparisonType.startswith);
  }

  /**
   * Generate a criteria for querying by point name start.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameStart(String systemGuid, List<String> pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        pointNames,
        ComparisonType.startswith);
  }

  /**
   * Generate a criteria for querying by point name end.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameEnd(String systemGuid, String... pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        List.of(pointNames),
        ComparisonType.endswith);
  }

  /**
   * Generate a criteria for querying by point name end.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameEnd(String systemGuid, List<String> pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        pointNames,
        ComparisonType.endswith);
  }

  /**
   * Generate a criteria for querying by point name containing a string.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameContains(String systemGuid, String... pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        List.of(pointNames),
        ComparisonType.contains);
  }

  /**
   * Generate a criteria for querying by point name containing a string.
   *
   * @param systemGuid systemGuid
   * @param pointNames point names strings
   * @return criteria
   */
  public static SearchCriteria byPointNameContains(String systemGuid, List<String> pointNames) {
    return new SearchCriteria(
        SearchType.pointName,
        systemGuid,
        pointNames,
        ComparisonType.contains);
  }

  /**
   * Generate a criteria for querying by tag names.
   *
   * @param systemGuid systemGuid
   * @param tagNames tag names strings
   * @return criteria
   */
  public static SearchCriteria byTagNames(String systemGuid, List<String> tagNames) {
    return new SearchCriteria(
        SearchType.tagName,
        systemGuid,
        tagNames,
        ComparisonType.exact);
  }

  /**
   * Generate a criteria for querying by tag values.
   * The values strings must be in the format "key=value".
   *
   * @param systemGuid systemGuid
   * @param tagValues tag values strings
   * @return criteria
   */
  public static SearchCriteria byTagValues(String systemGuid, List<String> tagValues) {
    return new SearchCriteria(
        SearchType.tagValue,
        systemGuid,
        tagValues,
        ComparisonType.exact);
  }

  /**
   * Generate a criteria for querying by tag values.
   * This convenience flattens the map into the correct {@code List} format.
   *
   * @param systemGuid systemGuid
   * @param tagValues tag values strings
   * @return criteria
   */
  public static SearchCriteria byTagValues(String systemGuid, Map<String, String> tagValues) {
    return new SearchCriteria(
        SearchType.tagValue,
        systemGuid,
        tagValues.entrySet().stream().map(
            e -> e.getKey() + '=' + e.getValue()).collect(toList()),
        ComparisonType.exact);
  }
}
