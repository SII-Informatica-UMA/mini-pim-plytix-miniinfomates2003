package com.miniinfomates2003.asset_management;

import com.miniinfomates2003.asset_management.entities.Activo;
import com.miniinfomates2003.asset_management.entities.Categoria;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class AssetManagementApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(AssetManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Crear instancias de Categoria y Activo
		Categoria cat1 = new Categoria();
		cat1.setId(1);
		cat1.setNombre("Categoria 1");

		Activo act1 = new Activo();
		act1.setId(1);
		act1.setNombre("Activo 1");

		// Asignar categoria a activo
		act1.setCategorias(Set.of(cat1));

		// Asignar activo a categoria
		cat1.setActivos(Set.of(act1));

		// Verificar la relación
		System.out.println("Activo: " + act1.getNombre() + " tiene la categoría: " + act1.getCategorias().iterator().next().getNombre());
	}
}