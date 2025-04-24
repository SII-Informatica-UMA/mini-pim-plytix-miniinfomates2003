package com.miniinfomates2003.asset_management.services;

import com.miniinfomates2003.asset_management.controllers.Mapper;
import com.miniinfomates2003.asset_management.dtos.CategoriaDTO;
import com.miniinfomates2003.asset_management.entities.Categoria;
import com.miniinfomates2003.asset_management.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Optional<Categoria> obtenerPorCategoria(Integer idCategoria) {
        return categoriaRepository.findById(idCategoria);
    }

    public List<Categoria> obtenerPorCuenta(Integer idCuenta) {
        return categoriaRepository.findByIdCuenta(idCuenta);
    }
}
