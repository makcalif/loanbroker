package com.reactive.paradigm.loanbroker;

import com.reactive.paradigm.loanbroker.service.WebClientTransportImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class LoanbrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanbrokerApplication.class, args);

	}

	@Component
	private class MyRunner implements CommandLineRunner {

		@Autowired
		WebClientTransportImpl webClientTransport;

		@Override
		public void run(String... args) throws Exception {
			webClientTransport.doGet();
		}
	}



}

