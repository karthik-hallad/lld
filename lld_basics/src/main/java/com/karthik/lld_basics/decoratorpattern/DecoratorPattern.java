package com.karthik.lld_basics.decoratorpattern;

public class DecoratorPattern {
  public void run(){
    // cofeewithmilkand3shots
    Coffee coffee = new WithCream(new WithExtraShots(new SimpleCoffee(), 3));
    // cofeewithhazelnetandmilk
    Coffee coffee2 = new WithHazelnet(new WithCream(new SimpleCoffee()));
    // cofeewithall
    Coffee coffee3 = new WithHazelnet(new WithCream(new WithSugar(new SimpleCoffee())));
    System.out.println(coffee.getCost());
    System.out.println(coffee.getDescription());
    System.out.println(coffee2.getCost());
    System.out.println(coffee2.getDescription());
    System.out.println(coffee3.getCost());
    System.out.println(coffee3.getDescription());
  }
}

interface Coffee{
  int getCost();
  String getDescription();
}

/// ---- concrete decorators ---- ///////

class SimpleCoffee implements Coffee{
  private int cost;
  SimpleCoffee(){
    this.cost = 10;
  }
  public int getCost(){
    return this.cost;
  }
  public String getDescription(){
    return "Simple Coffee";
  }
}

// everthing after this is a decorator and the methods add
// runtime behavior to the coffee.

abstract class CoffeeDecorator implements Coffee{
  protected Coffee coffee;
  CoffeeDecorator(Coffee coffee){
    this.coffee = coffee;
  }
}

// We are not extending simple coffee since we want to depend on abstractions, not on actual classes.
class WithCream extends CoffeeDecorator{
  WithCream(Coffee coffee){
    super(coffee);
  }
  public int getCost(){
    return this.coffee.getCost() + 10;
  }
  public String getDescription(){
    return this.coffee.getDescription() + " with cream";
  }
}

class WithHazelnet extends CoffeeDecorator{
  WithHazelnet(Coffee coffee){
    super(coffee);
  }

  public int getCost(){
    return this.coffee.getCost() + 15;
  }

  public String getDescription(){
    return this.coffee.getDescription() + " with hazelnet";
  }
}

class WithExtraShots extends CoffeeDecorator{
  private int shots;
  WithExtraShots(Coffee coffee, int shots){
    super(coffee);
    this.shots = shots;
  }
  public int getCost(){
    return this.coffee.getCost() + this.shots * 10;
  }
  public String getDescription(){
    return this.coffee.getDescription() + " with " + this.shots + " extra shots";
  }
}

class WithSugar extends CoffeeDecorator{
  WithSugar(Coffee coffee){
    super(coffee);
  }
  public int getCost(){
    return this.coffee.getCost() + 5;
  }
  public String getDescription(){
    return this.coffee.getDescription() + " with sugar";
  }
}