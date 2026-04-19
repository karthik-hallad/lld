package com.karthik.lld_basics.bridge;

public class Bridge {
  public void run(){
    VideoPlayer webPlayer = new WebPlayer(new HDQuality());
    webPlayer.play("video1.mp4");
    VideoPlayer mobilePlayer = new MobilePlayer(new UltraHDQuality());
    mobilePlayer.play("video2.mp4");
  }
}

interface VideoQuality {
  public void load(String title);
}

class SDQuality implements VideoQuality {
  @Override
  public void load(String title) {
    System.out.println("Loading " + title + " in SD Quality");
  }
}

class HDQuality implements VideoQuality {
  @Override
  public void load(String title) {
    System.out.println("Loading " + title + " in HD Quality");
  }
}

class UltraHDQuality implements VideoQuality {
  @Override
  public void load(String title) {
    System.out.println("Loading " + title + " in Ultra HD Quality");
  }
}

abstract class VideoPlayer {
  protected VideoQuality videoQuality;
  // abstract classes can have const but cannot
  // be invoked
  VideoPlayer(VideoQuality videoQuality){
    this.videoQuality = videoQuality;
  }

  abstract public void play(String title);
}

class WebPlayer extends VideoPlayer {
  WebPlayer(VideoQuality videoQuality){
    super(videoQuality);
  }
  @Override
  public void play(String title) {
    System.out.println("Playing " + title + " in Web Player");
    videoQuality.load(title);
  }
}

class MobilePlayer extends VideoPlayer {
  MobilePlayer(VideoQuality videoQuality){
    super(videoQuality);
  }
  @Override
  public void play(String title) {
    System.out.println("Playing " + title + " in Mobile Player");
    videoQuality.load(title);
  }
}