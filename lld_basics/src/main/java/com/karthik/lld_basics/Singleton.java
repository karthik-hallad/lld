package com.karthik.lld_basics;
public class Singleton {
  // Eager
  // private static final Singleton singleton = new Singleton();

  // // private const
  // private Singleton(){

  // }

  // public static Singleton getInstance(){
  //   return singleton
  // }

  // Lazy
  private static Singleton singleton;
  private Singleton(){

  }

  // syncronized ensures thread safety
  // public static synchronized Singleton  getInstance(){
  //   // Thread safety forcreation
  //   if (singleton == null){
  //     singleton = new Singleton();
  //   }
  //   return singleton;
  // }

  // easy without using language
  public static Singleton getInstance(){
    if (singleton == null){
      // just acuquie a lock in other languages.
      synchronized (Singleton.class) {
        if (singleton == null){
          singleton = new Singleton();
        }
      }
    }
    return singleton;
  }
}
