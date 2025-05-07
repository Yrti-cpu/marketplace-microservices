package org.yrti.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MarketplaceGatewayServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MarketplaceGatewayServiceApplication.class, args);
  }

}
