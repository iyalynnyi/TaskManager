package com.iyalynnyi.taskmanager.controller.common;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.iyalynnyi.taskmanager.exception.ApiResponseException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * Handles {@link ApiResponseException} and returns an appropriate client response.
   *
   * @param ex the {@link ApiResponseException} to handle
   * @return response with the appropriate client message and HTTP status code
   */
  @ExceptionHandler(value = {ApiResponseException.class})
  public ResponseEntity<String> handleApiException(ApiResponseException ex) {
    log.error("ApiResponseException occurred: {}", ex.getErrorMessage(), ex);
    return new ResponseEntity<>(ex.getErrorMessage(), ex.getHttpStatus());
  }

  /**
   * Handles validation errors when method arguments are not valid.
   *
   * @param ex the {@link MethodArgumentNotValidException} that occurs during validation
   * @param headers the headers to include in the response
   * @param status the HTTP status code to return
   * @param request the current request
   * @return response containing the validation error messages
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.warn("Validation failed for method argument: {}", ex.getBindingResult());
    Map<String, String> errors = new HashMap<>();

    ex.getBindingResult().getFieldErrors().forEach(error -> {
      String fieldName = error.getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return new ResponseEntity<>(errors, headers, status);
  }

  /**
   * Handles cases where the HTTP message is not readable.
   *
   * @param ex the {@link HttpMessageNotReadableException} that occurs
   * @param headers the headers to include in the response
   * @param status the HTTP status code to return
   * @param request the current request
   * @return response containing the error message related to unreadable HTTP message
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.error("HttpMessageNotReadableException occurred: {}", ex.getMessage(), ex);
    Throwable cause = ex.getCause();

    if (cause instanceof InvalidFormatException && cause.getMessage().contains("not one of the values accepted for Enum class")) {
      String message = ex.getMessage();

      String invalidValue = message.substring(message.indexOf("\"") + 1, message.lastIndexOf("\""));

      String[] enumFullNameParts = message.substring(message.indexOf("`") + 1, message.lastIndexOf("`")).split("\\.");
      String enumName = enumFullNameParts[enumFullNameParts.length - 1];
      String[] enumValues = message.substring(message.indexOf("[") + 1, message.lastIndexOf("]")).split(",");

      String errorMessage = String.format(
          "Invalid value '%s' for field '%s'. Accepted values are: [%s]",
          invalidValue, enumName, String.join(", ", enumValues)
      );

      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(errorMessage);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request data. " + ex.getMessage());
  }

  /**
   * Handles type mismatch exceptions.
   *
   * @param ex the {@link TypeMismatchException} that occurs
   * @param headers the headers to include in the response
   * @param status the HTTP status code to return
   * @param request the current request
   * @return response containing the error message for type mismatch
   */
  @Override
  protected ResponseEntity<Object> handleTypeMismatch(
      TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    log.warn("Type mismatch occurred for parameter: {}", ex.getPropertyName());

    if (!ex.getRequiredType().isEnum()) {
      return super.handleTypeMismatch(ex, headers, status, request);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(String.format("Invalid data provided for parameter [%s]. Possible values are: %s",
            ex.getPropertyName(), Arrays.stream(ex.getRequiredType().getEnumConstants())
                .map(Object::toString)
                .toList()));
  }
}

