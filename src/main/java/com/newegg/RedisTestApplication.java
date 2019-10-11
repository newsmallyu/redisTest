package com.newegg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RedisTestApplication {

	public static void main(String[] args) {
		System.setProperty("http.proxyHost", "s1firewall");
		System.setProperty("http.proxyPort", "8080");
		System.setProperty("https.proxyHost", "s1firewall");
		System.setProperty("https.proxyPort", "8080");
		SpringApplication.run(RedisTestApplication.class, args);
	}

}
