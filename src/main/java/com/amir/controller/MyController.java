package com.amir.controller;

import com.amir.messages.VideoResult;
import com.amir.messages.requests.AnalyzeRequest;
import com.amir.messages.requests.ParseRequest;
import com.amir.messages.responses.AnalyzeResponse;
import com.amir.messages.responses.ParseResponse;
import com.amir.messages.responses.StatusResponse;
import com.amir.utils.Prefrences;
import com.amir.utils.VideoMap;
import com.google.gson.Gson;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@RestController
//@RequestMapping("freeze")
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

    List<VideoResult> videoList = new ArrayList<VideoResult>();
    for (int i = 0 ; i < assetIds.length ; i++){
      VideoResult video = VideoMap.getInstance().getData(assetIds[i]);
      videoList.add(video);
    }
    AnalyzeResponse response = new AnalyzeResponse(true, videoList);
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
