package com.example.accessingdatajpa;

import com.example.accessingdatajpa.datasources.OracleShardingDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class AccessingDataJpaApplication {
	@Bean
	DataSource dataSource() {
		OracleShardingDataSource dataSource = new OracleShardingDataSource();
		dataSource.setProxyDataSource(
				DataSourceBuilder.create().url("jdbc:h2:mem:default").driverClassName("org.h2.Driver").build()
		);
		return dataSource;
	}

	private static final Logger log = LoggerFactory.getLogger(AccessingDataJpaApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataJpaApplication.class);
	}

	@Bean
	public CommandLineRunner demo(PersonRepository repository) {
		return (args) -> {
			// save a few customers
//			repository.save(new Person("Jack", "Bauer"));
//			repository.save(new Person("Chloe", "O'Brian"));
//			repository.save(new Person("Kim", "Bauer"));
//			repository.save(new Person("David", "Palmer"));
//			repository.save(new Person("Michelle", "Dessler"));
//
//			// fetch all customers
//			log.info("Customers found with findAll():");
//			log.info("-------------------------------");
//			for (Person customer : repository.findAll()) {
//				log.info(customer.toString());
//			}
//			log.info("");
//
//			// fetch an individual customer by ID
//			Person customer = repository.findById(1L);
//			log.info("Customer found with findById(1L):");
//			log.info("--------------------------------");
//			log.info(customer.toString());
//			log.info("");
//
//			// fetch customers by last name
//			log.info("Customer found with findByLastName('Bauer'):");
//			log.info("--------------------------------------------");
//			repository.findByLastName("Bauer").forEach(bauer -> {
//				log.info(bauer.toString());
//			});
//			// for (Customer bauer : repository.findByLastName("Bauer")) {
//			// 	log.info(bauer.toString());
//			// }
//			log.info("");
		};
	}

}
