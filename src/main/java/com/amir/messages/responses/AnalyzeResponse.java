package com.amir.messages.responses;

import com.amir.messages.VideoResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeResponse extends BaseResponse {

  private boolean all_videos_freeze_frame_synced;
  private List<VideoResult> videos;
}
