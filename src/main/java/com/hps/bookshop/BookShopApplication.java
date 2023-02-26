package com.hps.bookshop;

import com.hps.bookshop.service.file.FileStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BookShopApplication implements CommandLineRunner {
	@Resource
	private FileStorageService fileStorageService;

	public static void main(String[] args) {
		SpringApplication.run(BookShopApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		fileStorageService.init();
	}
}
