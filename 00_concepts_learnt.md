# Concepts Learnt — LLD Review Checklist

> This file tracks every concept learned during the
> LLD journey. It serves two purposes:
>
> 1. **Revision** — quickly recall what you've studied.
> 2. **Code Review** — when reviewing an LLD solution,
>    the AI walks through each concept below and reports:
>    - Is this concept **relevant** to the problem?
>    - If yes, is it **implemented** correctly? If not, why?
>    - If not implemented, **why** — not needed, or missed?
>
> Not every concept applies to every problem. For example,
> DRY has exceptions in performance-critical code, and
> YAGNI means you shouldn't add unused features. The
> review should be pragmatic, not dogmatic.

---

## How to Use This File for Review

When given an LLD solution to review, go through each
category below and for every concept ask:

1. **Relevant?** — Does this concept apply to the problem
   at hand? (e.g., ISP is relevant if there are interfaces;
   Cloning is relevant if objects need copying.)
2. **Implemented?** — If relevant, is it correctly applied
   in the code?
3. **Missing?** — If relevant but not implemented, flag it
   with an explanation of what should change.
4. **Not applicable?** — If the concept doesn't apply,
   briefly state why (e.g., "No inheritance hierarchy
   exists, so LSP is not applicable here").

For concepts **not listed here**, the AI should still
flag them if they are relevant to LLD best practices,
and suggest the user learn about them.

---

## A. Java Language Fundamentals

> Source: `java_oopd.md`

### A1. Memory Management — Stack vs Heap
- **Learnt:** Yes
- **What:** Primitives and references live on the stack
  (per method call); objects live on the heap.
  Static variables live in class metadata space
  (metaspace).
- **When relevant:** Always — understanding where objects
  live affects reasoning about scope, lifetime, and GC.

### A2. Copy Constructor
- **Learnt:** Yes
- **What:** Java has no default copy constructor (unlike
  C++). You must write one manually, typically delegating
  to another constructor via `this(...)`.
- **When relevant:** When objects need to be duplicated
  (e.g., prototype pattern, defensive copies).

### A3. Access Modifiers
- **Learnt:** Yes
- **What:** Four levels — `public`, `protected`, default
  (package-private), `private`. Controls visibility of
  fields and methods.
- **When relevant:** Always — proper encapsulation
  requires choosing the right access level. Default to
  most restrictive (`private`) and relax only when needed.

### A4. Polymorphism — Compile-Time (Overloading)
- **Learnt:** Yes
- **What:** Compiler resolves the method based on
  parameter types at compile time. Return type alone
  cannot differentiate overloaded methods.
- **When relevant:** When a class needs multiple versions
  of a method with different parameter types.

### A5. Polymorphism — Runtime (Overriding)
- **Learnt:** Yes
- **What:** JVM resolves the method at runtime based on
  the actual object type (not the reference type).
  Achieved via method overriding. This is the basis of
  dynamic dispatch.
- **When relevant:** Core to any design using inheritance
  or interface-based polymorphism. Used heavily in
  Strategy, Factory, Template Method patterns.

### A6. Abstract Classes
- **Learnt:** Yes
- **What:** Cannot be instantiated. Can have both abstract
  and concrete methods, constructors, and instance fields.
  A class can extend only one abstract class.
- **When relevant:** When you need a base implementation
  with shared state that subclasses build upon.

### A7. Interfaces
- **Learnt:** Yes
- **What:** Define a behavioral contract. All methods are
  implicitly `public abstract` (pre-Java 8). Fields are
  `public static final`. A class can implement multiple
  interfaces. Interfaces can extend other interfaces.
- **When relevant:** When you want to define a capability
  or contract without tying it to a specific
  implementation. Core to DIP, Strategy, and most
  design patterns.

### A8. Default Methods (Java 8+)
- **Learnt:** Yes
- **What:** Interfaces can have methods with a body using
  `default`. If two interfaces have the same default
  method, the implementing class must override it.
- **When relevant:** When adding new methods to an
  existing interface without breaking implementations.

### A9. Static Members (Variables, Methods, Blocks)
- **Learnt:** Yes
- **What:** Belong to the class, not to instances.
  Static methods cannot access non-static members
  directly. Static blocks run once when the class loads.
- **When relevant:** Utility classes, counters, factory
  methods, singleton pattern.

### A10. Inner Classes (Static Nested & Non-Static)
- **Learnt:** Yes
- **What:** Non-static inner classes have access to all
  outer class members (including private). Static nested
  classes can only access static members of the outer
  class.
- **When relevant:** When a class is only used within
  another class (e.g., Builder pattern, iterators).

### A11. Cloning — Shallow vs Deep
- **Learnt:** Yes
- **What:** `Cloneable` is a marker interface. `clone()`
  in `Object` does a shallow copy by default. For deep
  cloning, override `clone()` and manually clone nested
  objects.
- **When relevant:** Prototype pattern, defensive copies,
  any time objects with mutable nested state need
  duplication.

### A12. String Pool & Immutability
- **Learnt:** Yes
- **What:** String literals are stored in the String Pool.
  `"hello" == "hello"` is true (same pool reference).
  Strings are immutable — reassignment creates a new
  object, it does not modify the original.
- **When relevant:** Understanding equality (`==` vs
  `.equals()`), memory optimization, thread safety.

### A13. Garbage Collection
- **Learnt:** Yes
- **What:** Java uses reachability-based GC (Mark-and-Sweep),
  not reference counting. Objects are eligible for GC
  when unreachable from any live thread. Handles cyclic
  references, which reference counting cannot.
- **When relevant:** Understanding object lifecycle,
  avoiding memory leaks (e.g., static collections
  holding references indefinitely).

---

## B. Class Relationships

> Source: `java_oopd.md`, `lld_basics.md`

### B1. Association
- **Learnt:** Yes
- **What:** A general relationship where one class uses or
  interacts with another. Can be one-to-one, one-to-many,
  or many-to-many.
- **When relevant:** Whenever classes interact. Ask: is
  this a whole-part relationship? If no, it's association.

### B2. Aggregation (Weak HAS-A)
- **Learnt:** Yes
- **What:** Whole-part relationship where the part can
  exist independently. The part is created externally and
  passed in. Lifecycle is NOT tied.
- **When relevant:** When an object contains another but
  doesn't own its lifecycle (e.g., Department ↔ Employee).
- **How to identify:** Child created externally, can exist
  without parent, can be shared.

### B3. Composition (Strong HAS-A)
- **Learnt:** Yes
- **What:** Whole-part relationship where the part cannot
  exist without the whole. Parent creates the part
  internally. Lifecycle is tightly coupled.
- **When relevant:** When an object fully owns its parts
  (e.g., House ↔ Room, University ↔ College).
- **How to identify:** Parent creates child, child cannot
  exist alone, child is not shared.

### B4. Inheritance (IS-A)
- **Learnt:** Yes
- **What:** Subclass inherits properties and behavior from
  a superclass. Can extend or override.
- **When relevant:** When there's a true "is-a"
  relationship. Be cautious — prefer composition over
  inheritance when the relationship isn't clear-cut.

### B5. Realization (Implements)
- **Learnt:** Yes
- **What:** A class implements an interface, fulfilling
  the contract declared by that interface.
- **When relevant:** Whenever interfaces are used to
  define behavioral contracts.

### B6. Dependency (Uses Temporarily)
- **Learnt:** Yes
- **What:** One class uses another temporarily (e.g., as a
  method parameter or local variable). No object sharing.
  Changes to the used class may affect the dependent class.
- **When relevant:** Service-to-service calls, utility
  usage. Weaker than association.

---

## C. Core Design Principles

> Source: `lld_basics.md`

### C1. DRY — Don't Repeat Yourself
- **Learnt:** Yes
- **What:** Avoid code duplication. Extract common logic
  into reusable methods or classes.
- **When relevant:** Always — but with exceptions.
- **Exceptions:** Don't apply when it causes premature
  abstraction, hurts performance, sacrifices readability,
  or destabilizes legacy code.

### C2. KISS — Keep It Simple, Stupid
- **Learnt:** Yes
- **What:** Prefer the simplest solution that works.
  Avoid overengineering.
- **When relevant:** Always. If a one-liner works, don't
  write ten lines.

### C3. YAGNI — You Aren't Gonna Need It
- **Learnt:** Yes
- **What:** Don't add functionality until it's actually
  needed. Avoid building for hypothetical futures.
- **When relevant:** Always — but with exceptions.
- **Exceptions:** When a requirement is well-known and
  imminent, or in performance-critical areas where early
  preparation catches bottlenecks.

---

## D. SOLID Principles

> Source: `lld_basics.md`

### D1. Single Responsibility Principle (SRP)
- **Learnt:** Yes
- **What:** A class should have one responsibility and one
  reason to change. Applies to methods, modules,
  microservices — not just classes.
- **When relevant:** Always. Look for classes mixing
  business logic with DB/UI concerns.
- **How to spot violations:** Class does more than one
  distinct job; changing one feature risks breaking another.

### D2. Open/Closed Principle (OCP)
- **Learnt:** Yes
- **What:** Open for extension, closed for modification.
  Use interfaces/abstractions so new behavior can be added
  without changing existing code.
- **When relevant:** When branching logic grows (if/else
  chains for types). Frameworks, plugins, extensible
  systems.
- **How to spot violations:** Adding a new variant requires
  modifying existing classes.

### D3. Liskov Substitution Principle (LSP)
- **Learnt:** Yes
- **What:** A subclass must be substitutable for its parent
  without breaking correctness. Overriding should not
  change the parent's contract.
- **When relevant:** Any inheritance hierarchy. Classic
  violation: Rectangle-Square problem.
- **How to spot violations:** Subclass overrides methods
  in a way that changes meaning; replacing parent with
  child causes unexpected behavior.

### D4. Interface Segregation Principle (ISP)
- **Learnt:** Yes
- **What:** Don't force a class to implement methods it
  doesn't use. Prefer slim, purpose-specific interfaces.
- **When relevant:** When interfaces exist. Look for
  classes with empty or stub method implementations.
- **How to spot violations:** A class implements an
  interface but leaves several methods as no-ops or throws
  `UnsupportedOperationException`.

### D5. Dependency Inversion Principle (DIP)
- **Learnt:** Yes
- **What:** High-level modules should depend on
  abstractions, not on low-level modules. Use dependency
  injection to pass concrete implementations at runtime.
- **When relevant:** Whenever a high-level class creates
  or directly depends on a specific low-level class.
- **How to spot violations:** `new ConcreteClass()` inside
  a high-level module instead of receiving an abstraction
  via constructor/setter.

---

## E. UML & Modeling

> Source: `lld_basics.md`

### E1. UML Diagram Types (Structural vs Behavioral)
- **Learnt:** Yes
- **What:** Structural diagrams show static structure
  (classes, objects, components). Behavioral diagrams show
  dynamic behavior (sequences, activities, state changes).
- **When relevant:** When designing or documenting a system
  before/during implementation.

### E2. Class Diagrams
- **Learnt:** Yes
- **What:** Show classes, attributes, methods, visibility
  markers (+, -, #, ~), and relationships. Most important
  UML diagram for LLD.
- **When relevant:** Always in LLD — every solution should
  be expressible as a class diagram.

### E3. Perspective Diagrams
- **Learnt:** Yes
- **What:** Three perspectives — Conceptual (business
  view), Specification (design view), Implementation
  (code view). Each adds more detail.
- **When relevant:** When communicating designs to
  different audiences (analysts vs architects vs devs).

### E4. UML Relationship Notations
- **Learnt:** Yes
- **What:** Association (solid line), Aggregation (hollow
  diamond), Composition (filled diamond), Inheritance
  (hollow triangle), Realization (dashed + hollow triangle),
  Dependency (dashed + open arrow).
- **When relevant:** When drawing or reading class diagrams.

---

## F. Design Patterns — Creational

> Source: `creational.md`

### F1. Singleton Pattern
- **Learnt:** Yes
- **What:** Ensures exactly one instance of a class exists
  globally. Uses private constructor + static accessor.
- **When relevant:** Global state, config managers, logging,
  DB connection pools.
- **How to identify:** Need for shared global resource,
  class should not be instantiated more than once.

### F2. Factory Pattern
- **Learnt:** Yes
- **What:** Centralises object creation into a factory
  class. Client requests an object by type and gets the
  correct concrete instance without knowing the class.
- **When relevant:** When instantiation logic is complex,
  the concrete class depends on runtime conditions, or you
  want to decouple creation from usage.
- **How to spot violations:** `new ConcreteClass()` inside
  business logic with if-else to pick the type.

### F3. Builder Pattern
- **Learnt:** Yes
- **What:** Separates construction of a complex object from
  its representation. Uses a static inner Builder class with
  fluent `withX()` methods and a `build()` call.
- **When relevant:** Objects with many optional parameters,
  telescoping constructor anti-pattern, need for immutable
  objects after construction.

### F4. Abstract Factory Pattern
- **Learnt:** Yes
- **What:** Provides an interface for creating families of
  related objects. A factory-of-factories where each factory
  produces a consistent product family.
- **When relevant:** Multiple related objects must be created
  as a cohesive set (e.g., region-specific payment + invoice).
  The family can change at runtime.

---

## G. Design Patterns — Structural

> Source: `structural.md`

### G1. Adapter Pattern
- **Learnt:** Yes
- **What:** Wraps an incompatible class to conform to an
  expected interface. Acts as a translator between the target
  interface and the adaptee.
- **When relevant:** Integrating third-party libraries,
  legacy code, or external APIs with incompatible interfaces.

### G2. Decorator Pattern
- **Learnt:** Yes
- **What:** Adds behaviour to objects dynamically at runtime
  by wrapping them in decorator objects that share the same
  interface.
- **When relevant:** Need to add/remove/combine
  responsibilities at runtime. Avoid class explosion from
  every combination of features.
- **How to identify:** If you see many subclasses for
  combinations of features (e.g., `CheeseOlivePizza`),
  decorators are a better fit.

### G3. Facade Pattern
- **Learnt:** Yes
- **What:** Provides a simplified, single entry point to a
  complex subsystem. Orchestrates multiple internal services
  behind one method call.
- **When relevant:** When a subsystem has many classes and
  the client needs a simpler API. Sits at module/library
  boundaries.

### G4. Composite Pattern
- **Learnt:** Yes
- **What:** Composes objects into tree structures where both
  leaf and composite objects implement the same interface,
  allowing uniform treatment.
- **When relevant:** Part-whole hierarchies (folders in
  folders, products in bundles), recursive structures,
  avoiding `instanceof` checks.

### G5. Proxy Pattern
- **Learnt:** Yes
- **What:** Provides a surrogate/placeholder that controls
  access to a real object. Implements the same interface as
  the real object.
- **When relevant:** Lazy loading, caching, access control,
  logging, remote object access.

### G6. Bridge Pattern
- **Learnt:** Yes
- **What:** Decouples an abstraction from its implementation
  so both can vary independently. Connects two hierarchies
  via composition instead of inheritance.
- **When relevant:** Multiple dimensions of variability
  (platform AND quality, shape AND color). Prevents
  combinatorial class explosion.

---

## H. Design Patterns — Behavioural

> Source: `behavioural.md`

### H1. Iterator Pattern
- **Learnt:** Yes
- **What:** Provides sequential access to elements of a
  collection without exposing its internal representation.
  Uses `hasNext()` / `next()` interface.
- **When relevant:** Traversing collections uniformly,
  supporting multiple traversal strategies, hiding internal
  data structure.

### H2. Observer Pattern
- **Learnt:** Yes
- **What:** Defines a one-to-many dependency so that when a
  subject changes state, all observers are notified
  automatically.
- **When relevant:** Event systems, notification broadcasts,
  state change propagation. Use Pub-Sub at scale.

### H3. Strategy Pattern
- **Learnt:** Yes
- **What:** Encapsulates a family of algorithms behind a
  common interface and makes them interchangeable at runtime
  via a context class.
- **When relevant:** Multiple interchangeable algorithms,
  eliminating if-else chains, runtime behaviour selection.

### H4. Command Pattern
- **Learnt:** Yes
- **What:** Encapsulates a request as an object, enabling
  undo/redo, queuing, logging, and replay. Decouples invoker
  from receiver.
- **When relevant:** Undo/redo, batch operations, macro
  commands, transaction logging, plug-in architecture.

### H5. Template Pattern
- **Learnt:** Yes
- **What:** Defines the skeleton of an algorithm in a base
  class, letting subclasses override specific steps without
  changing the overall structure. Template method is `final`.
- **When relevant:** Multiple classes follow the same
  algorithm but differ in a few steps. Enforce fixed step
  order with optional customisation.

### H6. State Pattern
- **Learnt:** Yes
- **What:** Encapsulates state-specific behaviour into
  separate classes. The object delegates behaviour to its
  current state object, enabling behaviour change without
  altering the code.
- **When relevant:** Object behaviour depends on internal
  state, well-defined finite state transitions, avoiding
  complex switch/if-else on state.

### H7. Chain of Responsibility Pattern
- **Learnt:** Yes
- **What:** Passes a request along a chain of handlers where
  each handler independently decides to process or forward
  the request.
- **When relevant:** Layered processing / progressive
  filtering (middleware, servlet filters). NOT for routing
  to a known handler — use Map lookup for that.

### H8. Mediator Pattern
- **Learnt:** Yes
- **What:** Centralises complex communication between objects
  into a single mediator object. Objects interact through the
  mediator instead of directly with each other.
- **When relevant:** Multiple objects interacting but should
  remain decoupled, central permission/rule management,
  flexible broadcasting/filtering of messages.

### H9. Memento Pattern
- **Learnt:** Yes
- **What:** Captures an object's internal state as an
  immutable snapshot (memento) that can be restored later
  without violating encapsulation. Uses originator, memento,
  and caretaker.
- **When relevant:** Undo/redo functionality, state rollback,
  checkpointing, preserving encapsulation during state saves.

---

*Last updated: 2026-03-15*
