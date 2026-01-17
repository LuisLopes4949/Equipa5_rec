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

    public List<Categoria> listarPorUtilizador(Long userId) {
        return repository.findByUtilizadorIdOrGlobal(userId);
    }

    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    public Categoria criar(Categoria categoria, Long userId) {
        if (userId != null) {
            Utilizador u = utilizadorService.buscarPorId(userId);
            categoria.setUtilizador(u); 
        }
        categoria.setAtiva(true); // Garante que nasce ativa
        return repository.save(categoria);
    }

    // --- NOVO MÉTODO: SOFT DELETE ---
    public void eliminar(Long id, Long userId) {
        Categoria c = buscarPorId(id);

        // Segurança: Só podes apagar as tuas categorias. Não podes apagar as globais nem as de outros.
        if (c.getUtilizador() == null) {
            throw new RuntimeException("Não podes apagar categorias globais do sistema.");
        }
        if (!c.getUtilizador().getId().equals(userId)) {
            throw new RuntimeException("Esta categoria não te pertence.");
        }

        // Soft Delete: Em vez de delete(), fazemos update para false
        c.setAtiva(false);
        repository.save(c);
    }
}