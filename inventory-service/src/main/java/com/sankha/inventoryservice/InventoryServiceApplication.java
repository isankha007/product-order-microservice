package com.sankha.inventoryservice;

import com.sankha.inventoryservice.entity.Inventory;
import com.sankha.inventoryservice.inventory.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);

	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return  args -> {
			Inventory inventory=new Inventory();
			inventory.setSkuCode("iphone_13");
			inventory.setQuantity(100);

			Inventory inventory2=new Inventory();
			inventory2.setSkuCode("iphone_13_red");
			inventory2.setQuantity(100);

			Inventory inventory3=new Inventory();
			inventory2.setSkuCode("macbook");
			inventory2.setQuantity(1002);

			inventoryRepository.save(inventory2);
			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory3);
		};
	}
}
