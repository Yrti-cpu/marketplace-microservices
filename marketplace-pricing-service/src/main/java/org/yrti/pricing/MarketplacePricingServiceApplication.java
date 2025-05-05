package org.yrti.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class MarketplacePricingServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(MarketplacePricingServiceApplication.class, args);
  }

}
