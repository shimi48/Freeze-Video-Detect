package com.amir.messages.responses;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class Error {

  Short code;
  String message;

  public Error(short code, String message) {
    this.code = code;
    this.message = message;
  }

  public Error(String message) {
    this.message = message;
  }
}
