package com.upt.lp.despesaspessoais.service;

import com.upt.lp.despesaspessoais.entity.Despesas;
import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.repository.DespesasRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DespesasService {
    private final DespesasRepository repository;
    private final UtilizadorService utilizadorService;
    private final CategoriaService categoriaService;

    public DespesasService(DespesasRepository repository, UtilizadorService uService, CategoriaService cService) {
        this.repository = repository;
        this.utilizadorService = uService;
        this.categoriaService = cService;
    }

    public List<Despesas> listarPorUtilizador(Long userId) {
        return repository.findByUtilizadorId(userId);
    }

    public Despesas criarDespesa(Despesas despesa, Long userId, Long catId) {
        // Validação: Valor positivo 
        if (despesa.getValor() == null || despesa.getValor() <= 0) {
            throw new IllegalArgumentException("O valor deve ser numérico positivo.");
        }
        // Validação: Campos obrigatórios [cite: 2403]
        if (despesa.getData() == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }

        Utilizador u = utilizadorService.buscarPorId(userId);
        Categoria c = categoriaService.buscarPorId(catId);
        despesa.setUtilizador(u);
        despesa.setCategoria(c);

        return repository.save(despesa);
    }

    // --- NOVO: Editar Despesa [cite: 2414] ---
    public Despesas editarDespesa(Long id, Despesas dadosNovos, Long catId) {
        Despesas existente = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        // Atualizar campos 
        existente.setDescricao(dadosNovos.getDescricao());
        existente.setValor(dadosNovos.getValor());
        existente.setData(dadosNovos.getData());
        existente.setMetodoPagamento(dadosNovos.getMetodoPagamento());

        // Atualizar categoria se necessário
        if (catId != null) {
            Categoria c = categoriaService.buscarPorId(catId);
            existente.setCategoria(c);
        }

        return repository.save(existente);
    }
    
    // --- NOVO: Filtros --- 
    
    public List<Despesas> filtrarPorAno(Long userId, int ano) {
        LocalDate inicio = LocalDate.of(ano, 1, 1);
        LocalDate fim = LocalDate.of(ano, 12, 31);
        return repository.findByUtilizadorIdAndDataBetween(userId, inicio, fim);
    }

    public List<Despesas> filtrarPorCategoria(Long userId, Long catId) {
        return repository.findByUtilizadorIdAndCategoriaId(userId, catId);
    }
    
    public List<Despesas> filtrarPorValor(Long userId, Double min, Double max) {
        return repository.findByUtilizadorIdAndValorBetween(userId, min, max);
    }

    // --- NOVO: Estatísticas --- 
    // Retorna um Mapa: "Transporte" -> 150.0, "Alimentação" -> 200.0
    public Map<String, Double> getTotaisPorCategoria(Long userId) {
        List<Object[]> resultados = repository.somarDespesasPorCategoria(userId);
        
        // Converte a lista do banco num Mapa fácil de ler
        return resultados.stream()
            .collect(Collectors.toMap(
                obj -> (String) obj[0], // Nome da Categoria
                obj -> (Double) obj[1]  // Total Gasto
            ));
    }
}