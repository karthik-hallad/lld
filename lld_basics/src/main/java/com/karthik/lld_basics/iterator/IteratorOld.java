package com.karthik.lld_basics.iterator;

import java.lang.String;
import java.util.*;

public class IteratorOld {
  public void run(){
    YouTubePlaylistOld playlist = new YouTubePlaylistOld();
    playlist.addVideo(new VideoOld("video1"));
    playlist.addVideo(new VideoOld("video2"));
    playlist.addVideo(new VideoOld("video3"));
    // problem is we still to know the internal structure of the playlist to create the iterator.
    YouTubePlaylistIteratorOld iterator = new YouTubePlaylistIteratorOld(playlist.getVideos());
    while(iterator.hasNext()){
      System.out.println(iterator.next().getTitle());
    }
  }
}

class VideoOld {
  String title;
  public VideoOld(String title){
    this.title = title;
  }
  public String getTitle(){
    return this.title;
  }
}

class YouTubePlaylistOld {
  public List<VideoOld> videos;
  public YouTubePlaylistOld(){
    this.videos = new ArrayList<>();
  }
  public void addVideo(VideoOld video){
    this.videos.add(video);
  }
  public List<VideoOld> getVideos(){
    return this.videos;
  }
}

interface PlaylistIteratorOld {
  // an iteartor should always itearte towards videos.
  // end all be all
  public VideoOld next();
  public boolean hasNext();
}

class YouTubePlaylistIteratorOld implements PlaylistIteratorOld {
  List<VideoOld> videos;
  int position;
  YouTubePlaylistIteratorOld(List<VideoOld> videos){
    this.videos = videos;
    this.position = 0;
  }
  @Override
  public VideoOld next(){
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