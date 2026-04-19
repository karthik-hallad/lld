package com.karthik.lld_basics.proxy;

import java.util.Map;
import java.util.HashMap;

public class Proxy {
  public void run(){
    VideoDownloader videoDownloader = new CachedVideoDownloader();
    videoDownloader.downloadVideo("video1.mp4");
    videoDownloader.downloadVideo("video2.mp4");
    videoDownloader.downloadVideo("video1.mp4");
    videoDownloader.downloadVideo("video3.mp4");
    videoDownloader.downloadVideo("video3.mp4");
  }
}

interface VideoDownloader {
  String downloadVideo(String videoURL);
}

class CachedVideoDownloader implements VideoDownloader {
  private RealVideoDownloader realVideoDownloader;
  private Map<String,String> cache;

  public CachedVideoDownloader(){
    this.realVideoDownloader = new RealVideoDownloader();
    this.cache = new HashMap<>();
  }

  @Override
  public String downloadVideo(String videoURL) {
    if(cache.containsKey(videoURL)){
      System.out.println("Returning from cache " + videoURL);
      return cache.get(videoURL);
    }
    String fetchedVideo = realVideoDownloader.downloadVideo(videoURL);
    cache.put(videoURL,fetchedVideo);
    return fetchedVideo;
  }
}

class RealVideoDownloader implements VideoDownloader {
  @Override
  public String downloadVideo(String videoURL) {
    System.out.println("Fetched Video " + videoURL);
    return "fetched";
  }
}
