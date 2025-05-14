package org.yrti.order.exception;

public class ClientRequestException extends RuntimeException {

  public ClientRequestException(String clientName, String message) {
    super(clientName + ": " + message);

  }
}
