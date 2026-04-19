package com.karthik.lld_basics.abstractpattern;

public class Abstract {
  public void run() {
    CheckoutService checkoutService = new CheckoutService(new IndiaFactory(),"stripe");
    checkoutService.checkout(1500.00);
    CheckoutService checkoutService2 = new CheckoutService(new USAFactory(),"paypal");
    checkoutService2.checkout(15002.00);
  }
}

// Interface representing any payment gateway
interface PaymentGateway {
  void processPayment(double amount);
}

// Concrete implementation: Razorpay
class RazorpayGateway implements PaymentGateway {
  public void processPayment(double amount) {
      System.out.println("Processing INR payment via Razorpay: " + amount);
  }
}

// Concrete implementation: PayU
class PayUGateway implements PaymentGateway {
  public void processPayment(double amount) {
      System.out.println("Processing INR payment via PayU: " + amount);
  }
}

// Interface representing invoice generation
interface Invoice {
  void generateInvoice();
}

// Concrete invoice implementation for India
class GSTInvoice implements Invoice {
  public void generateInvoice() {
      System.out.println("Generating GST Invoice for India.");
  }
}


// lets use indiaFActory
class IndiaFactory implements RegionFactory{
  public PaymentGateway createPaymentGateway(String gatewayType){
      PaymentGateway paymentGateway;
      if (gatewayType.equals("razorpay")) {
          paymentGateway = new RazorpayGateway();
      } else {
          paymentGateway = new PayUGateway();
      }
      return paymentGateway;
  }

  public Invoice createInvoice(){
    return new GSTInvoice();
  }
}

class PayPalGateway implements PaymentGateway{
  public void processPayment(double amount){
    System.out.println("Processing USD payment via PayPal: " + amount);
  }
}

class USInvoice implements Invoice{
  public void generateInvoice(){
    System.out.println("Generating US Invoice.");
  }
}

class USAFactory implements RegionFactory{
  public PaymentGateway createPaymentGateway(String gatewayType){
    PaymentGateway paymentGateway;
    if (gatewayType.equals("paypal")) {
      paymentGateway = new PayPalGateway();
    } else {
      paymentGateway = new PayUGateway();
    }
    return paymentGateway;
  }
  public Invoice createInvoice(){
    return new USInvoice();
  }
}


interface RegionFactory {
  PaymentGateway createPaymentGateway(String gatewayType);
  Invoice createInvoice();
}


// CheckoutService that directly handles object creation (bad practice)
class CheckoutService {
  // private String gatewayType;
  // // Constructor accepts a string to determine which gateway to use
  // public CheckoutService(String gatewayType) {
  //     this.gatewayType = gatewayType;
  // }

  // // Checkout process hardcodes logic for gateway and invoice creation
  // public void checkOut(double amount) {
      // This code is bad since we have object creation logic
      // witht that of checkout core logic. so create a factory.
      // PaymentGateway paymentGateway;

      // // Hardcoded decision logic
      // if (gatewayType.equals("razorpay")) {
      //     paymentGateway = new RazorpayGateway();
      // } else {
      //     paymentGateway = new PayUGateway();
      // }

      // // Process payment using selected gateway
      // paymentGateway.processPayment(amount);

      // // Always uses GSTInvoice, even though more types may exist later
      // Invoice invoice = new GSTInvoice();
      // invoice.generateInvoice();

      // Here we can see that Object creation logic and core logic
      // is seperated
      // Factory pattern
      // PaymentGateway paymentGateway = IndiaFactory.createPaymentGateway(gatewayType);
      // Invoice invoice = IndiaFactory.createInvoice();

      // paymentGateway.processPayment(amount);
      // invoice.generateInvoice();

      // Now, if we need to move to us always, we need to start adding
      // usa and india use case here.
      // violates SRP/ here we use abstract pattern - multiple factories
      // For this we implement REgionFactory.
  // }

  // for this we need to take in RegionFactory as a parameter
  // dependency injection
  // we are directly injecting type of factory itself and we can
  // now call methods from it.
  private String gatewayType;
  RegionFactory regionFactory;
  public CheckoutService(RegionFactory regionFactory, String gatewayType){
    this.regionFactory = regionFactory;
    this.gatewayType = gatewayType;
  }
  public void checkout(double amount){
    PaymentGateway paymentGateway = regionFactory.createPaymentGateway(gatewayType);
    Invoice invoice = regionFactory.createInvoice();
    paymentGateway.processPayment(amount);
    invoice.generateInvoice();
  }
}
