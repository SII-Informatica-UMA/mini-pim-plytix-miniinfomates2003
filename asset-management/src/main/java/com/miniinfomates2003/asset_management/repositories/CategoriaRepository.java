package com.miniinfomates2003.asset_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miniinfomates2003.asset_management.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}

