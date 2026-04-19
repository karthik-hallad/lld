package com.karthik.lld_basics.template;

public class Template {
  public void run(){
    EmailNotification emailNotification = new EmailNotification();
    SMSNotification smsNotification = new SMSNotification();
    emailNotification.template("john@example.com", "Hello, how are you?");
    smsNotification.template("9876543210", "Hello, how are you?");
  }
}

abstract class NotificationService{
  // Final template method, it is assume steps are same
  // so this is the steps.
  public final void template(String to, String message){
    // step1
    rateLimitCheck(to);
    validateRecipient(to);
    String formatted = formatMessage(message);
    //step2
    String composedMessage = composeMessage(formatted);
    sendMessage(to, composedMessage);
    postSendAnalytics(to);
  }

  // Concrete operations
  private void rateLimitCheck(String to){
    System.out.println("Checking rate limit for: " + to);
  }

  private void validateRecipient(String to){
    System.out.println("Validating recipient: " + to);
  }

  private String formatMessage(String message){
    return message.trim();
  }

  // Primitive operations
  protected abstract String composeMessage(String formattedMessage);
  protected abstract void sendMessage(String to, String message);

  // hooks
  protected void postSendAnalytics(String to){
    System.out.println("Post send analytics for: " + to);
  }

}

class EmailNotification extends NotificationService{
  @Override
  protected String composeMessage(String formattedMessage){
    return "<html><body><p>" + formattedMessage + "</p></body></html>";
  }
  @Override
  protected void sendMessage(String to, String message){
    System.out.println("Sending EMAIL to " + to + " with content:\n" + message);
  }
}

class SMSNotification extends NotificationService{
  @Override
  protected String composeMessage(String formattedMessage){
    return "[SMS] " + formattedMessage;
  }
  @Override
  protected void sendMessage(String to, String message){
    System.out.println("Sending SMS to " + to + " with message: " + message);
  }
}