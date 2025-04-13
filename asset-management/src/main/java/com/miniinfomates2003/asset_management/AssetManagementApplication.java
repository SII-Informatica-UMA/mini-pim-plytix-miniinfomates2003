package com.miniinfomates2003.asset_management;

import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.entities.Categoria;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class AssetManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssetManagementApplication.class, args);
	}
}