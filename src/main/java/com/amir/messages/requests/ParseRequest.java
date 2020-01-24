package com.amir.messages.requests;

import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ParseRequest{

  @NotBlank(message = "asset ID not provided")
  private String assetId;
  @NotBlank(message = "file URL is not provided")
  private String fileURL;

}
