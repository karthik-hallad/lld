package com.karthik.lld_basics.chainofresp;

public class ChainOfResp {
  public void run(){
    SupportHandler general = new GeneralSupport();
    SupportHandler billing = new BillingSupport();
    SupportHandler technical = new TechnicalSupport();
    SupportHandler delivery = new DeliverySupport();

    // Setting up the chain: general -> billing -> technical -> delivery
    general.setNextHandler(billing);
    billing.setNextHandler(technical);
    technical.setNextHandler(delivery);

    general.HandleRequest("general");
    general.HandleRequest("billing");
    general.HandleRequest("technical");
    general.HandleRequest("delivery");
    general.HandleRequest("unknown");
  }
}


abstract class SupportHandler {
  SupportHandler nextHandler;
  public abstract void HandleRequest(String requestType);
  void setNextHandler(SupportHandler supportHandler) {
    this.nextHandler = supportHandler;
  }
}

class GeneralSupport extends SupportHandler{
  public void HandleRequest(String requestType){
    if(requestType.equalsIgnoreCase("general")){
      System.out.println("GeneralSupport: Handling general query");
    } else if(nextHandler != null){
      System.out.println("GeneralSupport: Passing request to next handler");
      nextHandler.HandleRequest(requestType);
    } else {
      System.out.println("GeneralSupport: No handler found for request");
    }
  }
}

class BillingSupport extends SupportHandler{
  public void HandleRequest(String requestType){
    if(requestType.equalsIgnoreCase("billing")){
      System.out.println("BillingSupport: Handling billing query");
    } else if(nextHandler != null){
      System.out.println("BillingSupport: Passing request to next handler");
      nextHandler.HandleRequest(requestType);
    } else {
      System.out.println("BillingSupport: No handler found for request");
    }
  }
}

class TechnicalSupport extends SupportHandler{
  public void HandleRequest(String requestType){
    if(requestType.equalsIgnoreCase("technical")){
      System.out.println("TechnicalSupport: Handling technical query");
    } else if(nextHandler != null){
      System.out.println("TechnicalSupport: Passing request to next handler");
      nextHandler.HandleRequest(requestType);
    } else {
      System.out.println("TechnicalSupport: No handler found for request");
    }
  }
}

class DeliverySupport extends SupportHandler{
  public void HandleRequest(String requestType){
    if(requestType.equalsIgnoreCase("delivery")){
      System.out.println("DeliverySupport: Handling delivery query");
    } else if(nextHandler != null){
      System.out.println("DeliverySupport: Passing request to next handler");
      nextHandler.HandleRequest(requestType);
    } else {
      System.out.println("DeliverySupport: No handler found for request");
    }
  }
}