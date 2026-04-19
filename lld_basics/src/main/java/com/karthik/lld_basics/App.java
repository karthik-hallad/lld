package com.karthik.lld_basics;

import com.karthik.lld_basics.factory.Factory;
import com.karthik.lld_basics.builder.Builder;
import com.karthik.lld_basics.chainofresp.ChainOfResp;
import com.karthik.lld_basics.abstractpattern.Abstract;
import com.karthik.lld_basics.adapter.Adapter;
import com.karthik.lld_basics.adapter.LoggingExample;
import com.karthik.lld_basics.decoratorpattern.DecoratorPattern;
import com.karthik.lld_basics.facade.Facade;
import com.karthik.lld_basics.composite.Composite;
import com.karthik.lld_basics.proxy.Proxy;
import com.karthik.lld_basics.bridge.Bridge;
import com.karthik.lld_basics.iterator.IteratorOld;
import com.karthik.lld_basics.iterator.RefinedIterator;
import com.karthik.lld_basics.behaviour.Behaviour;
import com.karthik.lld_basics.strategy.Strategy;
import com.karthik.lld_basics.command.CommandPattern;
import com.karthik.lld_basics.template.Template;
import com.karthik.lld_basics.state.State;
import com.karthik.lld_basics.mediator.Mediator;
import com.karthik.lld_basics.threads.Threads;
/**
 * Hello world!
 *
 */
public class App
{
    public static void logPattern(String patternName){
        System.out.println("--------------------------------");
        System.out.println(patternName);
        System.out.println("--------------------------------");
    }
    public static void main( String[] args )
    {
        // Singleton
        logPattern("Singleton Pattern");
        System.out.println(Singleton.getInstance());
        System.out.println(Singleton.getInstance());
        // Factory
        logPattern("Factory Pattern");
        Factory factory = new Factory();
        factory.run();
        // Builder
        logPattern("Builder Pattern");
        Builder builder = new Builder();
        builder.run();
        // Abstract Pattern
        logPattern("Abstract Pattern");
        Abstract abstractPattern = new Abstract();
        abstractPattern.run();
        // Adapter
        logPattern("Adapter Pattern");
        Adapter adapter = new Adapter();
        adapter.run();
        // Adapter logging example
        logPattern("Logging Example");
        LoggingExample loggingExample = new LoggingExample();
        loggingExample.runApplicationServices();
        // Decorator Pattern
        logPattern("Decorator Pattern");
        DecoratorPattern decoratorPattern = new DecoratorPattern();
        decoratorPattern.run();
        // Facade Pattern
        logPattern("Facade Pattern");
        Facade facade = new Facade();
        facade.run();
        // Composite Pattern
        logPattern("Composite Pattern");
        Composite composite = new Composite();
        composite.run();
        // Proxy Pattern
        logPattern("Proxy Pattern");
        Proxy proxy = new Proxy();
        proxy.run();
        // Bridge Pattern
        logPattern("Bridge Pattern");
        Bridge bridge = new Bridge();
        bridge.run();
        // Iterator Pattern
        logPattern("Iterator Pattern");
        IteratorOld iterator = new IteratorOld();
        iterator.run();
        // Refined Iterator Pattern
        logPattern("Refined Iterator Pattern");
        RefinedIterator refinedIterator = new RefinedIterator();
        refinedIterator.run();
        // Behaviour Pattern
        logPattern("Behaviour Pattern");
        Behaviour behaviour = new Behaviour();
        behaviour.run();
        // Strategy Pattern
        logPattern("Strategy Pattern");
        Strategy strategy = new Strategy();
        strategy.run();
        // Command Pattern
        logPattern("Command Pattern");
        CommandPattern commandPattern = new CommandPattern();
        commandPattern.run();
        // Template Pattern
        logPattern("Template Pattern");
        Template template = new Template();
        template.run();
        // State Pattern
        logPattern("State Pattern");
        State state = new State();
        state.run();
        // Chain of Responsibility Pattern
        logPattern("Chain of Responsibility Pattern");
        ChainOfResp chainOfResp = new ChainOfResp();
        chainOfResp.run();
        // Mediator Pattern
        logPattern("Mediator Pattern");
        Mediator mediator = new Mediator();
        mediator.run();
        // Threads
        logPattern("Threads");
        Threads threads = new Threads();
        threads.run();
    }
}
