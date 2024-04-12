/*
 * Copyright 2024 Tridium, Inc. All Rights Reserved.
 */

package com.tridiumx.ndsApiDemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import com.tridiumx.ndsApiDemo.auth.NdsApiTokenHolder;
import com.tridiumx.ndsApiDemo.auth.NdsOAuthConfig;
import com.tridiumx.ndsApiDemo.command.ReadCommandRequest;
import com.tridiumx.ndsApiDemo.command.ReadCommandResponse;
import com.tridiumx.ndsApiDemo.command.ReadInputsCommandResponse;
import com.tridiumx.ndsApiDemo.command.WriteCommandRequest;
import com.tridiumx.ndsApiDemo.command.WriteCommandResponse;
import com.tridiumx.ndsApiDemo.command.WriteInfo;
import com.tridiumx.ndsApiDemo.config.QueryConfig;
import com.tridiumx.ndsApiDemo.model.EmsQueryResponse;
import com.tridiumx.ndsApiDemo.model.EmsQueryResults;
import com.tridiumx.ndsApiDemo.model.Point;
import com.tridiumx.ndsApiDemo.model.SearchCriteria;
import com.tridiumx.ndsApiDemo.model.Tag;
import com.tridiumx.ndsApiDemo.tsdb.TsdbQuery;
import com.tridiumx.ndsApiDemo.tsdb.TsdbQueryResponse;

/**
 * <p>NdsApiDemoApplication is a simple Spring Boot application to demonstrate the usage
 * of the NDS APIs.  It makes an OAuth request to obtain an access token from the NCS
 * Identity Endpoint, then it uses that token to make a query to the Entity Model
 * Service (EMS) to find points.  The points found are then queried from the Egress
 * Service to find telemetry data.</p>
 * <p>This demo application was demonstrated at the 2023 Niagara Forum.</p>
 */
@SpringBootApplication
@Slf4j
public class NdsApiDemoApplication {

  public static final String AMPERSAND = "&";
  public static final String EQUALS = "=";

  // API Host Endpoint
  @Value("${nds.api.host}")
  String ndsApiHost;

  // HTTP request timeout in seconds
  @Value("${nds.api.timeout}")
  Integer ndsApiTimeoutSeconds;

  // Path to Data Egress API
  @Value("${nds.api.dataEgressRoute}")
  String dataEgressRoute;

  // Path to EMS API
  @Value("${nds.api.emsRoute}")
  String emsRoute;

  // Base path to Command API
  @Value("${nds.api.commandRoute}")
  String commandRoute;

  // JSON mapper for serialization and deserialization
  private final ObjectMapper mapper = new ObjectMapper();


  // OAuth configuration holder
  @Autowired
  NdsOAuthConfig authConfig;

  // Query configuration holder
  @Autowired
  QueryConfig queryConfig;

  // Simple HTTP Client used for all requests
  HttpClient httpClient = HttpClient.newBuilder().build();

  /**
   * The main entry point for the application.
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(NdsApiDemoApplication.class, args).close();
  }

  /**
   * The command line runner will execute all the steps in the application.
   * @param restTemplate the (unused) REST template
   * @return a CommandLineRunner
   * @throws Exception if a failure occurs
   */
  @Bean
  public CommandLineRunner run(@SuppressWarnings("unused") RestTemplate restTemplate) throws Exception {

    log.info(String.format("Running NDS API Usage Demo with customerId: %s, systemGuid:%s",
      queryConfig.getSystemGuid(), queryConfig.getCustomerId()));

    return args -> {

      // Get an access token
      log.info("Get access token from NCS Identity Endpoint");
      NdsApiTokenHolder holder = getAccessToken();
      log.info("Access Token acquired!\n" + holder.getAccessToken() + "\n\n");
      waitInput();

      // Query EMS for points
      log.info(String.format("Query EMS for points matching search criteria\n%s", queryConfig));
      waitInput();
      List<Point> points = modelQuery(holder.getAccessToken());
      log.info("Points found: "+points.size());
      waitInput();

      // Query Data Egress service for telemetry data
      log.info("Query Egress service for telemetry data on each point");
      for (Point point : points) {
        Optional<String> telemetryId = point.getTags().stream().filter(t -> "nc:telemetryId".equals(t.getTagId())).map(t -> t.getTagValue()).findFirst();
        if (telemetryId.isEmpty())
        { // no telemetry stream for this point so skip it
          continue;
        }
        log.info(String.format("Point:%s [cloudId=%s], [telemetryId=%s]", point.getName(), point.getCloudId(), telemetryId.get()));
        TsdbQueryResponse telemetryData = readTelemetryData(
          holder.getAccessToken(), queryConfig.getSystemGuid(), telemetryId.get());
        if (!telemetryData.getPointDetails().isEmpty()) {
          log.info(String.format("Query results for %s:\n%s\n\n", point.getName(),
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(telemetryData)));
        } else {
          log.info("No telemetry data for point!\n\n");
        }
        waitInput();
      }

      // Execute sample commands
      log.info("Send Commands for selected points");
      for (Point point : points)
      {
        Optional<Tag> setPoint = point.getTags().stream().filter(t -> "hs:sp".equals(t.getTagId())).findFirst();
        if (setPoint.isEmpty())
        {
          continue;
        }
        log.info(String.format("Point:%s [cloudId=%s]", point.getName(), point.getCloudId()));
        ReadCommandResponse readResponse = readPointCommand(
          holder.getAccessToken(), queryConfig.getSystemGuid(), point.getCloudId());
        if (!readResponse.getPointReadDetails().isEmpty()) {
          log.info(String.format("Read command results for %s:\n%s\n\n", point.getName(),
            mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readResponse)));
          waitInput();
          double value = Double.parseDouble(readResponse.getPointReadDetails().get(0).getValue()) + 5;
          int duration = 15;
          int priority = 10;
          WriteCommandResponse writeResponse = writePointCommand(holder.getAccessToken(),
            queryConfig.getSystemGuid(), point.getCloudId(), value, duration, priority);
          if (!writeResponse.getPointWriteDetails().isEmpty()) {
            log.info(String.format("Write command results for %s:\n%s\n\n", point.getName(),
              mapper.writerWithDefaultPrettyPrinter().writeValueAsString(writeResponse)));
          } else {
            log.info("No write results for point!\n\n");
          }
          waitInput();
          ReadInputsCommandResponse readInputsResponse = readPointInputsCommand(
            holder.getAccessToken(), queryConfig.getSystemGuid(), point.getCloudId());
          if (!readInputsResponse.getPointReadAllDetails().isEmpty()) {
            log.info(String.format("Read inputs command results for %s:\n%s\n\n", point.getName(),
              mapper.writerWithDefaultPrettyPrinter().writeValueAsString(readInputsResponse)));
          } else {
            log.info("No read input results for point!\n\n");
          }
        } else {
          log.info("No read results for point!\n\n");
        }
        waitInput();
      }
      log.info("Finished NDS API Usage Demo!");
    };
  }
  /**
   * Send a command to read the current value of the specified point from the Command Service API.
   * This accepts only a single cloudId, although the NDS API itself can accept a {@link List} of
   * cloudIds for the {@code cloudId} parameter.
   * @param accessToken an access token obtained from the identity endpoint
   * @param systemGuid the system GUID for the device in which the point resides
   * @param cloudId the cloudId for which data is requested
   * @param value the value to write to the point
   * @param duration the time in minutes that this value should apply
   * @param priority the input priority at which to write the point
   * @return the command result in a {@link ReadCommandResponse} object
   * @throws Exception if an error occurs
   */
  private WriteCommandResponse writePointCommand(String accessToken, String systemGuid, String cloudId,
    Object value, int duration, int priority)
    throws Exception
  {
    WriteCommandRequest request = WriteCommandRequest.builder()
      .points(List.of(WriteInfo.builder()
        .cloudId(cloudId)
        .value(value)
        .duration(duration)
        .inputPriority(priority)
        .build()))
      .build();
    String requestBody = mapper.writeValueAsString(request);

    // Build request for Command Service
    String route = String.format(commandRoute, systemGuid);
    HttpRequest commandRequest = HttpRequest.newBuilder()
      .uri(URI.create(ndsApiHost + route + "write"))
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + accessToken)
      .build();

    // Send HTTP request to Command Service and handle response
    HttpResponse<String> response = httpClient.send(
      commandRequest, HttpResponse.BodyHandlers.ofString());
    WriteCommandResponse commandResponse = mapper.readValue(
      response.body(), WriteCommandResponse.class);
    return commandResponse;
  }

  /**
   * Send a command to read the current input values of the specified point from the Command Service
   * API. This accepts only a single cloudId, although the NDS API itself can accept a {@link List}
   * of cloudIds for the {@code cloudId} parameter.
   * @param accessToken an access token obtained from the identity endpoint
   * @param systemGuid the system GUID for the device in which the point resides
   * @param cloudId the cloudId for which data is requested
   * @return the command result in a {@link ReadInputsCommandResponse} object
   * @throws Exception if an error occurs
   */
  private ReadInputsCommandResponse readPointInputsCommand(String accessToken, String systemGuid, String cloudId)
    throws Exception
  {
    ReadCommandRequest request = ReadCommandRequest.builder()
      .cloudIds(List.of(cloudId))
      .build();
    String requestBody = mapper.writeValueAsString(request);

    // Build request for Command Service
    String route = String.format(commandRoute, systemGuid);
    HttpRequest commandRequest = HttpRequest.newBuilder()
      .uri(URI.create(ndsApiHost + route + "readAll"))
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + accessToken)
      .build();

    // Send HTTP request to Command Service and handle response
    HttpResponse<String> response = httpClient.send(
      commandRequest, HttpResponse.BodyHandlers.ofString());
    ReadInputsCommandResponse commandResponse = mapper.readValue(
      response.body(), ReadInputsCommandResponse.class);
    return commandResponse;
  }

  /**
   * Send a command to read the current value of the specified point from the Command Service API.
   * This accepts only a single cloudId, although the NDS API itself can accept a {@link List} of
   * cloudIds for the {@code cloudId} parameter.
   * @param accessToken an access token obtained from the identity endpoint
   * @param systemGuid the system GUID for the device in which the point resides
   * @param cloudId the cloudId for which data is requested
   * @return the command result in a {@link ReadCommandResponse} object
   * @throws Exception if an error occurs
   */
  private ReadCommandResponse readPointCommand(String accessToken, String systemGuid, String cloudId)
    throws Exception
  {
    ReadCommandRequest request = ReadCommandRequest.builder()
      .cloudIds(List.of(cloudId))
      .build();
    String requestBody = mapper.writeValueAsString(request);

    // Build request for Command Service
    String route = String.format(commandRoute, systemGuid);
    HttpRequest commandRequest = HttpRequest.newBuilder()
      .uri(URI.create(ndsApiHost + route + "read"))
      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + accessToken)
      .build();

    // Send HTTP request to Command Service and handle response
    HttpResponse<String> response = httpClient.send(
      commandRequest, HttpResponse.BodyHandlers.ofString());
    ReadCommandResponse commandResponse = mapper.readValue(
      response.body(), ReadCommandResponse.class);
    return commandResponse;
  }

  /**
   * Read the telemetry data from the Egress Service API.  This accepts only a single telemetryId,
   * although the NDS API itself can accept a {@link List} of cloudIds for the {@code telemetryId}
   * parameter.
   * @param accessToken an access token obtained from the identity endpoint
   * @param systemGuid the system GUID for the device in which the point resides
   * @param telemetryId the telemetryId for which data is requested
   * @return the telemetry data in a {@link TsdbQueryResponse} object
   * @throws Exception if an error occurs
   */
  private TsdbQueryResponse readTelemetryData(String accessToken, String systemGuid, String telemetryId)
    throws Exception {

    // Build telemetry query
    TsdbQuery query = TsdbQuery.builder()
      .systemGuid(systemGuid)
      .startTimeStr(queryConfig.getStartTimeStr())
      .endTimeStr(queryConfig.getEndTimeStr())
      .recordLimitStr("5")
      .cloudId(List.of(telemetryId))
      .build();
    String queryBody = mapper.writeValueAsString(query);

    // Build request for Egress Service
    HttpRequest telemetryRequest = HttpRequest.newBuilder()
      .uri(URI.create(ndsApiHost + dataEgressRoute))
      .POST(HttpRequest.BodyPublishers.ofString(queryBody))
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + accessToken)
      .build();

    // Send HTTP request to Egress Service and handle response
    HttpResponse<String> response = httpClient.send(
      telemetryRequest, HttpResponse.BodyHandlers.ofString());
    TsdbQueryResponse telemetryData = mapper.readValue(
      response.body(), TsdbQueryResponse.class);
    return telemetryData;
  }

  /**
   * Query the Entity Model Service (EMS) for semantic model information by
   * point name, tag name, or tag value.  The query configuration is obtained
   * from the class's query configuration object.  The access token must be
   * passed in.
   * @param accessToken an access token obtained from the identity endpoint
   * @return a {@link List} of {@link Point} objects that match the query
   * @throws Exception if an error occurs
   */
  private List<Point> modelQuery(String accessToken) throws Exception {

    // Build EMS search query
    SearchCriteria searchQuery = new SearchCriteria(
      queryConfig.getSearchType(),
      queryConfig.getSystemGuid(),
      queryConfig.getSearchItems(),
      queryConfig.getComparisonType());
    String queryBody = mapper.writeValueAsString(
      searchQuery
    );

    // Build EMS query request.  The search criteria are expressed as a JSON
    // object in the POST body.
    String route = String.format(emsRoute, queryConfig.getCustomerId());
    HttpRequest modelQuery = HttpRequest.newBuilder()
      .uri(URI.create(ndsApiHost + route))
      .POST(HttpRequest.BodyPublishers.ofString(queryBody))
      .header("Content-Type", "application/json")
      .header("Authorization", "Bearer " + accessToken)
      .build();

    // Send HTTP request to EMS and handle response
    log.info("Model Query request: "+modelQuery+ '\n' +queryBody);
    HttpResponse<String> response = httpClient.send(modelQuery, HttpResponse.BodyHandlers.ofString());
    log.info("Model Query response: status code is "+response.statusCode()+ '\n' +response.body());
    EmsQueryResponse body = mapper.readValue(response.body(), EmsQueryResponse.class);
    List<Point> points = Optional.ofNullable(body.getContent()).orElse(new EmsQueryResults()).getPoints();
    return points;
  }

  /**
   * Get an access token from the NCS OAuth endpoint.
   * @return the access token
   * @throws Exception if an error occurs
   */
  private NdsApiTokenHolder getAccessToken() throws Exception {

    // Use form data for authentication type and scope
    Map<Object, Object> requestData = new HashMap<>();
    requestData.put("grant_type", "client_credentials");
    requestData.put("scope", "ncp:read");

    // Build the HTTP Request
    HttpRequest tokenRequest = HttpRequest.newBuilder()
      // Use NCS authentication endpoint from configuration file
      .uri(URI.create(authConfig.getTokenUri()))

      // Serialize request data into JSON string
      .POST(toFormData(requestData))

      // Include content type for the form data
      .header("Content-Type", "application/x-www-form-urlencoded")

      // Include the authorization header
      .header("Authorization", getAuthHeader(authConfig.getClientId(), authConfig.getClientSecret()))
      .build();

    // Send HTTP request and handle response
    HttpResponse<String> response = httpClient.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
    NdsApiTokenHolder tokenHolder =  mapper.readValue(response.body(), NdsApiTokenHolder.class);
    return tokenHolder;
  }

  /**
   * Convenience method to generate the authorization header for the token request.
   * @param username client id
   * @param password client secret
   * @return the encoded auth header
   */
  private static String getAuthHeader(String username, String password) {
    String toEncode = username + ':' + password;
    return "Basic " + Base64.getEncoder().encodeToString(toEncode.getBytes());
  }

  /**
   * Encode the mapped data into an HTTP form data format.
   * @param dataMap the list of properties
   * @return the encoded form data
   */
  private static HttpRequest.BodyPublisher toFormData(Map<Object, Object> dataMap) {
    StringBuilder builder = new StringBuilder();
    dataMap.forEach((key, value) -> {
      if (!builder.isEmpty())
      {
        builder.append(AMPERSAND);
      }
      builder.append(URLEncoder.encode(key.toString(), StandardCharsets.UTF_8))
        .append(EQUALS)
        .append(URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));
    });
    return HttpRequest.BodyPublishers.ofString(builder.toString());
  }

  /**
   * Convenience method to wait before proceeding with demo
   */
  private static void waitInput() {
    try { in.readLine(); } catch (IOException ignored) {}
  }

  // REST template bean
  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
}
