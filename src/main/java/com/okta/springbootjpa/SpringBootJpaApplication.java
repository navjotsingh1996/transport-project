package com.okta.springbootjpa;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.stream.Stream;

@SpringBootApplication
public class SpringBootJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootJpaApplication.class, args);
	}
	
}
