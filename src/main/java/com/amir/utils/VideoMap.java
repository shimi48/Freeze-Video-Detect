package com.amir.utils;

import com.amir.messages.VideoResult;
import lombok.*;

import java.util.concurrent.ConcurrentHashMap;

public class VideoMap {

  @ToString
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @EqualsAndHashCode
  public class VideoMapElement {
    private String status;
    private VideoResult video;
  }

  private static VideoMap instance;
  private ConcurrentHashMap<String, VideoMapElement> videoMap;

  private VideoMap() {
    this.videoMap = new ConcurrentHashMap<String, VideoMapElement>();
  }

  public static VideoMap getInstance() {
    if (instance == null)
      instance = new VideoMap();
    return instance;
  }

  public void add(String assetId) {
    VideoMapElement element = new VideoMapElement();
    element.setStatus("pending");
    this.videoMap.put(assetId, element);
  }

  public void setData(String assetId, VideoResult videoResult) {
    VideoMapElement element = new VideoMapElement("completed", videoResult);
    this.videoMap.replace(assetId, element);
  }

  public void setFailed(String assetId) {
    VideoMapElement element = new VideoMapElement();
    element.setStatus("failed");
    this.videoMap.replace(assetId, element);
  }

  public String getStatus(String assetId) {
    VideoMapElement element = new VideoMapElement();
    element.setStatus("not-found");
    return this.videoMap.getOrDefault(assetId, element).getStatus();
  }

  public VideoResult getData(String assetId) {
    return this.videoMap.getOrDefault(assetId, new VideoMapElement()).getVideo();
  }

  public void remove(String assetId) {
    this.videoMap.remove(assetId);
  }
}
