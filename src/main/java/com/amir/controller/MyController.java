package com.amir.controller;

import com.amir.messages.VideoResult;
import com.amir.messages.requests.ParseRequest;
import com.amir.messages.responses.AnalyzeResponse;
import com.amir.messages.responses.ParseResponse;
import com.amir.messages.responses.StatusResponse;
import com.amir.utils.VideoMap;
import com.google.gson.Gson;
import org.apache.commons.lang3.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * MyController
 *
 * MyController is the Spring MVC RestController handling the rest requests
 */
@RestController
@Validated
public class MyController {

  private Gson gson;

  @Autowired
  private final TaskExecutor taskExecutor;

  @Autowired
  public MyController(final TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
    this.gson = new Gson();
  }

  @PostMapping(path = "/parse", produces = MediaType.APPLICATION_JSON_VALUE)
  public ParseResponse parse(@Valid @RequestBody ParseRequest request) {

    System.out.println("[Request /parse]:" + request);

    VideoMap.getInstance().add(request.getAssetId());

    MyThread myThread = new MyThread(request);
    taskExecutor.execute(myThread);

    ParseResponse response = new ParseResponse(request.getAssetId(), "pending");
    System.out.println("[Response /parse]:" + response);
    return response;
  }

  @GetMapping(path = "/status/{assetId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public StatusResponse status(@Valid @PathVariable String assetId) {

    System.out.println("[Request /status]:" + assetId);

    String status = VideoMap.getInstance().getStatus(assetId);
    StatusResponse response = new StatusResponse(status);
    System.out.println("[Response /status]:" + response);
    return response;
  }

  @GetMapping(path = "/analyze/{assetIds}", produces = MediaType.APPLICATION_JSON_VALUE)
  public AnalyzeResponse analyze(@Valid @PathVariable String[] assetIds) {

    System.out.println("[Request /analyze]:" + assetIds.length);
    System.out.println("[Request /analyze]:" + assetIds[0]);

    int numOfPlaySegments = 0;
    boolean synced = true;
    List<Range<Long>> rangeList = new ArrayList<Range<Long>>();
    List<VideoResult> videoList = new ArrayList<VideoResult>();
    for (int i = 0 ; i < assetIds.length ; i++){
      VideoMap.VideoMapElement video = VideoMap.getInstance().getData(assetIds[i]);
      videoList.add(video.getVideo());

      if(!synced)
        continue;

      if(i == 0){
        numOfPlaySegments = video.getStartPlayIntervals().size();
        for(int j = 0 ; j < numOfPlaySegments ; j++){
          Range<Long> range = Range.between(video.getStartPlayIntervals().get(j) - 500, video.getStartPlayIntervals().get(j) + 500);
          rangeList.add(range);
        }
      }else{
        int myNumOfPlaySegments = video.getStartPlayIntervals().size();
        if(myNumOfPlaySegments != numOfPlaySegments){
          synced = false;
          System.out.println("[/analyze] sync not the same number of segments: first = " + numOfPlaySegments + "mine = " + myNumOfPlaySegments);
        }else{
          for(int k = 0 ; k < numOfPlaySegments ; k++){
            if(!rangeList.get(k).contains(video.getStartPlayIntervals().get(k))){
              synced = false;
              System.out.println("[/analyze] sync not in range: range = " + rangeList.get(k) + "value = " + video.getStartPlayIntervals().get(k));
              break;
            }
          }
        }
      }
    }
    AnalyzeResponse response = new AnalyzeResponse(synced, videoList);
    System.out.println("[Response /analyze]:" + response);
    return response;
  }

  @DeleteMapping(path = "/delete/{assetId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ParseResponse analyze(@Valid @PathVariable String assetId) {

    System.out.println("[Request /delete]:" + assetId);

    VideoMap.getInstance().remove(assetId);
    ParseResponse response = new ParseResponse(assetId, "deleted");
    System.out.println("[Response /delete]:" + response);
    return response;
  }

  @RequestMapping(path = "/")
  public String welcome() {//Welcome page, non-rest
    return "Welcome to RestTemplate Example.";
  }

}
