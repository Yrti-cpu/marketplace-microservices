package org.yrti.order.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.yrti.order.exception.ClientRequestException;

@Component
@Slf4j
public class ClientResponseHandle {

  public <T> T handleResponse(ResponseEntity<T> response, String serviceName) {
    log.debug("Received response from {} with status: {}", serviceName, response.getStatusCode());

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      String errorDetails = response.getBody() != null ?
          response.getBody().toString() : "Empty response body";
      throw new ClientRequestException(serviceName, errorDetails);
    }

    return response.getBody();
  }

}
