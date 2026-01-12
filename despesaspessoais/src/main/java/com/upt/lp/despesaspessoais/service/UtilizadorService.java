package com.upt.lp.despesaspessoais.service;

import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UtilizadorService {
    private final UtilizadorRepository repository;

    public UtilizadorService(UtilizadorRepository repository) {
        this.repository = repository;
    }

    public List<Utilizador> listarTodos() { return repository.findAll(); }
    
    public Utilizador criar(Utilizador u) { return repository.save(u); }
    
    public Utilizador buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    }
}