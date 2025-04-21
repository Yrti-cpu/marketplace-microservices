package org.yrti.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarketplaceEventsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarketplaceEventsApplication.class, args);
	}

}

// TODO-Крит Странный модуль. По сути содержит только DTO-шки. Зачем ему @SpringBootApplication ?