# Java Fundamentals — Revision Notes

> A living document of things I'm learning about Java.
> Keep adding new topics as you go.

---

## Table of Contents

1. [Memory Management](#1-memory-management)
2. [Copy Constructor](#2-copy-constructor)
3. [Access Modifiers](#3-access-modifiers)
4. [Polymorphism](#4-polymorphism)
5. [Abstraction — Abstract Classes & Interfaces](#5-abstraction--abstract-classes--interfaces)
6. [Static Members](#6-static-members)
7. [Inner Classes](#7-inner-classes)
8. [Class Relationships](#8-class-relationships)
9. [Cloning](#9-cloning)
10. [Strings & the String Pool](#10-strings--the-string-pool)
11. [Garbage Collection](#11-garbage-collection)

---

## 1. Memory Management

Java handles memory management automatically.
Objects that are no longer referenced are cleaned up by the
**Garbage Collector (GC)**, which helps avoid memory leaks.

### Stack Memory

- Stores **primitive variables** (`int`, `double`, `boolean`)
  and **references** to objects (e.g., `obj1`).
- `obj1` holds the reference (memory address) of the object
  created by `new Employee()`.
- When a method finishes executing, the stack frame is cleared,
  but the object in the heap remains as long as something
  references it.
- When a variable is declared **inside a method**, it lives
  on the stack.

### Heap Memory

- Stores **objects** (instances of classes).
  The object created by `new Employee()` is allocated on the heap.
- The object's attributes (e.g., `salary`, `employeeName`)
  live here.
- Memory stays allocated as long as at least one reference
  points to it. Once all references are gone (set to `null`
  or out of scope), the object becomes eligible for GC.
- **Instance variables** declared in a class are stored on the
  heap (as part of the object).

### Other Memory Areas

- **Static variables** are stored in the **class metadata space**
  (part of the method area / metaspace).

---

## 2. Copy Constructor

Java does **not** provide a default copy constructor
(unlike C++). You have to write one yourself.

### Java — manual copy constructor

```java
public Employee(Employee employee) {
    this(employee.employeeName, employee.salary);
}
```

### C++ — for comparison

```cpp
Employee(const Employee &employee) {
    this->employeeName = employee.employeeName;
    this->salary = employee.salary;
}
```

---

## 3. Access Modifiers

Java provides four access levels:

| Modifier      | Class | Package | Subclass | World |
|---------------|:-----:|:-------:|:--------:|:-----:|
| **public**    |  Yes  |   Yes   |   Yes    |  Yes  |
| **protected** |  Yes  |   Yes   |   Yes    |  No   |
| **default**   |  Yes  |   Yes   |   No     |  No   |
| **private**   |  Yes  |   No    |   No     |  No   |

- **Public** — accessible everywhere.
- **Private** — accessible only within the declaring class.
- **Protected** — accessible within the same package *and*
  by subclasses in other packages.
- **Default** (no modifier) — accessible within the same
  package only (package-private).

---

## 4. Polymorphism

### 4.1 Compile-Time Polymorphism (Static)

The compiler determines which method to call based on the
method's **signature** (name + parameter types).
Achieved through **method overloading**.

```java
System.out.println(calc.add(5, 3));       // calls int version
System.out.println(calc.add(5.5, 3.3));   // calls double version
```

> **Note:** Return type alone cannot differentiate overloaded
> methods. Only the parameter list matters.

Java does **not** support operator overloading.

### 4.2 Runtime Polymorphism (Dynamic)

The JVM decides which method to call **at runtime** based on
the **actual object type**, not the reference type.
Achieved through **method overriding**.

```java
Animal myAnimal = new Dog();
myAnimal.sound();  // calls Dog's sound(), not Animal's
```

When the reference type is `Animal`, you can only call methods
that `Animal` declares — but the implementation that runs is
the one from `Dog` (the actual object).

### Rules for Method Overriding

- Must have the **same name, parameters, and return type**
  as the parent method.
- The child method **cannot** have a more restrictive access
  modifier than the parent.
- Only **inheritable** methods (`public` or `protected`)
  can be overridden.
- Use the `@Override` annotation for clarity and
  compile-time safety.

---

## 5. Abstraction — Abstract Classes & Interfaces

Abstraction hides implementation details and exposes only
the necessary interface. Focus on **what** an object does,
not **how** it does it.

### Abstract Classes

- **Cannot** be instantiated directly — must be subclassed.
- Can have both **abstract methods** (no body) and
  **concrete methods** (with body).
- **Can** have constructors (invoked via `super()` from
  the subclass).
- Can hold **instance fields** (per-object state).

```java
abstract class Animal {
    String name;

    Animal(String name) {
        this.name = name;
    }

    abstract void makeSound();
}

class Dog extends Animal {
    Dog(String name) { super(name); }

    void makeSound() {
        System.out.println(
            "The dog " + this.name + " says : Woof!"
        );
    }
}

class Cat extends Animal {
    Cat(String name) { super(name); }

    void makeSound() {
        System.out.println(
            "The cat " + this.name + " says : Meow!"
        );
    }
}
```

### Interfaces

- All methods are implicitly `public` and `abstract`
  (before Java 8).
- **Cannot** have instance variables — fields are implicitly
  `public static final`.
- **Cannot** have constructors.
- A class can implement **multiple** interfaces.
- Interfaces can **extend** other interfaces.

```java
interface Animal {
    void eat();
}

interface Mammal extends Animal {
    void walk();
}

class Human implements Mammal {
    @Override
    public void eat() {
        System.out.println("Human eats food.");
    }

    @Override
    public void walk() {
        System.out.println("Human walks on two legs.");
    }
}
```

### Default Methods (Java 8+)

Added to allow new functionality in interfaces **without
breaking** existing implementations.

```java
default void defaultMethod() {
    System.out.println("This is a default method.");
}
```

- A class can override the default method if needed.
- If **two interfaces** provide the same default method,
  the implementing class **must** override it and explicitly
  choose which to use (or provide its own).
- Use default methods when certain implementing classes
  probably won't need to override — gives a common
  fallback implementation.

### Abstract Class vs Interface — Quick Comparison

| Aspect               | Abstract Class              | Interface                    |
|----------------------|-----------------------------|------------------------------|
| Multiple Inheritance | Extend only **one**         | Implement **many**           |
| State                | Instance fields allowed     | Only `public static final`   |
| Constructors         | Yes                         | No                           |
| Purpose              | Base impl + shared state    | Behavioral contract          |

**Rule of thumb:** Interfaces give a *blueprint*
(what to build). Abstract classes give a *base class*
(where to start building).

---

## 6. Static Members

### Static Variables

Shared across all instances of the class. Belong to
the **class**, not to any single object.

```java
Counter.displayCount(); // Output: Count: 2
```

### Static Methods

- Called on the **class** itself — no object needed.
- Can only access **static** members directly.
- Cannot access instance variables or instance methods
  without creating an object first.

```java
int result = MathUtils.add(5, 3);
```

To access non-static members from a static method,
create an instance:

```java
class Example {
    int instanceVar = 10;

    static void staticMethod() {
        Example obj = new Example();
        System.out.println(
            "Instance variable: " + obj.instanceVar
        );
    }
}
```

### Static Blocks

Run **once** when the class is first loaded, before any
object is created. Used to initialize static variables.

```java
class Example {
    static int value;

    static {
        value = 10;
        System.out.println("Static block executed.");
    }
}

class Main {
    public static void main(String[] args) {
        System.out.println("Value: " + Example.value);
        // Output: Static block executed. Value: 10
    }
}
```

- Multiple static blocks execute in the order they appear.

### Example — Counter using static

```java
class Counter {
    static int count;

    Counter() {
        count++;
    }

    static int getCount() {
        return count;
    }

    static void resetCount() {
        count = 0;
    }
}
```

> `getCount()` and `resetCount()` could also be non-static,
> but then you'd need an object to call them.
> Making them `static` lets you call `Counter.getCount()`
> directly.

---

## 7. Inner Classes

Classes defined **within** another class. Useful for logically
grouping classes that are only used in one place.

### Non-Static Inner Class

- Associated with an **instance** of the outer class.
- Has access to **all** members of the outer class
  (including private, both static and non-static).

### Static Nested Class

- Defined with the `static` modifier.
- Does **not** need an instance of the outer class.
- Can only access **static** members of the outer class.

---

## 8. Class Relationships

Three major types of relationships in OOP:

```
Association
 ├── Aggregation  (weak ownership)
 └── Composition  (strong ownership)
```

### Association

A general relationship where objects of one class interact
with objects of another. Can be:

- **One-to-One** — e.g., `Person` ↔ `Passport`
- **One-to-Many** — e.g., `Teacher` → many `Student`s
- **Many-to-Many** — e.g., `Student` ↔ `Course`

Implemented by holding references, pointers, or collections.

### Aggregation (weak "has-a")

The "whole" contains the "part", but the part can
**exist independently**. Lifecycle is **not** tied together.

```java
class Department {
    private List<Employee> employees;

    public Department(List<Employee> employees) {
        this.employees = employees;
    }
}

class Employee {
    private String name;

    public Employee(String name) {
        this.name = name;
    }
}
```

If `Department` is deleted, `Employee` objects still exist
because they were created **outside** and passed in.

### Composition (strong "part-of")

The "whole" **owns** the "part". If the whole is destroyed,
the parts are destroyed too. Lifecycle is **tightly coupled**.

```java
class House {
    private List<Room> rooms;

    public House() {
        rooms = new ArrayList<>();
        rooms.add(new Room("Living Room"));
        rooms.add(new Room("Bedroom"));
    }
}

class Room {
    private String name;

    public Room(String name) {
        this.name = name;
    }
}
```

`Room` objects are created **inside** `House`.
If `House` is destroyed, its `Room`s go with it.

### How to Tell Them Apart — 3 Questions

| Question                              | Composition        | Aggregation         |
|---------------------------------------|--------------------|---------------------|
| **Who creates the child object?**     | Parent creates it  | External code does  |
| **Can child exist without parent?**   | No                 | Yes                 |
| **Can the child be shared?**          | No                 | Yes                 |

### Composition Example — University & College

```java
class University {
    List<College> colleges;
    String name;

    University(String name) {
        this.name = name;
        colleges = new ArrayList<>();
    }

    void addCollege(String collegeName, String collegeId) {
        colleges.add(new College(collegeName, collegeId));
    }
}

class College {
    String name;
    String id;

    College(String name, String id) {
        this.name = name;
        this.id = id;
    }
}
```

This is **composition** because `University` creates `College`
internally — `College` is not injected from outside and its
lifecycle is tied to `University`.

If it were **aggregation**, you'd pass the object in:

```java
void addCollege(College college) {
    colleges.add(college);
}
```

Now `College` could exist independently and `University`
doesn't control its creation.

### Comparison Table

| Aspect        | Association           | Aggregation                     | Composition                      |
|---------------|-----------------------|---------------------------------|----------------------------------|
| Relationship  | General               | Weak                            | Strong                           |
| Ownership     | None                  | Contains but doesn't own        | Owns entirely                    |
| Independence  | Fully independent     | Part can exist independently    | Part cannot exist independently  |
| Example       | Teacher ↔ Student     | Employee ↔ Department           | Car ↔ Engine                     |

---

## 9. Cloning

Java cloning uses two components:

1. **`Cloneable` interface** — a marker interface (no methods).
   Signals to the JVM that `clone()` is safe to call.
2. **`clone()` method** — defined in `Object`. By default,
   performs a **shallow copy**.

### Shallow Cloning

Copies primitives by value, but references are copied
as-is (both original and clone point to the same
nested objects).

```java
@Override
protected Object clone() throws CloneNotSupportedException {
    return super.clone();  // shallow copy
}
```

### Deep Cloning

Creates a fully independent copy — nested objects are
cloned too.

```java
// In Address class
@Override
protected Object clone() throws CloneNotSupportedException {
    return new Address(this.city);
}

// In Person class
@Override
protected Object clone() throws CloneNotSupportedException {
    Person clonedPerson = (Person) super.clone();
    clonedPerson.address = (Address) address.clone();
    return clonedPerson;
}
```

---

## 10. Strings & the String Pool

- **String literals** are stored in the **String Pool**
  (a special area in the heap).
- `String a = "hello"` and `String b = "hello"` point to
  the **same object** in the pool.
- **Strings are immutable.** You cannot change a `String`'s
  internal value.

```java
a = "world";
```

This does **not** modify the original `"hello"` object.
It makes `a` point to a **new** `String` object `"world"`.

---

## 11. Garbage Collection

All objects are created with the `new` keyword and
allocated on the **heap**.

### Reference Counting (Conceptual)

Each object tracks how many references point to it.
When the count drops to zero, the object can be
deallocated.

An object is eligible for GC when it is **no longer
referenced from any live thread**.

### Why Java Doesn't Use Reference Counting

Java uses **reachability-based** garbage collection instead:

- Uses graph traversal (e.g., **Mark-and-Sweep**) to find
  all objects reachable from active threads.
- Even if an object has a reference count of 1, it could
  be **unreachable** (e.g., cyclic references: A → B → A).
- Java's GC handles cyclic references properly, which
  pure reference counting cannot.

---

*Keep adding new sections below as you learn more.*
