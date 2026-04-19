package com.karthik.lld_basics.adapter;

import java.util.*;


// The idea is that we will create an adapter class which will implement those methods that are defined in the interface and Each class in the adapter class will essentially eventually call the actual class method.The only thing we need to now figure out is where to initialize the object, and that we can simply do by using a constructor of the Adaptive Pack class.
public class Adapter {
  public void run() {
    CheckoutService checkoutService = new CheckoutService(new PayUGateway());
    checkoutService.checkout("12", 1780);
    // CheckoutService checkoutService2 = new CheckoutService(new RazorpayAPI());
    // checkoutService2.checkout("12", 1780);
    // throws: incompatible types: com.karthik.lld_basics.adapter.RazorpayAPI cannot be converted to com.karthik.lld_basics.adapter.PaymentGateway
    CheckoutService checkoutService2 = new CheckoutService(new RazorpayAPIAdapter());
    checkoutService2.checkout("15", 15098);
  }

}


// Target Interface:
// Standard interface expected by the CheckoutService
interface PaymentGateway {
    void pay(String orderId, double amount);
}

// Concrete implementation of PaymentGateway for PayU
class PayUGateway implements PaymentGateway {
    @Override
    public void pay(String orderId, double amount) {
        System.out.println("Paid Rs. " + amount + " using PayU for order: " + orderId);
    }
}

// Adaptee:
// An existing class with an incompatible interface.
// This class is not compatible with the PaymentGateway interface.
// This class cannot be used using dependency injection.
class RazorpayAPI {
    public void makePayment(String invoiceId, double amountInRupees) {
        System.out.println("Paid Rs. " + amountInRupees + " using Razorpay for invoice: " + invoiceId);
    }
}

// Adapter:
// Create a adapter (like charging adapter) for razorpay
class RazorpayAPIAdapter implements PaymentGateway {

  private RazorpayAPI razorpayAPI;

  RazorpayAPIAdapter(){
    this.razorpayAPI = new RazorpayAPI();
  }

  @Override
  public void pay(String orderId, double amount){
    this.razorpayAPI.makePayment(orderId, amount);
  }
}

// Client Class:
// Uses PaymentGateway interface to process payments
class CheckoutService {
    private PaymentGateway paymentGateway;

    // Constructor injection for dependency inversion
    public CheckoutService(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    // Business logic to perform checkout
    public void checkout(String orderId, double amount) {
        paymentGateway.pay(orderId, amount);
    }
}
