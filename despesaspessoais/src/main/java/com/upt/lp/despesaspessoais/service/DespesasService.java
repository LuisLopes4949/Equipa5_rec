package com.upt.lp.despesaspessoais.service;

import com.upt.lp.despesaspessoais.entity.Despesas;
import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.enums.MetodoPagamento;
import com.upt.lp.despesaspessoais.enums.TipoMovimento;
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
        
        // ... (validações de valor e data que já tinhas) ...

        Utilizador u = utilizadorService.buscarPorId(userId);
        Categoria c = categoriaService.buscarPorId(catId);

        // 🔒 NOVA VALIDAÇÃO DE SEGURANÇA 🔒
        // Se a categoria tem dono (não é global) E o dono não é o user atual...
        if (c.getUtilizador() != null && !c.getUtilizador().getId().equals(userId)) {
            throw new IllegalArgumentException("Erro: Essa categoria pertence a outro utilizador! Cria a tua própria.");
        }
        // ------------------------------------

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
        existente.setTipo(dadosNovos.getTipo());

     // No método editarDespesa...
        if (catId != null) {
            Categoria c = categoriaService.buscarPorId(catId);
            
            // 🔒 Proteção aqui também
            if (c.getUtilizador() != null && !c.getUtilizador().getId().equals(existente.getUtilizador().getId())) {
                 throw new IllegalArgumentException("Erro: Categoria inválida.");
            }

            existente.setCategoria(c);
        }

        return repository.save(existente);
    }
    
    // --- FILTROS ---
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

        d.setAtiva(false); 
        repository.save(d);
    }
}