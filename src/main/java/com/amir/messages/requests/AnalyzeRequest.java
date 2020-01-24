package com.amir.messages.requests;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AnalyzeRequest{

  @NotEmpty(message = "asset IDs not provided")
  private List<String> assetIds;

}
