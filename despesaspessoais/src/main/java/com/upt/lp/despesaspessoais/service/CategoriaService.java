package com.upt.lp.despesaspessoais.service;

import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.repository.CategoriaRepository;
import com.upt.lp.despesaspessoais.repository.DespesasRepository; // Importante
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {
    private final CategoriaRepository repository;
    private final DespesasRepository despesaRepository; // Precisamos disto para verificar uso

    public CategoriaService(CategoriaRepository repository, DespesasRepository despesaRepository) {
        this.repository = repository;
        this.despesaRepository = despesaRepository;
    }

    public List<Categoria> listarTodas() { return repository.findAll(); }
    public Categoria criar(Categoria c) { return repository.save(c); }
    
    public Categoria buscarPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    // --- NOVO: Editar Categoria [cite: 2420] ---
    public Categoria editar(Long id, Categoria novosDados) {
        Categoria cat = buscarPorId(id);
        cat.setNome(novosDados.getNome());
        cat.setNomeIcone(novosDados.getNomeIcone());
        cat.setCorHex(novosDados.getCorHex());
        return repository.save(cat);
    }

    // --- NOVO: Apagar Categoria com Regra de Proteção [cite: 2421, 2422] ---
    public void apagar(Long id) {
        // Regra da Equipa: Se tem despesas, NÃO apaga.
        if (despesaRepository.existsByCategoriaId(id)) {
            throw new RuntimeException("Não é possível apagar: Esta categoria já tem despesas associadas!");
        }
        repository.deleteById(id);
    }
}