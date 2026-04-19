package com.karthik.lld_basics.strategy;

public class Strategy {
  public void run(){
   RideMatchingService rideMatchingService = new RideMatchingService(new NearestDriverStrategy());
   rideMatchingService.match("Terminal 1");
   rideMatchingService.setStrategy(new SurgeDriverStrategy());
   rideMatchingService.match("Terminal 1");
   rideMatchingService.setStrategy(new AirportQueueStrategy());
   rideMatchingService.match("Terminal 1");
  }
}

interface MatchingStrategy{
  // this defines a set of strategies.
  // all algo should implement suvh strategies
  void match(String riderLocation);
}

class NearestDriverStrategy implements MatchingStrategy{
  // any class variables use here.
  @Override
  public void match(String riderLocation){
    System.out.println("Nearest Driver Strategy");
  }
}

class SurgeDriverStrategy implements MatchingStrategy{
  @Override
  public void match(String riderLocation){
    System.out.println("Surge Driver Strategy");
  }
}

class AirportQueueStrategy implements MatchingStrategy{
  @Override
  public void match(String riderLocation){
    System.out.println("Airport Queue Strategy");
  }
}

class RideMatchingService{
  MatchingStrategy matchingStrategy;
  RideMatchingService(MatchingStrategy matchingStrategy){
    this.matchingStrategy = matchingStrategy;
  }
  public void setStrategy(MatchingStrategy matchingStrategy){
    this.matchingStrategy = matchingStrategy;
  }
  public void match(String riderLocation){
    matchingStrategy.match(riderLocation);
  }
}