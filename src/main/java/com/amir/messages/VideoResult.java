package com.amir.messages;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoResult {

  private double longest_valid_period;
  private double valid_video_percentage;
  private List<List<String>> valid_periods;
}
