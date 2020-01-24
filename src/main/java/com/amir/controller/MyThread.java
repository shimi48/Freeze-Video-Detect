package com.amir.controller;

import com.amir.messages.VideoResult;
import com.amir.messages.requests.ParseRequest;
import com.amir.utils.Prefrences;
import com.amir.utils.VideoMap;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MyThread
 *
 * MyThread is Runnable responsible for executing the ffmpeg command and parse the output
 */
public class MyThread implements Runnable {

  private ParseRequest request;

  public MyThread(ParseRequest request) {
    this.request = request;
  }

  public void run() {
    try {
      Thread.sleep(5000);
    } catch (Exception e) {
    }
    System.out.println("in MyThread !!!!!!!!!!!!!!!!!");
    System.out.println("id = " + this.request.getAssetId() + " url = " + this.request.getFileURL());

    String ffmpegPath = "";
    if(System.getProperty("os.name").toLowerCase().contains("windows"))
      ffmpegPath = Prefrences.getInstance().getProperty(Prefrences.windowsFfmpegPath, "");
    else
      ffmpegPath = Prefrences.getInstance().getProperty(Prefrences.linuxFfmpegPathProp, "");

    System.out.println("ffmpegPath = " + ffmpegPath + " os = " + System.getProperty("os.name"));

    String[] cmd = {ffmpegPath,"-i",this.request.getFileURL(),"-vf","freezedetect=n=0.003:d=1","-map","0:v:0","-f","null","-"};
    String ffmpegOutputStr = "";

    try {
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      Process process = pb.start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      String line = "";
      while ((line = reader.readLine()) != null) {
        ffmpegOutputStr = ffmpegOutputStr + line;
      }

      process.waitFor();

      System.out.println(ffmpegOutputStr);
    } catch (Exception ex) {
      VideoMap.getInstance().setFailed(this.request.getAssetId());
      System.out.println(ex.getMessage());
      return;
    }

    VideoResult videoResult = new VideoResult(0.0, 0.0, new ArrayList<List<String>>());
    Pattern freezePattern = Pattern.compile("lavfi.freezedetect.freeze_(start|end):\\s*(\\d{1,2}([.\\-.])\\d{1,6})");
    Pattern timePattern = Pattern.compile("time=(\\d{1,2}([.\\-:])\\d{1,2}([.\\-:])\\d{1,2})");

    Matcher freezeMatcher = freezePattern.matcher(ffmpegOutputStr);
    Matcher timeMatcher = timePattern.matcher(ffmpegOutputStr);

    long timeLong = -1;
    while(timeMatcher.find()) {
      timeLong = Duration.between(LocalTime.MIN, LocalTime.parse( timeMatcher.group(1) )).toMillis();
      System.out.println("timeString = " + timeMatcher.group(1) + "timeLong = " + timeLong);
    }

    if(timeLong <= 0)
      return;

    long freezeStart = -1;
    long totalPlayTime = 0;
    long lastFreezeEnd = 0;
    long longestPlayTime = -1;
    String freezeStartStr = "";
    String freezeEndtStr = "0.00";
    long currentPlayTime = 0;
    List<List<String>> freezeList = new ArrayList<List<String>>();
    List<Long> startPlayIntervals = new ArrayList<Long>();
    while (freezeMatcher.find()) {
      String s = freezeMatcher.group(2);
      System.out.println(freezeMatcher.group(2)); //print out the timestamp
      double freezeDouble = Double.parseDouble(s) * 1000;
      long freezeMsec = Math.round(freezeDouble);
      if(freezeStart == -1){
        freezeStartStr = freezeMatcher.group(2);
        freezeList.add(new ArrayList<String>(Arrays.asList(freezeEndtStr, freezeStartStr)));
        freezeStart = freezeMsec;
        currentPlayTime = freezeMsec - lastFreezeEnd;
        totalPlayTime += currentPlayTime;
        if(currentPlayTime > longestPlayTime)
          longestPlayTime = currentPlayTime;

        System.out.println("start time in millis = " + freezeMsec + "longestPlayTime = " + longestPlayTime);
      } else {
        System.out.println("end time in millis = " + freezeMsec);
        freezeEndtStr = freezeMatcher.group(2);
        startPlayIntervals.add(freezeMsec);
        lastFreezeEnd = freezeMsec;
        freezeStart = -1;
      }
    }

    if(freezeStart == -1){
      double streamEnd = timeLong/1000.0;
      freezeStartStr = Double.toString(streamEnd);
      freezeList.add(new ArrayList<String>(Arrays.asList(freezeEndtStr, freezeStartStr)));
      currentPlayTime = timeLong - lastFreezeEnd;
      totalPlayTime += currentPlayTime;
      if(currentPlayTime > longestPlayTime)
        longestPlayTime = currentPlayTime;
    }
    else {

    }

    double validVideoPercentage = (double)totalPlayTime / timeLong;
    double longestPlayTimeDouble = longestPlayTime/1000.0;
    videoResult = new VideoResult(longestPlayTimeDouble, validVideoPercentage, freezeList);
    VideoMap.getInstance().setData(this.request.getAssetId(), videoResult, startPlayIntervals);
    Gson gson = new Gson();
    System.out.println("videoResult = " + gson.toJson(videoResult));
    System.out.println("assetId = " + this.request.getAssetId() + "videoResult = " + gson.toJson(VideoMap.getInstance().getData(this.request.getAssetId())));
  }
}
