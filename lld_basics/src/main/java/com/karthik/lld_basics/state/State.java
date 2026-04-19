package com.karthik.lld_basics.state;

import java.util.EnumMap;
import java.util.Map;

public class State {
  public void run() {
    // sm.next() and sm.cancel() are EVENTS — they represent
    // something that happened in the outside world.
    //   next()   = "user/system wants to advance the order"
    //   cancel() = "user/system wants to cancel the order"
    //
    // These do NOT contain business logic themselves. They just
    // tell the machine "this event occurred" and the machine
    // looks up the transition table + calls the right handler.
    OrderStateMachine sm = new OrderStateMachine();
    System.out.println("Current: " + sm.getCurrentState());

    sm.next();        // ORDER_PLACED -> PREPARING
    sm.next();        // PREPARING -> OUT_FOR_DELIVERY
    sm.cancel();      // Should fail — can't cancel once out
    sm.next();        // OUT_FOR_DELIVERY -> DELIVERED
    sm.cancel();      // Should fail — already delivered
  }
}

// =====================================================================
// IMPROVED VERSION — Transition-table-driven state machine
//
// Why this is better:
//   1. States don't know about each other. No state creates
//      another state. Transition rules live in ONE place.
//   2. StateMachine is not passed into states, so states can't
//      corrupt the machine. Only the machine mutates itself.
//   3. Changing the flow (add/remove/reorder states) means
//      editing the transition table, not every state class.
// =====================================================================

enum OrderState {
  ORDER_PLACED,
  PREPARING,
  OUT_FOR_DELIVERY,
  DELIVERED,
  CANCELLED
}

enum Event {
  NEXT,
  CANCEL
}

// Handlers define REACTIONS — what the system should DO when
// an event fires while in a specific state. This is where your
// actual business logic lives.
//
// Three lifecycle hooks per state:
//
//   onEnter()  — Runs when the machine ARRIVES at this state.
//                Real-world use: setup work for this state.
//                e.g. PREPARING.onEnter() -> notify kitchen,
//                     start prep timer, reserve ingredients.
//                e.g. OUT_FOR_DELIVERY.onEnter() -> start GPS
//                     tracker, send ETA push notification.
//                After a crash, if you restore state from DB,
//                you'd call onEnter() to re-init that state.
//
//   onNext()   — Runs when a NEXT event fires IN this state,
//                right BEFORE leaving. This is exit/cleanup.
//                e.g. PREPARING.onNext() -> print receipt,
//                     assign delivery driver, mark prep done.
//                e.g. ORDER_PLACED.onNext() -> charge payment,
//                     send confirmation email.
//
//   onCancel() — Runs when a CANCEL event fires IN this state.
//                If the transition is allowed: cleanup/refund.
//                If not allowed: just log/reject the attempt.
//                e.g. PREPARING.onCancel() -> refund payment,
//                     release reserved ingredients.
//                e.g. OUT_FOR_DELIVERY.onCancel() -> print
//                     "cannot cancel, already dispatched".
//
// KEY INSIGHT: handlers never decide WHICH state comes next.
// That's the transition table's job. Handlers only define
// what HAPPENS when you enter/leave/get-an-event.
interface OrderStateHandler {
  OrderState state();
  void onEnter();
  void onNext();
  void onCancel();
}

class OrderPlacedHandler implements OrderStateHandler {
  public OrderState state() { return OrderState.ORDER_PLACED; }

  // Real world: save order to DB, send order-confirmation email
  public void onEnter() {
    System.out.println("Order has been placed.");
  }

  // Real world: charge payment, lock inventory, send to kitchen
  public void onNext() {
    System.out.println("Moving order to preparation.");
  }

  // Real world: refund if pre-authorized, release inventory hold
  public void onCancel() {
    System.out.println("Order cancelled before preparation.");
  }
}

class PreparingHandler implements OrderStateHandler {
  public OrderState state() { return OrderState.PREPARING; }

  // Real world: notify kitchen, start prep timer
  public void onEnter() {
    System.out.println("Order is being prepared.");
  }

  // Real world: print receipt, assign delivery driver
  public void onNext() {
    System.out.println("Preparation done. Sending out.");
  }

  // Real world: refund payment, release reserved ingredients
  public void onCancel() {
    System.out.println("Order cancelled during preparation.");
  }
}

class OutForDeliveryHandler implements OrderStateHandler {
  public OrderState state() {
    return OrderState.OUT_FOR_DELIVERY;
  }

  // Real world: start GPS tracking, send ETA to customer
  public void onEnter() {
    System.out.println("Order is out for delivery.");
  }

  // Real world: confirm delivery with photo, stop GPS tracker
  public void onNext() {
    System.out.println("Order delivered successfully.");
  }

  // Not in transition table, so this is a rejection message.
  // Real world: log the attempt, show user "too late to cancel"
  public void onCancel() {
    System.out.println(
        "Cannot cancel — already out for delivery.");
  }
}

class DeliveredHandler implements OrderStateHandler {
  public OrderState state() { return OrderState.DELIVERED; }

  // Real world: send "your order arrived" notification,
  // trigger feedback/rating prompt, close the order record
  public void onEnter() {
    System.out.println("Order has been delivered.");
  }

  // Terminal state — no NEXT transition exists in the table.
  // This only runs as a rejection message.
  public void onNext() {
    System.out.println(
        "Order already delivered. No next state.");
  }

  // Terminal state — no CANCEL transition exists either.
  public void onCancel() {
    System.out.println("Cannot cancel a delivered order.");
  }
}

class CancelledHandler implements OrderStateHandler {
  public OrderState state() { return OrderState.CANCELLED; }

  // Real world: process refund, release inventory, send
  // cancellation confirmation email
  public void onEnter() {
    System.out.println("Order has been cancelled.");
  }

  // Terminal state — rejection messages only
  public void onNext() {
    System.out.println("Cancelled order cannot proceed.");
  }

  public void onCancel() {
    System.out.println("Order is already cancelled.");
  }
}

// The machine owns ALL transition logic in a single table.
// States are just dumb handlers — they never reference each other.
class OrderStateMachine {
  private final Map<OrderState, OrderStateHandler> handlers =
      new EnumMap<>(OrderState.class);

  // Transition table: (currentState, event) -> targetState
  // null means "transition not allowed"
  private final Map<OrderState, Map<Event, OrderState>> transitions
      = new EnumMap<>(OrderState.class);

  private OrderState currentState;

  OrderStateMachine() {
    registerHandlers();
    buildTransitionTable();
    this.currentState = OrderState.ORDER_PLACED;
  }

  private void registerHandlers() {
    register(new OrderPlacedHandler());
    register(new PreparingHandler());
    register(new OutForDeliveryHandler());
    register(new DeliveredHandler());
    register(new CancelledHandler());
  }

  private void register(OrderStateHandler handler) {
    handlers.put(handler.state(), handler);
  }

  private void buildTransitionTable() {
    // ORDER_PLACED --next--> PREPARING, --cancel--> CANCELLED
    addTransition(
        OrderState.ORDER_PLACED, Event.NEXT,
        OrderState.PREPARING);
    addTransition(
        OrderState.ORDER_PLACED, Event.CANCEL,
        OrderState.CANCELLED);

    // PREPARING --next--> OUT_FOR_DELIVERY, --cancel--> CANCELLED
    addTransition(
        OrderState.PREPARING, Event.NEXT,
        OrderState.OUT_FOR_DELIVERY);
    addTransition(
        OrderState.PREPARING, Event.CANCEL,
        OrderState.CANCELLED);

    // OUT_FOR_DELIVERY --next--> DELIVERED (cancel not allowed)
    addTransition(
        OrderState.OUT_FOR_DELIVERY, Event.NEXT,
        OrderState.DELIVERED);

    // DELIVERED and CANCELLED are terminal — no transitions
  }

  private void addTransition(
      OrderState from, Event event, OrderState to) {
    transitions
        .computeIfAbsent(from, k -> new EnumMap<>(Event.class))
        .put(event, to);
  }

  OrderState getCurrentState() { return currentState; }

  void next() { fire(Event.NEXT); }

  void cancel() { fire(Event.CANCEL); }

  // EXECUTION FLOW when sm.next() or sm.cancel() is called:
  //
  // 1. Look up current state's handler
  // 2. Check transition table: is (currentState, event) allowed?
  //
  // If NOT allowed (target == null):
  //   -> Call handler.onNext()/onCancel() which prints rejection
  //   -> State does NOT change. Done.
  //
  // If allowed:
  //   -> Call handler.onNext()/onCancel() = EXIT logic of old
  //      state (cleanup, finalize, persist)
  //   -> Update currentState to target
  //   -> Call newHandler.onEnter() = ENTRY logic of new state
  //      (setup, initialize, notify)
  //
  // Example: sm.next() while in PREPARING:
  //   1. PreparingHandler.onNext() -> "print receipt, assign driver"
  //   2. currentState = OUT_FOR_DELIVERY
  //   3. OutForDeliveryHandler.onEnter() -> "start GPS, send ETA"
  //
  // For crash recovery: you persist currentState (the enum) to DB.
  // On restart, load the enum, and call onEnter() on that state's
  // handler to re-initialize whatever that state needs.
  private void fire(Event event) {
    OrderStateHandler handler = handlers.get(currentState);

    Map<Event, OrderState> allowed = transitions.get(currentState);
    OrderState target =
        (allowed != null) ? allowed.get(event) : null;

    if (target == null) {
      if (event == Event.NEXT) handler.onNext();
      else handler.onCancel();
      return;
    }

    if (event == Event.NEXT) handler.onNext();
    else handler.onCancel();

    this.currentState = target;
    handlers.get(currentState).onEnter();
  }
}


// =====================================================================
// ORIGINAL VERSION (commented out) — problems annotated
//
// Problem 1: Every state hardcodes which state comes next.
//   OrderPlacedState.next() creates PreparingState directly.
//   If the flow changes, you edit every state class.
//
// Problem 2: The entire StateMachine is passed into each state.
//   Any state can call setState() with anything — no guard rails.
//   A typo like `stateMachine.setState(new CancelledState())`
//   inside next() silently breaks the whole flow.
//
// Problem 3: Adding a new state (e.g. PAYMENT_PENDING) requires
//   editing the state before it AND the state after it.
// =====================================================================

/*
class StateMachine {
  private StateInterface currentState;
  StateMachine(){
    this.currentState = new OrderPlacedState();
  }
  void setState(StateInterface state){
    this.currentState = state;
  }
  void nextState(){
    this.currentState.next(this);
  }
  void cancelOrder(){
    this.currentState.cancel(this);
  }
}

interface StateInterface {
  void next(StateMachine stateMachine);
  void cancel(StateMachine stateMachine);
  StateEnum currentState();
}

class OrderPlacedState implements StateInterface{
  public void next(StateMachine stateMachine){
    // Problem: this state decides what comes next
    stateMachine.setState(new PreparingState());
    System.out.println("Order is placed");
  }
  public void cancel(StateMachine stateMachine){
    stateMachine.setState(new CancelledState());
    System.out.println("Order is cancelled");
  }
  public StateEnum currentState(){
    return StateEnum.ORDER_PLACED;
  }
}

class PreparingState implements StateInterface{
  public void next(StateMachine stateMachine){
    stateMachine.setState(new OutForDeliveryState());
    System.out.println("Order is being prepared");
  }
  public void cancel(StateMachine stateMachine){
    stateMachine.setState(new CancelledState());
    System.out.println("Order is cancelled");
  }
  public StateEnum currentState(){
    return StateEnum.PREPARING;
  }
}

class OutForDeliveryState implements StateInterface{
  public void next(StateMachine stateMachine){
    stateMachine.setState(new DeliveredState());
    System.out.println("Order is out for delivery");
  }
  public void cancel(StateMachine stateMachine){
    System.out.println("Cannot cancel order out for delivery");
  }
  public StateEnum currentState(){
    return StateEnum.OUT_FOR_DELIVERY;
  }
}

class DeliveredState implements StateInterface{
  public void next(StateMachine stateMachine){
    System.out.println("Order is already delivered");
  }
  public void cancel(StateMachine stateMachine){
    System.out.println("Cannot cancel a delivered order");
  }
  public StateEnum currentState(){
    return StateEnum.DELIVERED;
  }
}

class CancelledState implements StateInterface{
  public void next(StateMachine stateMachine){
    System.out.println("Cancelled order cannot move to next state");
  }
  public void cancel(StateMachine stateMachine){
    System.out.println("Order is already cancelled");
  }
  public StateEnum currentState(){
    return StateEnum.CANCELLED;
  }
}
*/
