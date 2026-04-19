package com.karthik.lld_basics.command;

import java.util.Stack;

public class CommandPattern {
  public void run(){
    // create a command to turn on the light
    Light light = new Light();
    AC ac = new AC();
    RemoteControl remote = new RemoteControl();
    remote.setCommand(0, new TurnOnCommand(light));
    remote.setCommand(1, new TurnOffCommand(light));
    remote.setCommand(2, new TurnOnACCommand(ac));
    remote.setCommand(3, new TurnOffACCommand(ac));
    remote.pressButton(0);
    remote.pressButton(2);
    remote.pressButton(1);
    remote.pressUndo();
    remote.pressUndo();
  }
}

class Light {
  public void on(){
    System.out.println("Light is on");
  }
  public void off(){
    System.out.println("Light is off");
  }
}
class AC {
  public void on(){
    System.out.println("AC is on");
  }
  public void off(){
    System.out.println("AC is off");
  }
}

// we need a way of converting a command
// to a class. every command will execute or undo.
interface Command {
  void execute();
  void undo();
}

class TurnOnCommand implements Command {
  private Light light;
  TurnOnCommand(Light light){
    this.light = light;
  }
  public void execute(){
    light.on();
  }
  public void undo(){
    light.off();
  }
}

class TurnOffCommand implements Command {
  private Light light;
  TurnOffCommand(Light light){
    this.light = light;
  }
  public void execute(){
    light.off();
  }
  public void undo(){
    light.on();
  }
}

class TurnOnACCommand implements Command {
  private AC ac;
  TurnOnACCommand(AC ac){
    this.ac = ac;
  }
  public void execute(){
    ac.on();
  }
  public void undo(){
    ac.off();
  }
}

class TurnOffACCommand implements Command {
  private AC ac;
  TurnOffACCommand(AC ac){
    this.ac = ac;
  }
  public void execute(){
    ac.off();
  }
  public void undo(){
    ac.on();
  }
}

class RemoteControl {
  private Command[] buttons = new Command[10];
  private Stack<Command> commandHistory = new Stack<>();

  public void setCommand(int slot, Command command){
    buttons[slot] = command;
  }

  public void pressButton(int slot){
    if(slot >= 10 && buttons[slot] == null){
      System.out.println("No command assigned to slot " + slot);
    } else {
      buttons[slot].execute();
      commandHistory.push(buttons[slot]);
    }
  }

  public void pressUndo(){
    if(commandHistory.isEmpty()){
      System.out.println("No commands to undo");
    } else {
      commandHistory.pop().undo();
    }
  }
}