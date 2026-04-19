package com.karthik.lld_basics.mediator;

import java.util.ArrayList;
import java.util.List;

public class Mediator {
  public void run(){
    // 1 instance of document manager for each document.
    // now add to this document user2 and user3.
    // assume user1 is the owner of the document. He is automatically added to the document.
    DocumentManager documentManager = new DocumentManager();
    User user1 = new User("User1", documentManager);
    User user2 = new User("User2", documentManager);
    User user3 = new User("User3", documentManager);
    // automatically add user1 to the document.
    documentManager.join(user1);
    // user1 adds user2
    documentManager.join(user2);
    // user1 adds user3
    documentManager.join(user3);

    // user2 edits the document
    user2.sendMessage("Hello, I have edited the document.");
    // user1 replies back, say
    user1.sendMessage("Hello, I have seen your edit. I will review it and get back to you.");
    // mediator also helps in finer grained control
    // i.e sending only to specific users or owner.
  }
}

class User {
  String name;
  DocumentManager documentManager;
  User(String name, DocumentManager documentManager){
    this.name = name;
    this.documentManager = documentManager;
  }

  void sendMessage(String message){
    documentManager.sendMessage(this, message);
  }

  void recieveMessage(String message, User sender){
    System.out.println(name + " received message from " + sender.name + ": " + message);
  }

}


// if a adds b and c then its same b adding a and c
// so instead of add user we use join, collaborative pool esentially.
class DocumentManager {
  List<User> users;
  DocumentManager(){
    users = new ArrayList<>();
  }

  void join(User user){
    users.add(user);
  }

  void sendMessage(User sender, String message){
    for(User user : users){
      if(!user.name.equals(sender.name)){
        user.recieveMessage(message, sender);
      }
    }
  }

}