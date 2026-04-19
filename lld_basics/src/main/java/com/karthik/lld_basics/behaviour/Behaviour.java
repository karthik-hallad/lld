package com.karthik.lld_basics.behaviour;

import java.util.*;

public class Behaviour {
  public void run(){
    YoutubeChannel channel = new YoutubeChannel();
    EmailSubscriber emailSubscriber = new EmailSubscriber();
    MobileSubscriber mobileSubscriber = new MobileSubscriber();
    channel.addSubscriber(emailSubscriber);
    channel.addSubscriber(mobileSubscriber);
    channel.updateSubscribers("video1");
    channel.removeSubscriber(emailSubscriber);
    channel.updateSubscribers("video2");
  }
}

// just create 2 interfaces which should be implemented.
interface Subscriber {
  // Think like when sending notififcation
  // as per srp, subscriber should handle that
  // because subscribber would be handling logic
  // of communication with subcribers
  void update(String videoTitle);
}
interface Channel {
  void addSubscriber(Subscriber subscriber);
  void removeSubscriber(Subscriber subscriber);
  void updateSubscribers(String videoTitle);
}

class EmailSubscriber implements Subscriber{
  @Override
  public void update(String videoTitle){
    System.out.println("EmailSubscriber: " + videoTitle);
  }
}

class MobileSubscriber implements Subscriber{
  @Override
  public void update(String videoTitle){
    System.out.println("MobileSubscriber: " + videoTitle);
  }
}

class YoutubeChannel implements Channel{
  List<Subscriber> subscribers;
  YoutubeChannel(){
    this.subscribers = new ArrayList<>();
  }
  @Override
  public void addSubscriber(Subscriber subscriber){
    this.subscribers.add(subscriber);
  }
  @Override
  public void removeSubscriber(Subscriber subscriber){
    this.subscribers.remove(subscriber);
  }
  @Override
  public void updateSubscribers(String videoTitle){
    for(Subscriber subscriber : subscribers){
      subscriber.update(videoTitle);
    }
  }
}
