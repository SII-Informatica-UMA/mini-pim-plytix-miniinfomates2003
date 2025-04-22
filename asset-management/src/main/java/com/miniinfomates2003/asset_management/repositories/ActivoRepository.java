package com.miniinfomates2003.asset_management.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miniinfomates2003.asset_management.entities.Activo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivoRepository extends JpaRepository<Activo, Integer> {
    List<Activo> findByIdCuenta(Integer idCuenta);

    List<Activo> findByIdProductosContaining(Integer idProducto);

    @Query("SELECT a FROM Activo a JOIN a.categorias c WHERE c.id = :idCategoria")
    List<Activo> findByIdCategoria(@Param("idCategoria") Integer idCategoria);
}
