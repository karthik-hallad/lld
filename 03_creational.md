# Creational Design Patterns

## Quick Reference — When to Use & Pseudocode

### Singleton
**When:** You need exactly one shared instance globally
(config, logging, DB connection pool).

```
class Singleton {
    static instance;
    private Singleton() {}
    static getInstance() {
        if (instance == null) instance = new Singleton();
        return instance;
    }
}
```

### Factory
**When:** Client needs an object but shouldn't know/care
which concrete class is instantiated — decision is at runtime.

```
class Factory {
    static create(type) {
        if (type == "A") return new ConcreteA();
        if (type == "B") return new ConcreteB();
    }
}
obj = Factory.create("A");
obj.doWork();
```

### Builder
**When:** Object has many optional parameters, you want
step-by-step construction, and the result should be immutable.

```
obj = new Thing.Builder(requiredA, requiredB)
        .withOptionalX(x)
        .withOptionalY(y)
        .build();
```

### Abstract Factory
**When:** You need to create **families** of related objects
that must stay consistent (e.g., region-specific payment +
invoice), and the family can change at runtime.

```
factory = getFactory(region);   // returns IndiaFactory or USFactory
gateway = factory.createGateway();
invoice = factory.createInvoice();
gateway.pay(amount);
invoice.generate();
```

---

## Table of Contents

1. [Singleton Pattern](#singleton-pattern)
2. [Factory Pattern](#factory-pattern)
3. [Builder Pattern](#builder-pattern)
4. [Abstract Factory Pattern](#abstract-factory-pattern)

---

## Singleton Pattern

The **Singleton Pattern** ensures that a class has only one
instance and provides a global point of access to that instance.

> **Personal note:** Imagine you're building an application
> where you only want one shared object throughout the
> lifecycle of the program. This is where Singleton comes
> into play — it restricts object creation and guarantees
> that all parts of your application use the same object.
> Like logging, configuration handling, or managing a
> database connection. Think of AWS Configuration manager
> in our codebase.

**Why?**
- Exists once due to maintaining global state, resource
  constraints, logical reasoning (logging — we want the same
  instance to avoid contention).

### How It Works

1. **Private constructor** — prevents instantiation from
   outside the class.
2. **Static variable** — holds the single instance.
3. **Public static method** — provides global access to
   get the instance.

### Approaches

#### 1. Eager Loading (Early Initialization)

The instance is created as soon as the class is loaded,
regardless of whether it's ever used.

```java
class EagerSingleton {
    private static final EagerSingleton instance =
        new EagerSingleton();

    private EagerSingleton() {}

    public static EagerSingleton getInstance() {
        return instance;
    }
}
```

- **+** Thread-safe without any extra handling.
- **-** Wastes memory if the instance is never used.
- **+** Best if you are sure it will be used in the program.

#### 2. Lazy Loading (On-Demand Initialization)

The instance is created only when it's needed — the first
time `getInstance()` is called.

```java
class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
```

- **+** Only created when `getInstance()` is first called.
- **+** Saves memory if never used.
- **-** Not thread-safe by default. Requires synchronization
  in multi-threaded environments.

### Thread Safety for Lazy Loading

#### Synchronized Method

```java
public static synchronized Singleton getInstance() { }
```

The `synchronized` keyword ensures only one thread at a time
can execute `getInstance()`.
**Downside:** Every call is synchronized, even after the
instance is created — performance overhead.

#### Double-Checked Locking

> **Personal note:** VVIMP. Makes sense why double checking
> is important.

```java
private static volatile Singleton instance;

private Singleton() {}

public static Singleton getInstance() {
    if (instance == null) {
        synchronized (Singleton.class) {
            if (instance == null) {
                instance = new Singleton();
            }
        }
    }
    return instance;
}
```

> **Personal note:** Think of it this way: the upper check
> is to enter the synchronized state, and the lower check
> is the actual check. The upper check is necessary so that
> we don't do the synchronized check every single time.
> Multiple threads can still pass the outer null-check
> before one acquires the lock, so the inner check prevents
> a second instantiation.

The `volatile` keyword ensures changes made by one thread
are visible to others. Without `volatile`, one thread might
create the instance but other threads may not see the updated
value due to caching. `volatile` ensures the instance is
always read from main memory.

#### Bill Pugh Singleton (Best Practice for Lazy Loading)

```java
public class Singleton {
    private Singleton() {}

    private static class Holder {
        private static final Singleton INSTANCE =
            new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}
```

> **Personal note:** In Java, inner static classes are only
> loaded when we access them for the first time, so this
> uses Java's class loading mechanism to make it efficient.
> Since static classes are loaded only once during class
> usage, it works.

- **+** Instance not created until `getInstance()` is called.
- **+** `Holder` is not loaded until referenced, thanks to
  Java's class loading mechanism.
- **+** Thread safety, lazy loading, and high performance
  without synchronization overhead.
- **-** Best of both worlds: Lazy + Thread-safe.
- **-** Slightly less intuitive for beginners due to the
  nested static class.

### Summary

- **+** Provides a way to maintain a global resource.
- **+** Supports lazy loading with thread safety.
- **-** Used with parameters and confused with Factory.
- **-** Hard to write unit tests: difficult to isolate and
  mock since it holds global state.
- **-** Violates SRP: instance control/creation + core
  functionality.

---

## Factory Pattern

The **Factory Pattern** is a creational design pattern that
provides an interface for creating objects but allows
subclasses to alter the type of objects that will be created.

**When to use:**
- Client code needs to work with multiple types of objects.
- The decision of which class to instantiate must be made
  at runtime.
- The instantiation process is complex or needs to be
  controlled.

> **Personal note:** Think: when you want pizza, the factory
> (kitchen) handles the creation logic behind the scenes.
> Client does not care about the type of object nor cares
> about creating the object, it just wants to get the object.

### Components

| Component           | Role                                         |
|---------------------|----------------------------------------------|
| **Product**         | Interface/abstract class defining methods    |
| **Concrete Products** | Classes implementing the Product interface |
| **Factory**         | Returns different concrete products by input |

### Bad Design — Tightly Coupled Creation

```java
interface Logistics {
    void send();
}

class Road implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by road logic");
    }
}

class Air implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by air logic");
    }
}

class LogisticsService {
    public void send(String mode) {
        if (mode.equals("Air")) {
            Logistics logistics = new Air();
            logistics.send();
        } else if (mode.equals("Road")) {
            Logistics logistics = new Road();
            logistics.send();
        }
    }
}

class Main {
    public static void main(String[] args) {
        LogisticsService service = new LogisticsService();
        service.send("Air");
        service.send("Road");
    }
}
```

**Problems:**
- Object creation logic is tightly coupled with business
  logic.
- `Air` or `Road` is directly instantiated based on string
  comparison.
- **Tight Coupling:** `LogisticsService` depends directly
  on `Air` and `Road` classes.
- **Testing & Maintenance Nightmare:** Hard to test
  independently or mock logistics.

### Good Design — Factory Extracted

```java
interface Logistics {
    void send();
}

class Road implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by road logic");
    }
}

class Air implements Logistics {
    @Override
    public void send() {
        System.out.println("Sending by air logic");
    }
}

class LogisticsFactory {
    public static Logistics getLogistics(String mode) {
        if (mode.equalsIgnoreCase("Air")) {
            return new Air();
        } else if (mode.equalsIgnoreCase("Road")) {
            return new Road();
        }
        throw new IllegalArgumentException(
            "Unknown logistics mode: " + mode);
    }
}

class LogisticsService {
    public void send(String mode) {
        Logistics logistics =
            LogisticsFactory.getLogistics(mode);
        logistics.send();
    }
}

class Main {
    public static void main(String[] args) {
        LogisticsService service = new LogisticsService();
        service.send("Air");
        service.send("Road");
    }
}
```

> **Personal note:** Think of it as a high-level and
> low-level dependency. The high-level class
> (`LogisticsService`) should never depend on a low-level
> object like a concrete service. Using a switch statement
> essentially does that — depending on how the low-level
> class works. So what do we do? We call a factory, and
> that factory gives us the object. We just call `send()`
> on it. We don't care what arguments to pass, how to
> initialize it, or if an extension is required. Even if
> extension is required, the `LogisticsFactory` is the one
> getting extended, not `LogisticsService`.

### Summary

- **+** No code duplication: instantiation logic is
  centralized in the factory.
- **+** Easier testing & maintenance: each component can
  be tested independently. Can mock easily.
- **+** Enhances extensibility (OCP): introduce new classes
  (e.g., `Ship`) without modifying existing client code.
- **+** Centralizes object creation (SRP).
- **-** Increased complexity: additional layers might be
  overkill for very small programs.

[DIAGRAM](./images/factory_pattern.png)

---

## Builder Pattern

The **Builder Pattern** is a creational design pattern that
separates the construction of a complex object from its
representation. This allows you to create different types
and representations of an object using the same construction
process.

> **Personal note:** Think of ordering a pizza online. You
> select the crust type, size, toppings, cheese, and sauce
> — all step by step. The pizza shop then builds your pizza
> according to your selections. Different customers can use
> the same process to get entirely different pizzas.

### Problem — Constructor Explosion

```java
import java.util.*;

class BurgerMeal {
    private String bun;
    private String patty;
    private String sides;
    private List<String> toppings;
    private boolean cheese;

    public BurgerMeal(String bun, String patty,
            String sides, List<String> toppings,
            boolean cheese) {
        this.bun = bun;
        this.patty = patty;
        this.sides = sides;
        this.toppings = toppings;
        this.cheese = cheese;
    }
}

class Main {
    public static void main(String[] args) {
        BurgerMeal burgerMeal = new BurgerMeal(
            "wheat", "veg", null, null, false);
    }
}
```

- Risk of `NullPointerException` if we forget to null-check
  the optional components.
- Hard to read and maintain — need to remember parameter
  order.
- Even if we want to remove optionals, too many constructor
  overloads.

### Telescoping Constructor Anti-Pattern

```java
class BurgerMeal {
    public BurgerMeal(String bun, String patty) { ... }
    public BurgerMeal(String bun, String patty,
        boolean cheese) { ... }
    public BurgerMeal(String bun, String patty,
        boolean cheese, String side) { ... }
    public BurgerMeal(String bun, String patty,
        boolean cheese, String side,
        String drink) { ... }
}
```

- Error-prone due to parameter order.
- Inflexible and hard to maintain.

### Solution — Builder Pattern

```java
import java.util.*;

class BurgerMeal {
    private final String bunType;
    private final String patty;
    private final boolean hasCheese;
    private final List<String> toppings;
    private final String side;
    private final String drink;

    private BurgerMeal(BurgerBuilder builder) {
        this.bunType = builder.bunType;
        this.patty = builder.patty;
        this.hasCheese = builder.hasCheese;
        this.toppings = builder.toppings;
        this.side = builder.side;
        this.drink = builder.drink;
    }

    public static class BurgerBuilder {
        private final String bunType;
        private final String patty;

        private boolean hasCheese;
        private List<String> toppings;
        private String side;
        private String drink;

        public BurgerBuilder(String bunType, String patty) {
            this.bunType = bunType;
            this.patty = patty;
        }

        public BurgerBuilder withCheese(boolean hasCheese) {
            this.hasCheese = hasCheese;
            return this;
        }

        public BurgerBuilder withToppings(
                List<String> toppings) {
            this.toppings = toppings;
            return this;
        }

        public BurgerBuilder withSide(String side) {
            this.side = side;
            return this;
        }

        public BurgerBuilder withDrink(String drink) {
            this.drink = drink;
            return this;
        }

        public BurgerMeal build() {
            return new BurgerMeal(this);
        }
    }
}

class Main {
    public static void main(String[] args) {
        BurgerMeal plainBurger =
            new BurgerMeal.BurgerBuilder("wheat", "veg")
                .build();

        BurgerMeal burgerWithCheese =
            new BurgerMeal.BurgerBuilder("wheat", "veg")
                .withCheese(true)
                .build();

        List<String> toppings =
            Arrays.asList("lettuce", "onion", "jalapeno");
        BurgerMeal loadedBurger =
            new BurgerMeal.BurgerBuilder("multigrain",
                "chicken")
                .withCheese(true)
                .withToppings(toppings)
                .withSide("fries")
                .withDrink("coke")
                .build();
    }
}
```

> **Personal note:** Fields are `final` because we are not
> setting them in the builder class. Once initialized through
> the builder, we cannot change them. The private constructor
> is needed because at the end of `build()` we call it to
> set everything, and because we pass in all the parameters
> via the `with` methods, this works.

- **+** Private constructor forces object creation through
  the Builder only.
- **+** `build()` finalizes construction and returns the
  immutable `BurgerMeal` instance.

---

### Deep Dive Q&A

#### Q1: Why must the Builder be a STATIC inner class?

A non-static inner class is tied to an **instance** of the
outer class. That means to create a `BurgerBuilder`, you'd
first need a `BurgerMeal` object:

```java
BurgerMeal meal = ???;  // need this first
BurgerMeal.BurgerBuilder builder =
    meal.new BurgerBuilder("wheat", "veg");
```

But that's the whole problem — we're trying to create a
`BurgerMeal`. We can't create a `BurgerMeal` to create a
Builder to create a `BurgerMeal`. Chicken-and-egg problem.

A static inner class does NOT need an instance of the outer
class:

```java
new BurgerMeal.BurgerBuilder("wheat", "veg")
```

**Static inner class** gets two benefits:
1. No need for an outer class instance (because it's static)
2. Access to the outer class's private members (because it's
   inner/nested)

A non-static inner class gets #2 but not #1.
A separate external class gets #1 but not #2.
Only a static inner class gets **both**.

> **Personal note:** This is exactly why the builder can
> call `private BurgerMeal(BurgerBuilder builder)` — being
> a nested class gives it access to the outer class's
> private constructor.

#### Q2: Why return `this` from every `withX()` method?

> **Personal note:** You're right that Java passes object
> references by value, so `this.sides = sides` DOES modify
> the builder object. The caller's reference still points
> to the same modified object.

Technically, this works without returning `this`:

```java
BurgerBuilder builder =
    new BurgerBuilder("wheat", "veg");
builder.withCheese(true);
builder.withSide("fries");
BurgerMeal meal = builder.build();
```

But you **lose method chaining**. Without `return this`,
`.withCheese(true)` returns `void` — you can't call
`.withSide("fries")` on `void`.

`return this` is NOT about correctness — the mutation
happens either way. It's about enabling the **fluent
interface** pattern that makes Builder readable.

#### Q3: Why does `build()` use the constructor instead of setting fields directly?

In the notes, `build()` does:

```java
public BurgerMeal build() {
    return new BurgerMeal(this);
}
```

Why not set fields directly?

```java
public BurgerMeal build() {
    BurgerMeal meal = new BurgerMeal();
    meal.bun = this.bun;
    meal.patty = this.patty;
    return meal;
}
```

Two problems:
1. **Breaks immutability.** Fields can't be `final` if set
   after construction. The whole point is that the resulting
   object is immutable.
2. **Validation belongs in the constructor.** The constructor
   is the single place to enforce invariants. Bypassing it
   loses that checkpoint.

> **Personal note:** In the actual code implementation
> (`Builder.java`), `build()` passes individual fields
> instead of `this`. Both approaches are valid — passing
> `this` is cleaner with many fields; passing individual
> fields is more explicit. The key: `build()` MUST go
> through a constructor to preserve immutability and
> centralize validation.

### Summary

- **+** Complex objects with many optional parameters.
- **+** Immutability preferred once construction is complete
  (fields are `final`).
- **-** Overkill if your class has only 1–2 fields.
- **-** Not suitable if you don't want immutability.

### Real World Examples

**1. Lombok's `@Builder` Annotation (Java)**

```java
@Builder
public class User {
    private String name;
    private int age;
    private String address;
}

User user = User.builder()
    .name("John")
    .age(30)
    .address("NYC")
    .build();
```

**2. Amazon Cart Configuration**

> **Personal note:** Think about Amazon's shopping cart.
> When you add an item, you're not just storing an item ID.
> You're building a complex object with quantity, size/color,
> delivery option, gift wrap, save-for-later status,
> discounted price, etc. Each user customizes these
> differently. Internally, such cart items are likely created
> using a Builder Pattern for step-by-step configuration
> while ensuring data consistency and immutability.

---

## Abstract Factory Pattern

The **Abstract Factory Pattern** is a creational design
pattern that provides an interface for creating **families**
of related or dependent objects without specifying their
concrete classes.

In simpler terms: you use it when you have multiple
factories, each responsible for producing objects that are
meant to work together.

**When to use:**
- Multiple related objects must be created as a cohesive set
  (e.g., payment gateway + corresponding invoice generator).
- The type of objects depends on a specific context (country,
  theme, platform).
- Client code should remain independent of concrete product
  classes.
- Consistency across a family of related products must be
  maintained (e.g., US payment gateway paired with US-style
  invoice).

### Bad Design — Hardcoded Object Creation

```java
interface PaymentGateway {
    void processPayment(double amount);
}

class RazorpayGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println(
            "Processing INR payment via Razorpay: "
            + amount);
    }
}

class PayUGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println(
            "Processing INR payment via PayU: " + amount);
    }
}

interface Invoice {
    void generateInvoice();
}

class GSTInvoice implements Invoice {
    public void generateInvoice() {
        System.out.println(
            "Generating GST Invoice for India.");
    }
}

class CheckoutService {
    private String gatewayType;

    public CheckoutService(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    public void checkOut(double amount) {
        PaymentGateway paymentGateway;

        if (gatewayType.equals("razorpay")) {
            paymentGateway = new RazorpayGateway();
        } else {
            paymentGateway = new PayUGateway();
        }

        paymentGateway.processPayment(amount);

        Invoice invoice = new GSTInvoice();
        invoice.generateInvoice();
    }
}

class Main {
    public static void main(String[] args) {
        CheckoutService razorpayService =
            new CheckoutService("razorpay");
        razorpayService.checkOut(1500.00);
    }
}
```

### Improved Design — Abstract Factory

```java
// ========== Interfaces ==========
interface PaymentGateway {
    void processPayment(double amount);
}

interface Invoice {
    void generateInvoice();
}

// ========== India Implementations ==========
class RazorpayGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println(
            "Processing INR payment via Razorpay: "
            + amount);
    }
}

class PayUGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println(
            "Processing INR payment via PayU: " + amount);
    }
}

class GSTInvoice implements Invoice {
    public void generateInvoice() {
        System.out.println(
            "Generating GST Invoice for India.");
    }
}

// ========== US Implementations ==========
class PayPalGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println(
            "Processing USD payment via PayPal: "
            + amount);
    }
}

class StripeGateway implements PaymentGateway {
    public void processPayment(double amount) {
        System.out.println(
            "Processing USD payment via Stripe: "
            + amount);
    }
}

class USInvoice implements Invoice {
    public void generateInvoice() {
        System.out.println(
            "Generating Invoice as per US norms.");
    }
}

// ========== Abstract Factory ==========
interface RegionFactory {
    PaymentGateway createPaymentGateway(String gatewayType);
    Invoice createInvoice();
}

// ========== Concrete Factories ==========
class IndiaFactory implements RegionFactory {
    public PaymentGateway createPaymentGateway(
            String gatewayType) {
        if (gatewayType.equalsIgnoreCase("razorpay")) {
            return new RazorpayGateway();
        } else if (gatewayType.equalsIgnoreCase("payu")) {
            return new PayUGateway();
        }
        throw new IllegalArgumentException(
            "Unsupported gateway for India: "
            + gatewayType);
    }

    public Invoice createInvoice() {
        return new GSTInvoice();
    }
}

class USFactory implements RegionFactory {
    public PaymentGateway createPaymentGateway(
            String gatewayType) {
        if (gatewayType.equalsIgnoreCase("paypal")) {
            return new PayPalGateway();
        } else if (gatewayType.equalsIgnoreCase("stripe")) {
            return new StripeGateway();
        }
        throw new IllegalArgumentException(
            "Unsupported gateway for US: " + gatewayType);
    }

    public Invoice createInvoice() {
        return new USInvoice();
    }
}

// ========== Checkout Service ==========
class CheckoutService {
    private PaymentGateway paymentGateway;
    private Invoice invoice;
    private String gatewayType;

    public CheckoutService(RegionFactory factory,
            String gatewayType) {
        this.gatewayType = gatewayType;
        this.paymentGateway =
            factory.createPaymentGateway(gatewayType);
        this.invoice = factory.createInvoice();
    }

    public void completeOrder(double amount) {
        paymentGateway.processPayment(amount);
        invoice.generateInvoice();
    }
}

// ========== Main Method ==========
class Main {
    public static void main(String[] args) {
        CheckoutService indiaCheckout =
            new CheckoutService(
                new IndiaFactory(), "razorpay");
        indiaCheckout.completeOrder(1999.0);

        System.out.println("---");

        CheckoutService usCheckout =
            new CheckoutService(
                new USFactory(), "paypal");
        usCheckout.completeOrder(49.99);
    }
}
```

> **Personal note:** Think of it as an extension of the
> Factory Pattern. In Factory, you have one factory that
> produces whatever object you want based on a parameter.
> But the problem arises when the factory itself might
> change — e.g., we might have a US payment generator or
> an India payment generator. We'd have to keep doing a
> switch case for the factory itself, and nesting might
> keep increasing. Instead of passing parameters, we create
> a factory itself and pass it in. All types of factories
> implement the same interface, following the Dependency
> Inversion Principle where a high-level object depends on
> abstraction, not a low-level object.

> **Personal note:** Why not static classes? Because we're
> passing the factory as a dependency injection object.
> That's why we create our factory object, and this object
> has the methods we asked it to implement through the
> interface. If we have custom variables, we can pass them
> either while initializing the factory itself or in the
> checkout service — both accept parameters since we're
> not dealing with static variables.

### Summary

- **+** Adding a new gateway or invoice type does not
  require modifying `CheckoutService`.
- **+** Follows SOLID: especially OCP and DIP.
- **+** Encapsulates object creation.
- **+** Promotes consistency across products — forces all
  regions to implement the same interface.
- **+** Open for extension, closed for modification.
- **-** Increased complexity: multiple factories and
  interfaces.
- **-** Difficult to extend product families: adding a new
  product requires updating all factory implementations.

### Class Diagram

[DIAGRAM](./images/abstract_factory_pattern.png)

#### Diagram Analysis — Why Dependency Arrows Everywhere?

Looking at the diagram above, every arrow from
`CheckoutService` is a **dashed arrow** (dependency). Here's
why each relationship type is what it is:

| Relationship | Arrow Type | Why? |
|---|---|---|
| `CheckoutService` → `RegionFactory` | **Dependency** (dashed) | `RegionFactory` is a **constructor parameter only**. `CheckoutService` does NOT store it as a field. It uses the factory transiently in the constructor to create products, then discards it. That's textbook dependency — "I use you, but I don't hold you." |
| `CheckoutService` → `PaymentGateway` | **Association** (should be solid) | `CheckoutService` stores `paymentGateway` as an instance field (`private PaymentGateway paymentGateway`). This is a long-lived reference — association, not dependency. |
| `CheckoutService` → `Invoice` | **Association** (should be solid) | Same reasoning — `invoice` is stored as a field. |
| `IndiaFactory / USFactory` → `RegionFactory` | **Realization** (dashed + hollow arrow) | They implement the `RegionFactory` interface. Correct in the diagram. |
| `RazorpayGateway` etc. → `PaymentGateway` | **Realization** (dashed + hollow arrow) | They implement the `PaymentGateway` interface. Correct. |
| `IndiaFactory` → `RazorpayGateway` | **Dependency** (dashed) | The factory **creates** these objects inside a method and returns them. It does not store them as fields. Correct — it's a transient "creates" relationship. |

> **Correction:** The diagram uses dependency (dashed) arrows
> from `CheckoutService` to `PaymentGateway` and `Invoice`,
> but these should technically be **association** (solid)
> arrows because `CheckoutService` holds them as instance
> fields. However, `CheckoutService` → `RegionFactory` IS
> correctly a dependency — the factory is only used in the
> constructor and **not** stored as a field.

**TL;DR on your question:** `CheckoutService` →
`RegionFactory` is correctly a dependency (not association)
because there is **no field** storing the factory. The
factory is consumed in the constructor and thrown away. The
`paymentGateway` and `invoice` fields are the ones that
should have been solid association arrows in the diagram.

---

*Keep adding new sections below as you learn more.*
