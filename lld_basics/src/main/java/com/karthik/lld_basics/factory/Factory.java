package com.karthik.lld_basics.factory;

public class Factory {
  public void run(){
    LogisticsService ls = new LogisticsService();
    ls.send("air");
    ls.send("road");
  }
}

interface Logistics {
  void send();
}

class Road implements Logistics {
  @Override
  public void send() {
    System.out.println("Sending by road");
  }
}

class Air implements Logistics {
  @Override
  public void send() {
    System.out.println("Sending by air");
  }
}

class LogisticsFactory {
  // static method since we do not care about intialzizting this.
  public static Logistics getLogistics(String mode){
    if(mode.equalsIgnoreCase("AIR")){
      return new Air();
    } else if(mode.equalsIgnoreCase("ROAD")){
      return new Road();
    }
    throw new IllegalArgumentException("Not supported");
  }
}

class LogisticsService {
  public void send(String mode){
    // // Noob way of doing this.
    // // LogisticService deals with both creation of object
    // // and also sending the item which is bad.
    // if(mode.equals("air")){
    //   Logistics logistics = new Air();
    //   logistics.send();
    // } else if(mode.equals("road")){
    //   Logistics logistics = new Road();
    //   logistics.send();
    // }

    // better way, just handle multi object creation to facotry
    Logistics logistics = LogisticsFactory.getLogistics(mode);
    logistics.send();

  }
}