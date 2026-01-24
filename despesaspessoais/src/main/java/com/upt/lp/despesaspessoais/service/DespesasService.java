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
        return repository.findByUtilizadorIdAndAtivaTrue(userId);
    }

    public Despesas criarDespesa(Despesas despesa, Long userId, Long catId) {
        if (despesa.getValor() == null || despesa.getValor() <= 0) {
            throw new IllegalArgumentException("O valor deve ser numérico positivo.");
        }
        if (despesa.getData() == null) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }

        Utilizador u = utilizadorService.buscarPorId(userId);
        Categoria c = categoriaService.buscarPorId(catId);
        despesa.setUtilizador(u);
        despesa.setCategoria(c);
        despesa.setAtiva(true); 

        return repository.save(despesa);
    }

    public Despesas editarDespesa(Long id, Despesas dadosNovos, Long catId) {
        Despesas existente = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        existente.setDescricao(dadosNovos.getDescricao());
        existente.setValor(dadosNovos.getValor());
        existente.setData(dadosNovos.getData());
        existente.setMetodoPagamento(dadosNovos.getMetodoPagamento());

        if (catId != null) {
            Categoria c = categoriaService.buscarPorId(catId);
            existente.setCategoria(c);
        }

        return repository.save(existente);
    }
    

    
    public List<Despesas> filtrarPorAno(Long userId, int ano) {
        LocalDate inicio = LocalDate.of(ano, 1, 1);
        LocalDate fim = LocalDate.of(ano, 12, 31);
       
        return repository.findByUtilizadorIdAndDataBetweenAndAtivaTrue(userId, inicio, fim);
    }

    public List<Despesas> filtrarPorCategoria(Long userId, Long catId) {
        
        return repository.findByUtilizadorIdAndCategoriaIdAndAtivaTrue(userId, catId);
    }
    
    public List<Despesas> filtrarPorValor(Long userId, Double min, Double max) {
        return repository.findByUtilizadorIdAndValorBetweenAndAtivaTrue(userId, min, max);
    }

    // --- ESTATÍSTICAS ---

    public Map<String, Double> getTotaisPorCategoria(Long userId) {
        List<Object[]> resultados = repository.somarDespesasPorCategoria(userId);
        return resultados.stream().collect(Collectors.toMap(o -> (String) o[0], o -> (Double) o[1]));
    }
    
    public Map<Integer, Double> getTotaisPorMes(Long userId) {
        List<Object[]> resultados = repository.somarDespesasPorMes(userId);
        return resultados.stream().collect(Collectors.toMap(o -> (Integer) o[0], o -> (Double) o[1]));
    }

    // --- ELIMINAR (Soft Delete) ---
    public void eliminarDespesa(Long id, Long userId) {
        Despesas d = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Despesa não encontrada"));

        if (!d.getUtilizador().getId().equals(userId)) {
            throw new RuntimeException("Esta despesa não te pertence.");
        }

        d.setAtiva(false); // Marca como apagada
        repository.save(d);
    }
}