package com.karthik.lld_basics.iterator;

import java.util.*;

enum IterationType {
  FORWARD,
  BACKWARD,
  RANDOM
}

public class RefinedIterator {
  public void run(){
    YouTubePlaylist playlist = new YouTubePlaylist();
    playlist.addVideo(new Video("video1"));
    playlist.addVideo(new Video("video2"));
    playlist.addVideo(new Video("video3"));
    // problem is we still to know the internal structure of the playlist to create the iterator.
    PlaylistIterator iterator = playlist.createIterator(IterationType.FORWARD);
    while(iterator.hasNext()){
      System.out.println(iterator.next().getTitle());
    }
  }
}

class Video {
  String title;
  public Video(String title){
    this.title = title;
  }
  public String getTitle(){
    return this.title;
  }
}

interface PlaylistIterator{
  boolean hasNext();
  Video next();
}

class YouTubePlaylist {
  public List<Video> videos;
  public YouTubePlaylist(){
    this.videos = new ArrayList<>();
  }
  public void addVideo(Video video){
    this.videos.add(video);
  }
  public List<Video> getVideos(){
    return this.videos;
  }
  public PlaylistIterator createIterator(IterationType iterationType){
    switch(iterationType){
      case FORWARD:
        return new YouTubePlaylistIterator(this.videos);
      default:
        return new YouTubePlaylistIterator(this.videos);
    }
  }
}

class YouTubePlaylistIterator implements PlaylistIterator{
  List<Video> videos;
  int position;
  YouTubePlaylistIterator(List<Video> videos){
    this.videos = videos;
    this.position = 0;
  }
  @Override
  public Video next(){
    if (this.hasNext()){
      return this.videos.get(this.position++);
    }
    return null;
  }
  @Override
  public boolean hasNext(){
    return this.position < this.videos.size();
  }
}