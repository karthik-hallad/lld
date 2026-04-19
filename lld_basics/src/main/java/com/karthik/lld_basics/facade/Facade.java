package com.karthik.lld_basics.facade;

import java.lang.String;

public class Facade {
  public void run(){
    MovieBookingFacade movieBookingFacade = new MovieBookingFacade();
    movieBookingFacade.bookMovieTicket("user123", "movie456", "A10", "user@example.com", 500);
  }
}

class MovieBookingFacade {
  PaymentService paymentService;
  LoyaltyPointsService loyaltyPointsService;
  NotificationService notificationService;
  SeatReservationService seatReservationService;
  TicketService ticketService;
  MovieBookingFacade(){
    this.paymentService = new PaymentService();
    this.loyaltyPointsService = new LoyaltyPointsService();
    this.notificationService = new NotificationService();
    this.seatReservationService = new SeatReservationService();
    this.ticketService = new TicketService();
  }

  String bookMovieTicket(String accountId, String movieId, String seatNumber, String userEmail, double amount){
    paymentService.makePayment(accountId, amount);
    seatReservationService.reserveSeat(movieId, seatNumber);
    ticketService.generateTicket(movieId, seatNumber);
    loyaltyPointsService.addPoints(accountId, 50);
    notificationService.sendBookingConfirmation(userEmail);
    return "Movie ticket booked successfully";
  }
}


// Service class responsible for handling payments
class PaymentService {
  public void makePayment(String accountId, double amount) {
      System.out.println("Payment of ₹" + amount + " successful for account " + accountId);
  }
}

// Service class responsible for reserving seats
class SeatReservationService {
  public void reserveSeat(String movieId, String seatNumber) {
      System.out.println("Seat " + seatNumber + " reserved for movie " + movieId);
  }
}

// Service class responsible for sending notifications
class NotificationService {
  public void sendBookingConfirmation(String userEmail) {
      System.out.println("Booking confirmation sent to " + userEmail);
  }
}

// Service class for managing loyalty/reward points
class LoyaltyPointsService {
  public void addPoints(String accountId, int points) {
      System.out.println(points + " loyalty points added to account " + accountId);
  }
}

// Service class for generating movie tickets
class TicketService {
  public void generateTicket(String movieId, String seatNumber) {
      System.out.println("Ticket generated for movie " + movieId + ", Seat: " + seatNumber);
  }
}
