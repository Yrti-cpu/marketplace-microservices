package org.yrti.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MarketplaceOrderServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MarketplaceOrderServiceApplication.class, args);
  }

}
