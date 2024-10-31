package com.iyalynnyi.taskmanager.exception;

import org.springframework.http.HttpStatusCode;

/**
 * Custom exception class to represent API response errors.
 * This exception encapsulates an error message and an associated HTTP status code.
 */
public class ApiResponseException extends RuntimeException {

  private final String errorMessage;
  private final HttpStatusCode httpStatus;

  /**
   * Constructs a new ApiResponseException with the specified error message and HTTP status code.
   *
   * @param errorMessage the detail message explaining the reason for the exception.
   * @param httpStatus the HTTP status code associated with the error.
   */
  public ApiResponseException(String errorMessage, HttpStatusCode httpStatus) {
    super(errorMessage);
    this.errorMessage = errorMessage;
    this.httpStatus = httpStatus;
  }

  /**
   * Formats the specified message pattern with the provided arguments.
   *
   * @param messagePattern the message pattern to be formatted.
   * @param args the arguments to format the message pattern.
   * @return the formatted message.
   */
  private static String format(String messagePattern, Object... args) {
    return args != null && args.length != 0 ? String.format(messagePattern, args) : messagePattern;
  }

  /**
   * Returns the error message associated with this exception.
   *
   * @return the error message.
   */
  public String getErrorMessage() {
    return this.errorMessage;
  }

  /**
   * Returns the HTTP status code associated with this exception.
   *
   * @return the HTTP status code.
   */
  public HttpStatusCode getHttpStatus() {
    return this.httpStatus;
  }
}

