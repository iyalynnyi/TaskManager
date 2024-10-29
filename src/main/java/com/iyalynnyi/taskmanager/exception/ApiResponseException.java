package com.iyalynnyi.taskmanager.exception;

import org.springframework.http.HttpStatusCode;

public class ApiResponseException extends RuntimeException {
  private final String errorMessage;
  private final HttpStatusCode httpStatus;

  public ApiResponseException(String messagePattern, HttpStatusCode httpStatus, Object... args) {
    super(format(messagePattern, args));
    this.errorMessage = this.getMessage();
    this.httpStatus = httpStatus;
  }

  public ApiResponseException(String errorMessage, HttpStatusCode httpStatus) {
    super(errorMessage);
    this.errorMessage = errorMessage;
    this.httpStatus = httpStatus;
  }

  private static String format(String messagePattern, Object... args) {
    return args != null && args.length != 0 ? String.format(messagePattern, args) : messagePattern;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public HttpStatusCode getHttpStatus() {
    return this.httpStatus;
  }
}
