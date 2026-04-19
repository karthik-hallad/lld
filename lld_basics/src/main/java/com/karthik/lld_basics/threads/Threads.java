package com.karthik.lld_basics.threads;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;


public class Threads {
  public void run() {
    System.err.println("Running CreateManageThreads to demonstrate threading...");
    CreateManageThreads createManageThreads = new CreateManageThreads();
    createManageThreads.runFunction();

    System.err.println("Running ExecutorThreads to demonstrate threading...");
    ExecutorThreads executorThreads = new ExecutorThreads();
    try {
        executorThreads.runFunction();
    } catch (Exception e) {
        e.printStackTrace();
    }

    Syncronization syncronization = new Syncronization();
    syncronization.run();

    System.err.println("Locks and sync demo");
    MutexThreads mutexThreads = new MutexThreads();
    mutexThreads.run();
  }
}

class CreateManageThreads {
  public void runFunction() {

    // ─────────────────────────────────────────────────────────────────────────
    // APPROACH 1: Runnable
    // ─────────────────────────────────────────────────────────────────────────
    // Runnable is the simplest way to define a task for a thread.
    // Limitation: run() returns void — you CANNOT get a result back from
    // the task. There is no built-in way to know if it succeeded or failed
    // beyond using shared mutable state (error-prone).
    // Also, run() cannot throw checked exceptions — you must handle them inside.
    System.err.println("Runnable Threads");
    Thread smsThreadRunnable = new Thread(new SMSTaskRunnable());
    Thread emailThreadRunnable = new Thread(new EmailTaskRunnable());
    smsThreadRunnable.start();
    emailThreadRunnable.start();

    try {
      // join() makes the MAIN thread wait here until each worker thread finishes.
      // Without join(), the main thread would continue running and could exit
      // before the worker threads complete their work.
      // We must do this manually because Runnable gives us no other handle
      // to know when a task is done.
      smsThreadRunnable.join();
      emailThreadRunnable.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // APPROACH 2: Callable + FutureTask (still using raw Thread)
    // ─────────────────────────────────────────────────────────────────────────
    // Callable is like Runnable but solves two of its problems:
    //   1. call() returns a value (generic type T).
    //   2. call() can throw checked exceptions — the exception is captured
    //      inside the FutureTask and re-thrown when you call get().
    //
    // Since Thread only accepts Runnable, we wrap Callable inside FutureTask.
    // FutureTask implements both Runnable AND Future:
    //   - As a Runnable: Thread calls futureTask.run() → which calls callable.call()
    //   - As a Future:   gives us get() to retrieve the result later.
    System.err.println("Callable Threads");
    FutureTask<String> smFutureTask = new FutureTask<>(new SMSTaskCallable());
    FutureTask<String> emailFutureTask = new FutureTask<>(new EmailTaskCallable());
    Thread smsThreadCallable = new Thread(smFutureTask);
    Thread emailThreadCallable = new Thread(emailFutureTask);
    smsThreadCallable.start();
    emailThreadCallable.start();

    try {
      // No need of join() - Waits if necessary for the computation to complete,
      // and then retrieves its result.
      // Now safe to call get() — the threads have already finished so this
      // returns immediately without blocking.
      System.err.println(smFutureTask.get());
      System.err.println(emailFutureTask.get());
    } catch (Exception e) {
      e.printStackTrace();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // APPROACH 3: ExecutorService (the recommended, production-grade approach)
    // ─────────────────────────────────────────────────────────────────────────
    // WHAT ExecutorService offers over raw Thread:
    //   1. THREAD POOL — threads are pre-created and REUSED for multiple tasks.
    //      Creating a new Thread per task is expensive (OS-level resource).
    //      A pool of 2 threads here means at most 2 threads are allocated,
    //      no matter how many tasks you submit.
    //   2. TASK QUEUE — if you submit more tasks than pool size, extras wait
    //      in a queue automatically. You don't manage this yourself.
    //   3. LIFECYCLE MANAGEMENT — you control when the pool starts and stops
    //      via shutdown(). No need to track individual thread objects.
    //   4. Future returned automatically — submit() returns a Future<T>
    //      without needing to manually wrap in FutureTask.
    //   5. Exception propagation — any exception thrown in call() is wrapped
    //      and re-thrown as ExecutionException when you call future.get().
    //
    // KEY DIFFERENCE from Runnable/raw Thread:
    //   - With raw Thread, YOU create threads, start them, and join them.
    //   - With ExecutorService, YOU just submit tasks; the pool manages threads.
    //
    // newFixedThreadPool(2): creates a pool with exactly 2 worker threads.
    SMSTaskCallable smsTask = new SMSTaskCallable();
    EmailTaskCallable emailTask = new EmailTaskCallable();
    ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(2);

    // submit() hands the task to the pool and returns a Future immediately.
    // The task runs asynchronously in one of the pool's threads.
    Future<String> st = executorService.submit(smsTask);
    Future<String> et = executorService.submit(emailTask);

    try {
        // WHY NO join() HERE?
        // Future.get() already BLOCKS the main thread until the task finishes
        // and returns its result. It is the equivalent of join() + result retrieval
        // combined into one call.
        // With raw Thread we called join() (wait) and then get() (read result)
        // as two separate steps. Here, get() does both in one step.
        // So join() is not needed — and there are no Thread objects to join on anyway,
        // because we never created or held references to the actual threads;
        // the ExecutorService owns and manages them internally.
        System.out.println(st.get()); // blocks until SMS task is done, then prints result
        System.out.println(et.get()); // blocks until Email task is done, then prints result
    } catch (InterruptedException | ExecutionException e) {
        // InterruptedException: current thread was interrupted while waiting in get()
        // ExecutionException: the task itself threw an exception inside call()
        e.printStackTrace();
    }

    // shutdown() signals the ExecutorService to stop accepting new tasks
    // and allows currently running/queued tasks to finish before the pool closes.
    // Without shutdown(), the JVM may not exit because pool threads are non-daemon
    // threads that keep running.
    executorService.shutdown();
    System.err.println("Main loop exiting");
  }
}


// ─────────────────────────────────────────────────────────────────────────────
// Runnable tasks — no return value, no checked exception from signature
// ─────────────────────────────────────────────────────────────────────────────

// Simulates sending an SMS by sleeping for 2 seconds, then printing a message.
class SMSTaskRunnable implements Runnable {
    public void run() {
        try {
            Thread.sleep(2000); // simulate network/IO delay for SMS
            System.out.println("SMS Sent using Runnable.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Notice: nothing to return. Caller has no way to get a result.
    }
}

// Simulates sending an Email by sleeping for 3 seconds, then printing a message.
class EmailTaskRunnable implements Runnable {
    public void run() {
        try {
            Thread.sleep(3000); // simulate longer network/IO delay for Email
            System.out.println("Email Sent using Runnable.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Notice: nothing to return. Caller has no way to get a result.
    }
}


// ─────────────────────────────────────────────────────────────────────────────
// Callable tasks — return a value; can propagate exceptions via Future.get()
// ─────────────────────────────────────────────────────────────────────────────
// WHY DOES EACH TASK PRODUCE TWO LINES OF OUTPUT?
// Each call() method has two separate print actions:
//   1. System.out.println(...) INSIDE call() — executed by the WORKER thread.
//      This prints "SMS Sent using Callable." / "Email Sent using Callable."
//   2. return "SMS Sent" / return "Email Sent" — the return value is stored
//      in the Future. When the MAIN thread calls st.get() / et.get(), it
//      retrieves this string and prints it with System.out.println(st.get()).
// So: worker thread prints one line, main thread prints another — two prints total per task.
//
// WHY DOES THE OUTPUT APPEAR IN SEQUENTIAL PAIRS (SMS pair, THEN Email pair)?
// Both tasks are submitted and start concurrently in the thread pool (t=0).
// But st.get() is called FIRST — it blocks the main thread until SMS finishes (t=2s).
// Only after st.get() returns does the main thread reach et.get().
// Email was already running in parallel, so it finishes shortly after (t=3s).
// Result: SMS pair appears together, then Email pair — even though both tasks ran in parallel.

// Callable<String> means call() returns a String result.
// This result is captured by FutureTask/ExecutorService and available via get().
class EmailTaskCallable implements Callable<String> {
    public String call() throws Exception {
        try {
            Thread.sleep(3000); // simulate longer delay for Email
            // OUTPUT LINE 1: printed by the worker thread running this call()
            System.out.println("Email Sent using Callable.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // OUTPUT LINE 2: this return value is stored in the Future.
        // The main thread retrieves it via et.get() and prints it separately.
        return "Email Sent";
    }
}

class SMSTaskCallable implements Callable<String> {
    public String call() throws Exception {
        try {
            Thread.sleep(4000); // simulate delay for SMS
            // OUTPUT LINE 1: printed by the worker thread running this call()
            System.out.println("SMS Sent using Callable.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // OUTPUT LINE 2: this return value is stored in the Future.
        // The main thread retrieves it via st.get() and prints it separately.
        return "SMS Sent";
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ExecutorThreads: demonstrates the two main ways to submit work to a pool —
//   execute() for fire-and-forget (no return value)
//   submit()  for tasks that return a result via Future
// ─────────────────────────────────────────────────────────────────────────────
class ExecutorThreads {
  public void runFunction() throws Exception {

    // ── PART 1: execute() — fire and forget (no return value) ────────────────
    // newFixedThreadPool(10): pre-creates 10 worker threads.
    // Submitted tasks run on those threads; no new thread is spawned per task.
    // If more than 10 tasks are submitted at once, extras wait in an internal queue.
    ExecutorService executorService = Executors.newFixedThreadPool(10);

    // Alternative: newCachedThreadPool() — creates threads on demand and reuses
    // idle ones. Good when task count is unpredictable and tasks are short-lived.
    // ExecutorService cachedExecutorService = Executors.newCachedThreadPool();

    // execute() accepts a Runnable (no return value).
    // Both calls return immediately — tasks run asynchronously in pool threads.
    // We don't get any handle to check completion or result.
    sendEmail(executorService, "karthik.hallad");
    sendEmail(executorService, "kumar.matcha");

    // shutdown() stops accepting new tasks. Already-submitted tasks finish normally.
    // Without this, the JVM won't exit because pool threads are non-daemon threads.
    executorService.shutdown();

    // ── PART 2: submit() — get a result back via Future ──────────────────────
    // submit() accepts a Callable<T> (or Runnable) and returns a Future<T>.
    // The Future is a "receipt" — it lets the main thread retrieve the result
    // (or any exception) once the worker thread finishes.
    ExecutorService executorService2 = Executors.newFixedThreadPool(10);

    // Both tasks are submitted and start running in parallel immediately.
    // sendEmailAndReturnEmail() returns a Future right away — it does NOT block.
    Future<String> emailResult1 = sendEmailAndReturnEmail(executorService2, "karthik.hallad");
    Future<String> emailResult2 = sendEmailAndReturnEmail(executorService2, "kumar.matcha");

    // get() BLOCKS the main thread until the task is done, then returns the result.
    // Both tasks were already running in parallel since submit() was called above,
    // so the total wait time is ~max(task1_time, task2_time), not sum of both.
    System.out.println(emailResult1.get());
    System.out.println(emailResult2.get());

    executorService2.shutdown();
  }

  // ── execute() variant: Runnable, no return value ─────────────────────────
  // Use this when you just want to fire a task and don't need to know its result.
  // execute() only accepts Runnable (or lambda that matches Runnable: () -> void).
  // It cannot accept a Callable because execute() has no way to store a return value.
  public static void sendEmail(ExecutorService executor, String recipient) {
    // Passing a lambda — the lambda body IS the Runnable.run() implementation.
    // Equivalent to: executor.execute(new EmailTaskRunnable());
    // For runnables, submit() also works: executor.submit(() -> { ... });
    // BUT execute() can NOT take a Callable — Callable.call() returns a value,
    // and execute() discards all return values (it's void).
    executor.execute(() -> {
        // Thread.currentThread().getName() shows which pool thread is running this task.
        // With a fixed pool of 10, you'll see names like "pool-N-thread-1", etc.
        System.out.println("Sending email to " + recipient + " on " + Thread.currentThread().getName());
        try {
            Thread.sleep(1000);  // simulate sending delay
        } catch (InterruptedException e) {
            // Best practice: re-set the interrupt flag so calling code can detect it.
            // Swallowing InterruptedException without this loses the interrupt signal.
            Thread.currentThread().interrupt();
        }
        System.out.println("Email sent to " + recipient);
        // No return — caller gets nothing back. Task result is lost after this point.
    });
    // execute() returns void immediately. The lambda runs asynchronously.
  }

  // ── submit() variant: Callable lambda, returns Future<String> ────────────
  // Use this when you need the result of the task.
  // submit() can accept BOTH Runnable and Callable.
  //   - submit(Runnable)  → returns Future<?>, get() returns null
  //   - submit(Callable<T>) → returns Future<T>, get() returns the actual value
  // The lambda here has a return statement → Java infers it as Callable<String>.
  public static Future<String> sendEmailAndReturnEmail(ExecutorService executor, String recipient) {
    // Equivalent to passing a named Callable: executor.submit(new EmailTaskCallable());
    return executor.submit(() -> {
      // This lambda body is Callable.call() — it runs on a pool thread, not main.
      System.out.println(
        "Sending email to " + recipient + " on " + Thread.currentThread().getName());
      try {
          Thread.sleep(1000);  // simulate sending delay
      } catch (InterruptedException e) {
          Thread.currentThread().interrupt();  // preserve interrupt status
      }
      // The returned String is wrapped inside the Future by the ExecutorService.
      // The main thread retrieves it by calling future.get().
      return "RETURN VALUE: Email sent to " + recipient;
    });
    // submit() returns the Future immediately without waiting for the task to finish.
    // The caller decides when to block by calling get().
  }
}

class Syncronization {
    public void run() {
      System.err.println("Running Syncronization to demonstrate threading...");
      Safety safety = new Safety();
      Thread t1 = new Thread(() -> {
        for (int i = 0; i < 1000; i++) {
          safety.increment();
          safety.incrementWithBlock();
          Safety.incrementStatic();
          safety.incrementAtomic();
          safety.incrementVolatile();
        }
      });
      Thread t2 = new Thread(() -> {
        for (int i = 0; i < 1000; i++) {
          safety.increment();
          safety.incrementWithBlock();
          Safety.incrementStatic();
          safety.incrementAtomic();
          if (i ==0 ) {
            safety.getVolatile();
          }
        }
      });
      t1.start();
      t2.start();
      try {
        t1.join();
        t2.join();
        System.out.println("Counter: " + safety.counter);
        System.out.println("Block Counter: " + safety.blockCounter);
        System.out.println("Static Counter: " + Safety.staticCounter);
        System.out.println("Atomic Counter: " + safety.atomicInteger.get());
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
}

class Safety {
  int counter = 0;
  int blockCounter = 0;
  static int staticCounter = 0;
  AtomicInteger atomicInteger = new AtomicInteger(0);
  volatile boolean volatileCounter = false; // volatile does NOT guarantee atomicity, only visibility
  // Instance level lock. Blocks
  synchronized void increment() {
    counter++;
  }
  // Class level lock. BLocks
  static synchronized void incrementStatic() {
    staticCounter++;
  }
  void incrementWithBlock() {
    // Block level lock. Blocks
    synchronized(this) {
      blockCounter++;
    }
  }
  void incrementAtomic() {
    // atomicInteger.getAndIncrement(); or
    // atomicInteger.compareAndExchange(counter, counter+1)
    // Manual method
    int prev, next;
    do {
      prev = atomicInteger.get();
      next = prev + 1;
    } while(!atomicInteger.compareAndSet(prev, next));
  }

  void incrementVolatile() {
    // This is NOT thread-safe. Two threads could read the same value
    // and both write back the same incremented value, losing one increment.
    volatileCounter = true;
  }
  void getVolatile() {
    // Point is some other set the counter to true, we immediately see that change.
    // Volatile ensures visibility of changes across threads.
    System.out.println(volatileCounter);
  }

}

// --------

class MutexThreads {
public void run(){
  System.err.println("Running Reintrant to demonstrate locks...");
  TicketBooking ticketBooking = new TicketBooking();
  Thread user1 = new Thread(() -> ticketBooking.bookTicket("User1"));
  Thread user2 = new Thread(() -> ticketBooking.bookTicket("User2"));
  user1.start();
  user2.start();
  try {
    user1.join();
    user2.join();
  } catch (InterruptedException e) {
    e.printStackTrace();
  }
  System.err.println("Expiring Reentrant Lock: Auto-Releasing Idle Threads");
  // shared expiring lock
  ExpiringReentrantLock expLock = new ExpiringReentrantLock();
  /* Idle user grabs the lock, simulates going idle for 5 s,
      then checks the flag and releases the lock */
  Thread idleUser = new Thread(() -> {
      if (expLock.tryLockWithTimeout(3000)) {
          System.out.println("IdleUser acquired lock, going idle...");

          // ── NEW WAY: Poll the volatile flag in a tight loop ────────────────
          // Instead of one big Thread.sleep(5000), we check isSessionActive()
          // every 200 ms. When the timer fires at t=3000ms and sets
          // isLocked=false, the NEXT iteration of this loop sees it immediately
          // (volatile guarantees visibility across threads) and breaks out.
          // The lock is released at ~t=3000ms, not at t=5000ms.
          //
          // WHY THIS IS BETTER THAN THE OLD WAY:
          // Old way — Thread.sleep(5000) + unlockSafely():
          //   The timer fired at t=3s and set isLocked=false, but nobody was
          //   watching. The owner thread was deep inside sleep(5000) and couldn't
          //   react. The lock was needlessly held for 2 extra seconds, blocking
          //   any other thread that wanted it. The volatile flag was written to
          //   but never READ — making it completely pointless.
          //
          // OLD WAY HAD ONE LEGITIMATE PURPOSE (as a design skeleton):
          //   It demonstrated that the TIMER cannot call unlock() itself (due to
          //   ReentrantLock owner-only unlock rule). The flag pattern showed the
          //   INTENT of cross-thread signalling — just without the polling receiver.
          //   It was a half-implemented pattern: the "signal" side existed but the
          //   "react" side didn't. This new code completes the other half.
          //
          // ANALOGY: The old way is like sending a text message to someone who
          // is asleep for 5 hours and won't check their phone. The message
          // (isLocked=false) is sent correctly, but there's no one awake to
          // read it and act on it until they naturally wake up.
          // ──────────────────────────────────────────────────────────────────
          long waited = 0;
          while (expLock.isSessionActive() && waited < 5000) {
              try {Thread.sleep(200);}catch(Exception e) {}   // check every 200 ms
              waited += 200;
          }
          // By the time we reach here, either:
          //   a) timer fired (t=3s) → isSessionActive() returned false → unlocks early
          //   b) 5000ms elapsed without timer (shouldn't happen here, but acts as
          //      a hard cap — avoids holding the lock forever if the timer misfires)
          expLock.unlockSafely();
      }
  }, "IdleUser");

  /* Active user starts after 1 s and keeps retrying every 1000 ms
      until the idle thread releases the lock */
  Thread activeUser = new Thread(() -> {
      while (true) {
          if (expLock.tryLockWithTimeout(3000)) {
              System.out.println("ActiveUser booked!");
              // Same polling pattern as idleUser above.
              // ActiveUser doesn't need a long session, so it will naturally
              // exit the loop quickly (either timer fires at 3s, or the 5s
              // hard cap is hit). This shows the SAME pattern works for any
              // lock owner — the loop is the universal "do work until timeout"
              // construct when using this expiring lock design.
              long waited = 0;
              while (expLock.isSessionActive() && waited < 5000) {
                  try {Thread.sleep(200);}catch(Exception e) {}   // check every 200 ms
                  waited += 200;
              }
              expLock.unlockSafely();
              break;
          } else {
              // tryLock() returned false — lock is currently held by another thread.
              // We back off for 1s and retry. This is a SPIN-WAIT with backoff:
              // avoids hammering tryLock() in a hot loop which wastes CPU.
              System.out.println("ActiveUser still waiting...");
              try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
          }
      }
  }, "ActiveUser");

  idleUser.start();
  activeUser.start();

  try {
      idleUser.join();
      activeUser.join();
  } catch (InterruptedException ignored) {}

  expLock.shutdown();
  // Semaphore:
  // used to guard access to a pool of resources (e.g. DB connections, API rate limits).
  TUFPlusAccount tufPlusAccount = new TUFPlusAccount(2);
  Thread u1 = new Thread(() -> {try{tufPlusAccount.login("karthik");}catch(Exception e){}});
  Thread u2 = new Thread(() -> {try{tufPlusAccount.login("kishan");}catch(Exception e){}});
  Thread u3 = new Thread(() -> {try{tufPlusAccount.login("mayur");}catch(Exception e){}});
  Thread u4 = new Thread(() -> {try{tufPlusAccount.login("mayur");}catch(Exception e){}});
  u1.start();u2.start();u3.start();
  try {Thread.sleep(3000);}catch(Exception e){}
  tufPlusAccount.logout("karthik");
  u4.start();
   try {
      u1.join();u2.join();u3.join();u4.join();
  } catch (InterruptedException ignored) {}

  System.out.println("Producer Consumer with syncronized and wait notify");
  // Producer consumer problem
  ProducerConsumer producerConsumer = new ProducerConsumer();
  Thread producer = new Thread(() -> {
    for (int i = 0; i < 10; i++) {
      producerConsumer.produce(i);
    }
  });
  Thread consumer = new Thread(() -> {
    for (int i = 0; i < 10; i++) {
      producerConsumer.consume();
    }
  });
  producer.start();
  consumer.start();
  try {
    producer.join();
    consumer.join();
  } catch (InterruptedException ignored) {}

  System.out.println("Producer Consumer with Locks");
  ProducerConsumerLock producerConsumerLock = new ProducerConsumerLock();
  Thread producer2 = new Thread(() -> {
    for (int i = 0; i < 10; i++) {
      producerConsumerLock.produce(i);
    }
  });
  Thread consumer2 = new Thread(() -> {
    for (int i = 0; i < 10; i++) {
      producerConsumerLock.consume();
    }
  });
  producer2.start();
  consumer2.start();
  try{
    producer2.join();
    consumer2.join();
  } catch (InterruptedException ignored) {}
}
}

class TicketBooking {
private int availableSeats = 1;
private final ReentrantLock lock = new ReentrantLock();

public void bookTicket(String user){
  // This thread has to perform unlock lock Unline go there
  // is no defer lock and Exceptions can be thrown anytime, so use
  // try catch block to ensure lock is released even if an exception occurs.
  // If the lock is held by another thread then the
  //  * current thread becomes disabled for thread scheduling
  //  * purposes and lies dormant until the lock has been acquired,
  //  * at which time the lock hold count is set to one.
  // If the current thread already holds the lock then the hold
  // * count is incremented by one and the method returns immediately.
  lock.lock();
  try {
    if (availableSeats > 0) {
      System.out.println(user + " booked a seat.");
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      availableSeats--;
    } else {
      System.out.println("No seats available for " + user);
    }
  } finally {
    lock.unlock();
  }
}
}

// Will allow to close the lock based on timeouts.
class ExpiringReentrantLock {
  private final ReentrantLock lock = new ReentrantLock();
  private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
  // volatile flag: set to true by the owner when the lock is acquired,
  // and cleared to false by either:
  //   a) the scheduled timer (after timeout) — signals the owner to release early
  //   b) the owner itself in unlockSafely() — normal release
  //
  // IMPORTANT — this flag is ONLY useful if the owner thread actively POLLS it.
  // The pattern is:
  //   owner acquires lock → starts a polling loop checking isLocked every N ms
  //   → when timer sets isLocked=false, the owner breaks the loop and calls unlock.
  // Without polling in the owner thread, the flag is set but never reacted to.
  private volatile boolean isLocked = false;

  // Exposes the flag for owner threads to poll in their work loop.
  public boolean isSessionActive() {
      return isLocked;
  }

  // Tries to acquire immediately; if successful,
  // schedules auto-unlock signal
  // If locked, simulate a session then unlock
  public boolean tryLockWithTimeout(long timeoutMills) {
    // tries locking and immediatly reverts back.
    boolean locked = lock.tryLock();
    // we can use this also to lock. this tries for 10 seconds
    // lock.tryLock(10, TimeUnit.SECONDS);
    if (locked){
      isLocked = true; // signal that a timed session is active
      // schedule a flag-clearing task after timeout
      // NOTE: the scheduler thread cannot call lock.unlock() directly
      // because ReentrantLock only permits the OWNER thread to unlock.
      // Instead, we clear the flag so the owner thread knows to release.
      executorService.schedule(() -> {
        if (isLocked) {
            System.out.println("Timeout reached – signalling owner to release.");
            isLocked = false; // owner thread will pick this up
        }
      }, timeoutMills, TimeUnit.MILLISECONDS);
      return locked;
    }
    return false;
  }

  // Called by the owner thread to release the lock.
  // If the timer already cleared the flag, this still performs cleanup.
  public void unlockSafely() {
      if (lock.isHeldByCurrentThread()) {
          isLocked = false;
          lock.unlock();
          System.out.println("Lock released by " + Thread.currentThread().getName());
      }
  }

  // Graceful shutdown for the scheduler
  public void shutdown() {
      executorService.shutdownNow();
  }

}


// Enforces a max-devices policy for a TUF+ account
class TUFPlusAccount {
  private final Semaphore semaphore;
  public TUFPlusAccount(int maxDevices) {
    this.semaphore = new Semaphore(maxDevices);
  }

  public boolean login(String user) throws InterruptedException {
    System.out.println("User " + user + " attempting to log in...");
    if(semaphore.tryAcquire(2, TimeUnit.SECONDS)){
      // wait up to 2 seconds to acquire a permit)
      System.out.println("User " + user + " logged in successfully.");
      return true;
    }
    System.out.println("User " + user + " failed to log in: max devices reached.");
    return false;
  }

  public void logout(String user){
    semaphore.release();
    System.out.println("User " + user + " logged out successfully.");
  }

}

class ProducerConsumer {
  Queue<Integer> buffer = new LinkedList<>();
  int capacity = 5;

  // synchronized ensures only ONE thread (producer or consumer) is active
  // inside these methods at a time. But synchronized ALONE is not enough.
  // WHY wait()/notifyAll() ARE STILL NECESSARY:
  // Consider: consumer enters consume(), buffer is empty.
  // Without wait(), the consumer would spin in the while loop HOLDING the lock.
  // The producer wants to enter produce() to add something, but it CAN'T —
  // it's blocked waiting for the lock the consumer already holds.
  // Consumer waits for data. Producer waits for the lock. → DEADLOCK.
  // wait() solves this by doing TWO things atomically:
  //   1. Releases the monitor lock  ← this is the magic part
  //   2. Suspends the calling thread
  // Now the producer can acquire the lock, produce an item, and call notifyAll().
  // notifyAll() wakes the consumer back up. The consumer re-acquires the lock
  // and re-checks the condition (while loop) before proceeding.
  // WHY while() instead of if():
  // wait() can return spuriously (JVM wakes a thread for no reason).
  // Also, if multiple consumers are waiting and one consumes the item,
  // the others should not proceed. The while loop re-checks the condition
  // after every wakeup — guarantee correctness regardless of spurious wakes.
  //
  // SUMMARY: synchronized = mutual exclusion (one at a time)
  //          wait()       = "I can't proceed, release the lock and sleep"
  //          notifyAll()  = "I changed shared state, wake up anyone waiting"
  //          All three work together — none can be removed.
  public synchronized void consume() {
    // Note that the while loop in the makeCoffee() and
    // drinkCoffee() methods protects against spurious
    // wake-ups and re-checks condition after every resume.
    while (buffer.size() == 0) {
      try{wait();}catch(Exception e){}
    }
    int num = buffer.poll();
    System.out.println("Consumed: " + num);
    // Notify producer in case it was sleeping waiting for buffer space.
    notifyAll();
  }
  public synchronized void produce(int num) {
    while (buffer.size() == capacity) {
      try{wait();}catch(Exception e){}
    }
    buffer.offer(num);
    System.out.println("Produced: " + num);
    // Notify consumer in case it was sleeping waiting for an item.
    notifyAll();
  }
}

class ProducerConsumerLock {
  Queue<Integer> buffer = new LinkedList<>();
  int capacity = 5;

  // ── WHY ReadWriteLock IS WRONG HERE ──────────────────────────────────────
  // ReadWriteLock allows MULTIPLE readers at the same time, one writer at a time.
  // It is designed for: many threads reading, few threads writing (e.g. a cache).
  //
  // Producer-consumer is NOT that pattern. Both producer and consumer mutate
  // the buffer (offer/poll). Two consumers holding the read lock simultaneously
  // could both see buffer.size() > 0, both proceed, then one gets null from
  // poll() → data corruption.
  //
  // Also: wait()/notify() are intrinsic monitor operations — they ONLY work
  // inside a synchronized block. Using them with ReadWriteLock would throw
  // IllegalMonitorStateException at runtime because no intrinsic monitor is held.
  //
  // ── THE CORRECT EXPLICIT-LOCK EQUIVALENT ─────────────────────────────────
  // ReentrantLock + Condition is the explicit API counterpart of
  // synchronized + wait/notify. Key advantage:
  //
  //   synchronized + notifyAll()  → wakes ALL waiting threads (both producers
  //                                  and consumers) even if only one side needs
  //                                  to be woken. Wasteful under heavy contention.
  //
  //   ReentrantLock + 2 Conditions → TWO separate wait queues:
  //     notFull:  only producers wait here; only consumers signal it.
  //     notEmpty: only consumers wait here; only producers signal it.
  //   Each side wakes ONLY the relevant waiter — more precise and efficient.
  //
  // condition.await()     ≡ wait()      — releases lock and suspends thread
  // condition.signalAll() ≡ notifyAll() — wakes threads waiting on THIS condition
  // ─────────────────────────────────────────────────────────────────────────
  private final ReentrantLock lock = new ReentrantLock();
  // WHY NOT lock.wait() instead of notEmpty.await()?
  //
  // Option A — wait() / this.wait():
  //   Requires holding the INTRINSIC monitor of 'this' (i.e. inside a
  //   'synchronized' block). We're not in synchronized — we hold a ReentrantLock.
  //   Calling wait() here throws IllegalMonitorStateException at runtime.
  //
  // Option B — lock.wait():
  //   'lock' is a ReentrantLock object. lock.wait() would require holding
  //   the INTRINSIC monitor of the lock object itself (i.e. synchronized(lock)).
  //   Even if you did that, it would NOT release the ReentrantLock you acquired
  //   with lock.lock(). The producer tries to call lock.lock() — still taken —
  //   deadlock. lock.wait() has NO knowledge of the ReentrantLock internals.
  // Lock system	     How you acquire it	    How you wait on it
  // Intrinsic monitor (every Object has one)	synchronized(lock) { }	lock.wait()
  // ReentrantLock     (explicit API)	        lock.lock()	condition.await()
  // BASICALLY CALLING lock.wait() waits on the monitor of the object 'lock',
  // which is NOT the same as the ReentrantLock's internal mechanism for locking.
  // No, you would never legitimately call lock.wait() on a
  // ReentrantLock object. It would only "work" if you structured your
  // entire concurrency around synchronized(lock) { lock.wait(); } — which
  // means you're ignoring the ReentrantLock API entirely and using the lock object
  // purely as a plain monitor. At that point, why create a ReentrantLock at all?
  // You'd just use synchronized on any plain Object.
  //
  // Option C — notEmpty.await() ✓ (correct):
  //   Condition objects are created by a specific ReentrantLock:
  //   lock.newCondition(). This permanently binds notEmpty to 'lock'.
  //   await() internally knows to release THAT ReentrantLock and suspend
  //   the thread atomically — exactly what wait() does for synchronized.
  //
  // One-liner rule:
  //   wait()         → releases intrinsic monitor (synchronized)
  //   lock.wait()    → releases intrinsic monitor of the lock OBJECT — wrong lock
  //   condition.await() → releases the ReentrantLock that created this condition ✓
  private final Condition notFull  = lock.newCondition(); // producer waits here when buffer full
  private final Condition notEmpty = lock.newCondition(); // consumer waits here when buffer empty

  public void consume() {
    lock.lock(); // acquire the single mutual-exclusion lock
    try {
      while (buffer.size() == 0) {
        // Buffer empty — can't consume.
        // await() atomically: releases the lock AND suspends this thread.
        // Producer can now acquire the lock and add an item.
        // When producer calls notEmpty.signalAll(), this thread wakes up,
        // re-acquires the lock, and re-checks the while condition.
        notEmpty.await();
      }
      int num = buffer.poll();
      System.out.println("Consumed: " + num);
      // A slot just freed — wake any producers waiting on notFull.
      // Only producers are waiting on notFull, so no consumers are needlessly woken.
      notFull.signalAll();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // preserve interrupt status
    } finally {
      lock.unlock(); // always release in finally to avoid lock leaks
    }
  }

  public void produce(int num) {
    lock.lock();
    try {
      while (buffer.size() == capacity) {
        // Buffer full — can't produce.
        // await() releases the lock so a consumer can drain an item.
        notFull.await();
      }
      buffer.offer(num);
      System.out.println("Produced: " + num);
      // An item is now available — wake any consumers waiting on notEmpty.
      // Only consumers are waiting on notEmpty, so no producers are needlessly woken.
      notEmpty.signalAll();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } finally {
      lock.unlock();
    }
  }
}
// thread calls wait()
//   └─ 1. atomically releases the monitor lock
//   └─ 2. thread suspends (parked)
//   └─ 3. notifyAll() wakes it
//   └─ 4. thread moves to "waiting to re-acquire lock" queue
//   └─ 5. fights for the lock against other threads (may block here)
//   └─ 6. lock re-acquired ← happens INSIDE wait(), before it returns
// wait() returns ← you're back in your code, lock is already held again