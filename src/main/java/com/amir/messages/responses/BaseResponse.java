package com.amir.messages.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseResponse {

  private Error error;

  public BaseResponse(Error error) {
    this.error = error;
  }
}
