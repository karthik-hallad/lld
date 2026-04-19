package com.karthik.lld_basics.adapter;

public class LoggingExample {
  public void runApplicationServices(){
    SampleApplication sampleApplication = new SampleApplication(new NextGenCloudTelemetryAdapter());
    sampleApplication.run();
    SampleApplication sampleApplication2 = new SampleApplication(new LegacySyslogDaemonAdapter());
    sampleApplication2.run();
  }
}


// consider
// Library A: A legacy system that requires a priority integer, a source string, and a byte array.
class LegacySyslogDaemon {
  public void writeLog(int priorityCode, CharSequence source, byte[] rawData) {
      System.out.println("[LEGACY SYSTEM - Priority: " + priorityCode + "] "
                         + source + " -> " + new String(rawData));
  }
}

// Library B: A modern cloud service that requires a stream name, timestamp, and JSON payload.
class NextGenCloudTelemetry {
  public void emitTelemetry(String logStream, long timestampMs, String jsonPayload) {
       System.out.println("[CLOUD TELEMETRY - " + timestampMs + "] Stream: "
                          + logStream + " | Payload: " + jsonPayload);
  }
}

// My interface logging
// Remember the dependency principle that we should depend on an interface,
// not on a low-level class, and that's what we are trying to do here.
interface Logger {
  void logInfo(String message);
}

// Usually, we initialize a logger while doing construction. That makes sense, right?
class SampleApplication {
  Logger logger;
  SampleApplication(Logger logger){
    this.logger = logger;
  }

  void run(){
    logger.logInfo("Hey i am logging a message");
  }
}

class LegacySyslogDaemonAdapter implements Logger {
  LegacySyslogDaemon legacySyslogDaemon;
  LegacySyslogDaemonAdapter(){
    this.legacySyslogDaemon = new LegacySyslogDaemon();
  }

  public void logInfo(String message) {
    // 1 for info
    this.legacySyslogDaemon.writeLog(1, "ApplicationTarget", message.getBytes());
  }
}

class NextGenCloudTelemetryAdapter implements Logger {

  NextGenCloudTelemetry nextGenCloudTelemetry;
  NextGenCloudTelemetryAdapter(){
    this.nextGenCloudTelemetry = new NextGenCloudTelemetry();
  }

  public void logInfo(String message) {
    String json = "{\"level\":\"INFO\", \"message\":\"" + message + "\"}";
    this.nextGenCloudTelemetry.emitTelemetry("INFO", System.currentTimeMillis(), message);
  }
}