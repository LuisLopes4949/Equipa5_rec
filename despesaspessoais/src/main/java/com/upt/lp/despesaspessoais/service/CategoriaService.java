package com.upt.lp.despesaspessoais.service;

import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;
    private final UtilizadorService utilizadorService; 

    public CategoriaService(CategoriaRepository repository, UtilizadorService uService) {
        this.repository = repository;
        this.utilizadorService = uService;
    }

    public List<Categoria> listarTodas() {
        return repository.findAll();
    }

    // Método que usa a Query especial
    public List<Categoria> listarPorUtilizador(Long userId) {
        return repository.findByUtilizadorIdOrGlobal(userId);
    }

    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    // Ao criar, associamos o Utilizador
    public Categoria criar(Categoria categoria, Long userId) {
        if (userId != null) {
            Utilizador u = utilizadorService.buscarPorId(userId);
            categoria.setUtilizador(u); 
        }
        return repository.save(categoria);
    }
}