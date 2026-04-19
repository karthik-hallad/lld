package com.karthik.lld_basics.builder;

import java.util.Arrays;
import java.util.List;
public class Builder {
  public void run(){
    BurgerMeal burgerMeal = new BurgerMeal.BurgerBuilder("wheat", "veg")
    .withSides("fries")
    .withToppings(Arrays.asList("lettuce", "onion", "jalapeno"))
    .withCheese(true)
    .build();

    BurgerMeal burgerMeal2 = new BurgerMeal.BurgerBuilder("roll", "chicken")
    .withSides("fries")
    .withCheese(false)
    .build();

    System.out.println(burgerMeal.toString());
    System.out.println(burgerMeal2.toString());
  }
}

// Represents a customizable Burger Meal
class BurgerMeal {
    // Mandatory components
    private String bun;
    private String patty;

    // Optional components
    private String sides;
    private List<String> toppings;
    private boolean cheese;

    // Constructor trying to handle all combinations. This is not a good way to do it.
    private BurgerMeal(String bun, String patty, String sides, List<String> toppings, boolean cheese) {
        this.bun = bun;
        this.patty = patty;
        this.sides = sides;
        this.toppings = toppings;
        this.cheese = cheese;
    }

    public String toString(){
      return "BurgerMeal{" +
      "bun=" + bun +
      ", patty=" + patty +
      ", sides=" + sides +
      ", toppings=" + toppings +
      ", cheese=" + cheese +
      '}';
    }

    // You can have it ourside or inside the class
    // but it should be static.
    static class BurgerBuilder {
      // Mandatory components
      private final String bun;
      private final String patty;

      // Optional components
      private String sides;
      private List<String> toppings;
      private boolean cheese;

      BurgerBuilder(String bun, String patty){
        this.bun = bun;
        this.patty = patty;
      }

      BurgerBuilder withSides(String sides){
        this.sides = sides;
        // what happens if i dont return here, i still am
        // using am assigning sides to this right
        return this;
      }

      BurgerBuilder withToppings(List<String> toppings){
        this.toppings = toppings;
        return this;
      }

      BurgerBuilder withCheese(boolean cheese){
        this.cheese = cheese;
        return this;
      }

      BurgerMeal build(){
        return new BurgerMeal(bun, patty, sides, toppings, cheese);
      }
    }
}
