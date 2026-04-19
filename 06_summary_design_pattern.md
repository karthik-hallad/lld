# Design Patterns — Revision Summary

> Quick-reference sheet for all design patterns learned.
> Each pattern includes: what it does, when to use it,
> real-life and code examples, personal anecdotes, and a
> CodeHelper section for implementation guidance.

---

## Master Comparison Table

| Pattern | Category | Intent (One-Liner) | When to Use | Key Mechanism |
|---------|----------|-------------------|-------------|---------------|
| **Singleton** | Creational | Exactly one shared instance globally | Config, logging, DB pool | Private constructor + static accessor |
| **Factory** | Creational | Centralise object creation by type | Runtime type decision, decouple creation from usage | Static factory method returns interface type |
| **Builder** | Creational | Step-by-step construction of complex objects | Many optional params, immutable result | Static inner Builder + fluent `withX()` + `build()` |
| **Abstract Factory** | Creational | Create families of related objects | Region/theme-specific product sets that must stay consistent | Factory-of-factories, each producing a cohesive family |
| **Adapter** | Structural | Make incompatible interfaces work together | Legacy code, third-party APIs with different method signatures | Wrapper class implementing target interface, delegating to adaptee |
| **Decorator** | Structural | Add behaviour to objects dynamically at runtime | Runtime add/remove responsibilities, avoid class explosion | Wrapper sharing same interface, stacking layers |
| **Facade** | Structural | Simplified entry point to a complex subsystem | Many internal services, client needs one-call API | Single class orchestrating multiple subsystem calls |
| **Composite** | Structural | Treat individual objects and groups uniformly | Tree/part-whole hierarchies (folders, bundles) | Leaf + Composite both implement same interface |
| **Proxy** | Structural | Control access to a real object | Lazy loading, caching, access control, logging | Same interface, intercepts calls before delegating |
| **Bridge** | Structural | Decouple abstraction from implementation | Two dimensions of variability (platform + quality) | Abstract class holds interface reference; both hierarchies vary independently |
| **Iterator** | Behavioural | Sequential access without exposing internals | Traverse any collection uniformly | `hasNext()` / `next()` interface, collection owns iterator creation |
| **Observer** | Behavioural | One-to-many notification on state change | Event systems, notification broadcasts | Subject holds list of observers, calls `update()` on change |
| **Strategy** | Behavioural | Swap algorithms at runtime | Multiple interchangeable algorithms, eliminate if-else | Interface for algorithm + context class holds current strategy |
| **Command** | Behavioural | Encapsulate request as an object | Undo/redo, queuing, macros, logging | Command interface with `execute()` / `undo()`, invoker holds commands |
| **Template** | Behavioural | Fixed algorithm skeleton, variable steps | Same algorithm with differing steps across subclasses | `final` template method in base class + abstract/hook methods |
| **State** | Behavioural | Change behaviour based on internal state | Lifecycle/workflow with distinct states and transitions | State interface + context delegates to current state object |
| **Chain of Responsibility** | Behavioural | Pass request along a chain of handlers | Layered processing / middleware / filters | Handler chain where each decides process-or-forward |
| **Mediator** | Behavioural | Centralise communication between objects | Multiple objects interacting but should stay decoupled | Mediator interface, components communicate through it |
| **Memento** | Behavioural | Capture and restore internal state | Undo/redo, checkpointing, rollback | Originator creates Memento; Caretaker stores/manages them |

---

## Creational Patterns

---

### Singleton

**What it does:** Ensures a class has exactly one instance
and provides a global point of access to it.

**When to use:** Global state management — configuration,
logging, database connection pools, caches.

**Real-life example:** AWS Configuration Manager in a
codebase — one shared config object across the entire app.

**Code example:** `Singleton.getInstance()` — private
constructor prevents external instantiation; a static method
lazily creates and returns the single instance.

#### Personal Anecdotes
- Think of it like a shared resource everyone in the office
  uses — one printer, one config file.
- Double-checked locking: the outer null-check avoids
  synchronization overhead on every call. The inner check
  prevents double creation when two threads pass the outer
  check simultaneously.
- Bill Pugh Singleton exploits Java's class loading — inner
  static classes are loaded only when first accessed.

#### CodeHelper
1. Make the constructor `private`.
2. Declare a `private static` instance variable.
3. Provide a `public static getInstance()` method.
4. For thread safety, use **double-checked locking** with
   `volatile`, or the **Bill Pugh idiom** (static inner
   holder class) for best lazy + thread-safe performance.

---

### Factory

**What it does:** Centralises object creation into a factory
class so the client gets the right object by specifying a
type, without knowing the concrete class.

**When to use:** Runtime type decisions, complex instantiation
logic, decoupling creation from business logic.

**Real-life example:** Logistics system — client says "Air"
or "Road," the factory returns the correct `Logistics`
implementation.

**Code example:** `LogisticsFactory.getLogistics("Air")`
returns an `Air` or `Road` instance. `LogisticsService`
never touches concrete classes.

#### Personal Anecdotes
- Think pizza kitchen: the factory handles creation behind
  the scenes. Client doesn't care what type or how to create
  it — just wants the object.
- It's really about DIP: the high-level class
  (`LogisticsService`) should never depend on low-level
  concrete objects. The factory acts as the middleman.

#### CodeHelper
1. Define a **Product interface** (e.g., `Logistics`).
2. Create **Concrete Products** implementing it.
3. Write a **Factory class** with a static method that takes
   a type parameter and returns the correct concrete product.
4. Client calls the factory method, receives the interface
   type, and uses it — zero knowledge of concrete classes.

---

### Builder

**What it does:** Separates construction of a complex object
from its representation using step-by-step fluent methods.
The result is typically immutable.

**When to use:** Objects with many optional parameters,
avoiding telescoping constructors, need for immutability.

**Real-life example:** Amazon cart item — quantity, size,
color, delivery option, gift wrap, discounts — each user
customises differently, built step-by-step.

**Code example:** `new BurgerMeal.BurgerBuilder("wheat", "veg").withCheese(true).withSide("fries").build()`

#### Personal Anecdotes
- Fields are `final` because they're set only via the
  builder's `build()` call through the private constructor.
  Once built, immutable.
- Builder MUST be a **static inner class** — non-static
  requires an outer instance first (chicken-and-egg
  problem). Static inner gets both: no outer instance needed
  + access to private constructor.
- `return this` in `withX()` enables fluent chaining — not
  about correctness (mutation happens anyway), it's about
  readability.

#### CodeHelper
1. Make the target class constructor **private**, accepting
   the Builder.
2. Create a **static inner Builder class** with required
   params in its constructor and optional params via
   `withX()` methods that `return this`.
3. Builder's `build()` calls `new TargetClass(this)`.
4. All fields in the target class are `final`.

---

### Abstract Factory

**What it does:** Provides an interface for creating families
of related objects. Each concrete factory produces a
consistent set of products.

**When to use:** Region/theme/platform-specific product
families that must stay consistent (e.g., India payment +
GST invoice vs. US payment + US invoice).

**Real-life example:** Checkout system — India gets Razorpay
+ GST Invoice, US gets PayPal + US Invoice. The factory
ensures consistency.

**Code example:** `new CheckoutService(new IndiaFactory(), "razorpay")` — the factory creates both the payment gateway
and invoice that belong together.

#### Personal Anecdotes
- Extension of Factory Pattern. In Factory, one factory
  makes whatever you want. In Abstract Factory, the factory
  itself might change (India vs. US). Instead of nesting
  switch cases, create a factory and pass it in.
- Not static classes — because the factory is injected as a
  dependency. DIP: high-level depends on abstraction, not
  low-level factory.

#### CodeHelper
1. Define **Product interfaces** (e.g., `PaymentGateway`,
   `Invoice`).
2. Create concrete product implementations per family
   (India: Razorpay, GSTInvoice; US: PayPal, USInvoice).
3. Define an **Abstract Factory interface** with methods
   to create each product.
4. Create **Concrete Factories** per family implementing it.
5. Client receives a factory via injection and uses it to
   create all products — never references concrete classes.

---

## Structural Patterns

---

### Adapter

**What it does:** Wraps an incompatible class to make it
conform to an expected interface. Translates method calls.

**When to use:** Integrating third-party APIs, legacy code,
or external libraries with different method signatures.

**Real-life example:** Payment gateway — `PayUGateway`
conforms to `PaymentGateway`, but `RazorpayAPI` uses
`makePayment()`. An adapter wraps Razorpay to match
`PaymentGateway.pay()`.

**Code example:** `RazorpayAdapter implements PaymentGateway`
— constructor creates `RazorpayAPI` internally, `pay()`
delegates to `razorpayAPI.makePayment()`.

#### Personal Anecdotes
- Think USB adapter plug: putting another class over the
  original which takes the original as a parameter. The
  adapter class implements the expected interface and each
  method calls the actual class method.
- The key question is where to initialise the actual object
  — do it in the adapter's constructor.

#### CodeHelper
1. Identify the **Target interface** (what client expects).
2. Identify the **Adaptee** (existing incompatible class).
3. Create an **Adapter class** that implements the Target
   interface.
4. In the adapter's constructor, create (or receive) the
   Adaptee instance.
5. Each interface method in the adapter delegates to the
   appropriate Adaptee method.

---

### Decorator

**What it does:** Adds behaviour to objects dynamically at
runtime by wrapping them in layers. Each decorator shares
the same interface as the wrapped object.

**When to use:** Runtime add/remove/combine responsibilities,
avoiding class explosion from feature combinations, stacking
behaviours in arbitrary order.

**Real-life example:** Pizza toppings — start with
MargheritaPizza, wrap with ExtraCheese, wrap with Olives,
wrap with StuffedCrust. Each wrapper adds cost and
description.

**Code example:**
`Pizza p = new StuffedCrust(new Olives(new ExtraCheese(new MargheritaPizza())))`

#### Personal Anecdotes
- Abstract decorator implements the base interface and its
  constructor ALWAYS takes the base type. Any extra method
  uses the decorator constructor to get a duplicate and add
  runtime behaviour.
- We depend on abstractions, not concrete classes — that's
  why the decorator is abstract.
- For Google Docs, at runtime you can make text italic and
  immediately revert — add then remove a decorator. Builder
  can't do this — once built, immutable.
- Builder = WHAT to build (construction-time). Decorator =
  HOW it behaves (runtime).

#### CodeHelper
1. Define a **Component interface** (e.g., `Pizza`).
2. Create **Concrete Components** (e.g., `PlainPizza`).
3. Create an **Abstract Decorator** that implements the
   interface and holds a reference to a Component via
   constructor.
4. Create **Concrete Decorators** extending the abstract
   decorator. Each overrides methods to add behaviour and
   delegates to `super.pizza`.
5. Client wraps objects: `new Cheese(new Olives(base))`.

---

### Facade

**What it does:** Provides a single simplified entry point
to a complex subsystem, hiding the internal orchestration.

**When to use:** When a subsystem has many classes and the
client needs a simple API. Sits at module/library/service
boundaries.

**Real-life example:** Movie booking — one `bookMovieTicket()`
call internally handles payment, seat reservation, ticket
generation, loyalty points, and notification.

**Code example:** `MovieBookingFacade.bookMovieTicket(...)` —
one method, five internal service calls.

#### Personal Anecdotes
- At the end of the day, it's for the client. We just
  provide a simplified interface.
- Normal layered architecture is NOT a Facade. A Facade has
  specific intent: hide complexity for an external consumer
  at a subsystem boundary. No new business logic — just
  orchestration and delegation.

#### CodeHelper
1. Identify the **complex subsystem** (multiple services).
2. Create a **Facade class** that holds references to all
   internal services.
3. Expose one or few high-level methods that orchestrate
   the internal calls in the correct sequence.
4. Client only interacts with the Facade — never with
   internal services directly.

---

### Composite

**What it does:** Composes objects into tree structures.
Both leaf and composite objects implement the same interface,
allowing clients to treat them uniformly.

**When to use:** Part-whole hierarchies (folders in folders,
products in bundles), recursive structures, eliminating
`instanceof` checks.

**Real-life example:** Amazon cart — a `Product` (leaf) and
a `ProductBundle` (composite) both implement `CartItem`.
Bundles can contain products AND other bundles.

**Code example:** `CartItem` interface with `getPrice()` and
`display()`. `ProductBundle.addItem(CartItem)` accepts both
products and bundles.

#### Personal Anecdotes
- Composite is just where the whole and the part both
  implement the same interface. Leaves are declared as the
  interface type for injection; composites are declared as
  concrete type during construction (to access `addItem()`),
  then treated uniformly as the interface during usage.
- Nothing gets "removed" when you upcast — the object in
  memory never changes. The reference type is just a lens.

#### CodeHelper
1. Define a **Component interface** (e.g., `CartItem`) with
   common methods.
2. Create **Leaf classes** implementing it directly.
3. Create **Composite classes** implementing the interface
   and holding a `List<Component>`. Add `addItem()` /
   `removeItem()` methods.
4. Common methods (like `getPrice()`) in the Composite
   iterate over children and aggregate results.
5. Client uses `List<Component>` — treats everything
   uniformly.

---

### Proxy

**What it does:** Provides a placeholder that controls access
to a real object by implementing the same interface.

**When to use:** Lazy loading expensive objects, caching
repeated calls, access control, logging, remote access.

**Real-life example:** Video download cache — first request
downloads from network, subsequent requests return from
cache. Client uses `VideoDownloader` interface throughout.

**Code example:** `CachedVideoDownloader implements VideoDownloader` — checks cache map first, only calls
`RealVideoDownloader` on cache miss.

#### Personal Anecdotes
- Use a separate class so proxy and real object both follow
  SRP.
- Types: Virtual (lazy init), Protection (access control),
  Remote (network abstraction), Smart (logging/counting).

#### CodeHelper
1. Define a **Subject interface** (e.g., `VideoDownloader`).
2. Create the **Real Subject** implementing it.
3. Create a **Proxy class** implementing the same interface.
4. Proxy holds a reference to the Real Subject. In each
   method, add control logic (cache check, auth check, lazy
   init) THEN delegate to the real object.

---

### Bridge

**What it does:** Decouples an abstraction from its
implementation, connecting them via composition. Both
hierarchies can vary independently.

**When to use:** Two dimensions of variability (platform AND
quality, notification type AND delivery channel). Prevents
combinatorial class explosion.

**Real-life example:** Video player — `WebPlayer`,
`MobilePlayer` (abstraction) + `HDQuality`, `UltraHDQuality`
(implementation). Any player works with any quality.

**Code example:** `new WebPlayer(new HDQuality())` — the
player delegates to the quality's `load()` method.

#### Personal Anecdotes
- Forget the CS jargon. "Abstraction" = the WHAT (what kind
  of player?). "Implementation" = the HOW (how does the
  quality work?).
- `VideoPlayer` is abstract class because it needs shared
  state (the quality field) and an enforced constructor.
  `VideoQuality` is just an interface — pure method contract,
  no shared state.
- The entire point: split two things that change
  independently into two separate hierarchies, and connect
  them with association instead of inheritance. That's it.

#### CodeHelper
1. Identify the two dimensions of variability.
2. Define an **Implementor interface** for one dimension
   (e.g., `VideoQuality`).
3. Create **Concrete Implementors** (e.g., `HDQuality`).
4. Define an **Abstraction** (abstract class) that holds a
   reference to the Implementor via constructor.
5. Create **Refined Abstractions** (e.g., `WebPlayer`).
   In their methods, delegate to `implementor.method()`.
6. Client composes: `new WebPlayer(new HDQuality())`.

---

## Behavioural Patterns

---

### Iterator

**What it does:** Provides a way to access elements of a
collection sequentially without exposing the underlying
representation.

**When to use:** Traversing collections uniformly regardless
of internal structure, supporting multiple traversal
strategies, hiding data structure details.

**Real-life example:** YouTube playlist — iterate through
videos without knowing if it's stored as a list, array, or
tree. `createIterator()` handles everything.

**Code example:** `Playlist.createIterator()` returns a
`PlaylistIterator` with `hasNext()` / `next()`. Client
doesn't know or care about the internal structure.

#### Personal Anecdotes
- Client doesn't need to know the structure OR how to
  declare the iterator. `createIterator()` does everything.
- Can have multiple iterators for the same playlist:
  `createIterator(FORWARD)`, `createIterator(BACKWARD)`,
  `createIterator(RANDOM)`.

#### CodeHelper
1. Define an **Iterator interface** with `hasNext()` and
   `next()`.
2. Define an **Iterable interface** with
   `createIterator()`.
3. The **Collection** implements Iterable and returns a
   concrete iterator from `createIterator()`.
4. The **Concrete Iterator** holds the collection + position
   state and implements traversal logic.
5. Client calls `collection.createIterator()` and uses the
   iterator — never accesses the internal structure.

---

### Observer

**What it does:** Defines a one-to-many dependency — when a
subject changes state, all registered observers are notified
automatically.

**When to use:** Event/notification systems, state change
propagation, dynamic subscriptions. Use Pub-Sub at scale.

**Real-life example:** YouTube subscriptions — channel
(subject) uploads a video, all subscribers (observers) get
notified. No polling needed.

**Code example:** `YouTubeChannel` maintains a
`List<Subscriber>`. On `uploadVideo()`, it calls
`subscriber.update(title)` on each observer.

#### Personal Anecdotes
- Two interfaces: observer and subject. The SUBJECT holds
  all OBSERVERS and calls their methods to notify them.
  Not the other way around.
- Tight coupling and SRP violation in naive approach — can't
  keep adding new observers without modifying the subject.
- Observer fails at high scale (synchronous in-memory
  iteration with millions of observers). At scale, use
  Pub-Sub with an event broker for async delivery.

#### CodeHelper
1. Define an **Observer interface** with `update()`.
2. Define a **Subject interface** with `subscribe()`,
   `unsubscribe()`, and `notifyObservers()`.
3. **Concrete Subject** maintains `List<Observer>`. On state
   change, iterates the list and calls `update()` on each.
4. **Concrete Observers** implement their own notification
   logic in `update()`.
5. Client subscribes observers to the subject.

---

### Strategy

**What it does:** Encapsulates a family of algorithms behind
a common interface. A context class holds the current
strategy and delegates to it. Strategies are interchangeable
at runtime.

**When to use:** Multiple interchangeable algorithms,
eliminating if-else chains in business logic, runtime
behaviour selection.

**Real-life example:** Uber ride matching — nearest driver,
surge priority, airport queue. The matching service switches
strategy based on real-time conditions.

**Code example:** `RideMatchingService` holds a
`MatchingStrategy`. Client calls
`rideService.matchRider(location)` — the service delegates
to whichever strategy is currently set.

#### Personal Anecdotes
- Strategy IS just polymorphism. The pattern gives it a name
  and architectural intent — separation of concerns.
- The context class is only useful when you have multiple
  callers or need runtime swapping. If only called in one
  place and never swapped, skip the context.
- It doesn't eliminate conditionals — it relocates them to
  the composition root (factory/config/DI). Each algorithm
  branch becomes its own testable class.

#### CodeHelper
1. Define a **Strategy interface** with the algorithm method.
2. Create **Concrete Strategies** implementing it.
3. Create a **Context class** that holds a Strategy
   reference and delegates to it.
4. Client creates a strategy, passes it to the context (or
   swaps it via setter), and calls the context method.

---

### Command

**What it does:** Encapsulates a request as an object,
decoupling the invoker from the receiver. Enables undo/redo,
queuing, logging, and replay.

**When to use:** Undo/redo support, batch/macro operations,
transaction logging, plug-in architecture, decoupling sender
from receiver.

**Real-life example:** Remote control — pressing a button
executes a command object that knows which device to operate.
The remote never directly calls Light/AC methods.

**Code example:** `LightOnCommand implements Command` — has
`execute()` calling `light.on()` and `undo()` calling
`light.off()`. `RemoteControl` stores command history in a
`Stack<Command>` for undo.

#### Personal Anecdotes
- Undo in naive code is VERY tightly coupled — both Light
  and AC baked into the remote control.
- Command turns actions into objects. Once it's an object,
  you can store it, queue it, undo it, redo it, log it,
  serialize it, and replay it. Without the pattern, actions
  are just method calls — they vanish the moment they
  execute.
- For redo: use two stacks (undo + redo). New action clears
  the redo stack. This is how Ctrl+Z/Ctrl+Y works.

#### CodeHelper
1. Define a **Command interface** with `execute()` and
   `undo()`.
2. Create **Concrete Commands** — each holds a reference to
   a Receiver and calls its methods.
3. Create **Receiver classes** (e.g., `Light`, `AC`) with
   the actual business logic.
4. Create an **Invoker** (e.g., `RemoteControl`) that stores
   commands in slots and a `Stack<Command>` for history.
5. Client wires commands to receivers, assigns them to
   invoker slots. Invoker calls `execute()` and pushes to
   history. Undo pops and calls `undo()`.

---

### Template

**What it does:** Defines the skeleton of an algorithm in a
base class `final` method. Subclasses override specific steps
but the overall structure and order remain fixed.

**When to use:** Multiple classes follow the same algorithm
but differ in a few steps. Enforce step order. Avoid
duplicating common logic.

**Real-life example:** Notification service — Email and SMS
both follow the same flow (rate limit, validate, format,
send, analytics) but differ in how they compose and send
the message.

**Code example:** `NotificationSender.send()` is `final` and
calls `rateLimitCheck()`, `validateRecipient()`,
`composeMessage()`, `sendMessage()`, `postSendAnalytics()`.
Subclasses override the abstract and hook methods.

#### Personal Anecdotes
- Access modifier guide for template steps:
  `private` = no override capability needed.
  `protected` = override possible but not mandatory.
  `protected abstract` = override is NOT OPTIONAL.
  Then define `public final` template method that calls them
  in sequence.
- That's literally it. Once you know which methods are
  private, protected, or abstract, the template method just
  defines the sequence.

#### CodeHelper
1. Create an **abstract base class** with a `public final`
   template method that defines the step sequence.
2. Mark common steps as `private` (concrete, no override).
3. Mark variable steps as `protected abstract` (subclasses
   must implement).
4. Mark optional steps as `protected` with a default body
   (hook — subclasses may override).
5. Subclasses extend the base and implement the abstract
   methods.

---

### State

**What it does:** Encapsulates state-specific behaviour into
separate classes. The object (context) delegates to its
current state object, and transitions happen by swapping the
state.

**When to use:** Object behaviour depends on internal state,
finite state transitions, eliminating complex switch/if-else
on state strings.

**Real-life example:** Food delivery order — Order Placed ->
Preparing -> Out for Delivery -> Delivered. Each state has
distinct allowed actions (e.g., cancel only in early states).

**Code example:** `OrderContext` holds an `OrderState`.
`order.next()` calls `currentState.next(this)`, which
transitions to the next state. Each state class decides
valid transitions.

#### Personal Anecdotes
- Hardcoded transitions inside states violate OCP — if you
  add a state between "placed" and "preparing," you edit
  existing state classes. Better: externalize transitions
  into a transition table.
- State vs Chain: State = "I am in state X, event Y
  happened, I transition to state Z." Object has memory.
  Chain = "Pass request through filters. Each decides
  independently." No memory between calls. State is a
  **lifecycle**. Chain is a **pipeline**.

#### CodeHelper
1. Define a **State interface** with methods for each event
   (e.g., `next()`, `cancel()`).
2. Create **Concrete State classes** implementing the
   interface. Each method either transitions to a new state
   (via `context.setState(new NextState())`) or rejects the
   action.
3. Create a **Context class** holding the current state. It
   delegates all calls to `currentState.method(this)`.
4. (Advanced) Externalize transitions into a **transition
   table** in the context rather than hardcoding next-state
   knowledge in each state class.

---

### Chain of Responsibility

**What it does:** Passes a request along a chain of handlers.
Each handler independently decides whether to process it or
forward it to the next handler.

**When to use:** Layered processing / progressive filtering
(middleware, servlet filters, logging appenders). NOT for
routing to a known handler — use a Map lookup for that.

**Real-life example:** HTTP servlet filters — Auth ->
RateLimit -> Logging -> Controller. Each filter independently
decides pass-or-stop. No filter knows about any other.

**Code example:** `AuthFilter`, `RateLimitFilter`,
`LoggingFilter` each implement `Filter.doFilter()`. The
chain is configurable — add/remove/reorder without touching
filter code.

#### Personal Anecdotes
- The customer support example (routing by type string) is
  misleading — that's just a glorified switch statement.
  A `Map<String, Handler>` does it better with O(1) lookup.
- CoR shines when you DON'T have a single routing key —
  when each handler inspects the request and makes its own
  decision. Think middleware, not routing.
- State is a lifecycle. Chain is a pipeline. State has
  memory (current state). Chain has no memory between calls.

#### CodeHelper
1. Define a **Handler** abstract class/interface with
   `handleRequest()` and a `nextHandler` reference.
2. Create **Concrete Handlers** — each checks if it should
   process. If yes, process (and optionally pass forward).
   If no, forward to `nextHandler`.
3. Client wires the chain: `A.setNext(B); B.setNext(C)`.
4. Send the request to the first handler.
5. Ensure the last handler in the chain handles the "no one
   can process this" fallback.

---

### Mediator

**What it does:** Centralises complex communication between
objects into a single mediator. Objects interact through the
mediator instead of directly with each other.

**When to use:** Multiple objects interacting but should
stay decoupled, central permission/rule management, flexible
message broadcasting/filtering.

**Real-life example:** Collaborative document editor —
users (Alice, Bob, Charlie) don't notify each other directly.
The `CollaborativeDocument` mediator broadcasts changes to
all participants.

**Code example:** `DocumentSessionMediator` interface with
`broadcastChange()` and `join()`. `User.makeChange()` calls
`mediator.broadcastChange(change, this)` — the mediator
iterates all users except the sender.

#### Personal Anecdotes
- Can't store too many users in a single object — direct
  references between all users don't scale. The mediator
  solves this by centralising the communication.
- Think Air Traffic Control: planes don't talk to each
  other directly. ATC coordinates movements. ATC can also
  filter — tell only nearby planes about a message.

#### CodeHelper
1. Define a **Mediator interface** with communication
   methods (e.g., `broadcastChange()`, `join()`).
2. Create a **Concrete Mediator** that holds a list of
   participants and implements the communication logic.
3. **Colleague classes** (participants) hold a reference to
   the mediator. All communication goes through
   `mediator.method()` instead of direct references.
4. Mediator handles broadcasting, filtering,
   transformation, and role management centrally.

---

### Memento

**What it does:** Captures an object's internal state as an
immutable snapshot that can be stored and restored later
without violating encapsulation.

**When to use:** Undo/redo functionality, state rollback,
checkpointing, saving/restoring workflow state.

**Real-life example:** Resume editor — make changes to name,
education, skills. Each version is saved as a memento.
Undo restores the previous version from a history stack.

**Code example:** `ResumeEditor.save()` returns a
`Memento` (inner class with private fields).
`ResumeHistory` (caretaker) stores a `Stack<Memento>`.
`history.undo(editor)` pops and restores.

#### Personal Anecdotes
- Think DynamoDB memento: saves internal state based on
  workflow ID. When you want to revert, use the memento.
  The storage service is the memento, `resumeOrStart` is
  the caretaker, the code is the originator.
- The originator alone creates its snapshots — this
  preserves encapsulation. The caretaker never looks inside
  the memento, it just stores/retrieves it.

#### CodeHelper
1. In the **Originator** (the object to save):
   - Add a `save()` method that returns a new `Memento`
     capturing all internal state.
   - Add a `restore(Memento)` method that sets state from
     the memento.
   - Define `Memento` as a **static inner class** with
     `private final` fields and private getters (only the
     originator can access them).
2. Create a **Caretaker** class with a `Stack<Memento>`.
   - `save(originator)` pushes `originator.save()`.
   - `undo(originator)` pops and calls
     `originator.restore(memento)`.

---

*Use this file for quick revision before interviews or
design discussions.*
