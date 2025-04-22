package com.miniinfomates2003.asset_management.repositories;

import com.miniinfomates2003.asset_management.entities.Activo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.miniinfomates2003.asset_management.entities.Categoria;
import java.util.List;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    List<Categoria> findByIdCuenta(Integer idCuenta);
}

