package com.upt.lp.despesaspessoais.controller;

import com.upt.lp.despesaspessoais.entity.Despesas;
import com.upt.lp.despesaspessoais.service.DespesasService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/despesas")
public class DespesasController {

    private final DespesasService service;

    public DespesasController(DespesasService service) { this.service = service; }

    @GetMapping("/user/{userId}")
    public List<Despesas> listarPorUser(@PathVariable Long userId) {
        return service.listarPorUtilizador(userId);
    }

    @PostMapping
    public Despesas criar(@RequestBody Despesas despesa, 
                         @RequestParam Long userId, 
                         @RequestParam Long catId) {
        return service.criarDespesa(despesa, userId, catId);
    }

    // --- NOVO: Editar Despesa (PUT) [cite: 2414] ---
    // URL: PUT /api/despesas/5?catId=2
    @PutMapping("/{id}")
    public Despesas editar(@PathVariable Long id, 
                          @RequestBody Despesas despesa,
                          @RequestParam Long catId) {
        return service.editarDespesa(id, despesa, catId);
    }

    // --- NOVOS ENDPOINTS DE FILTROS  ---

    // 1. Filtro por Ano: GET /api/despesas/filtro/ano?userId=1&ano=2025
    @GetMapping("/filtro/ano")
    public List<Despesas> filtrarPorAno(@RequestParam Long userId, @RequestParam int ano) {
        return service.filtrarPorAno(userId, ano);
    }

    // 2. Filtro por Categoria: GET /api/despesas/filtro/categoria?userId=1&catId=5
    @GetMapping("/filtro/categoria")
    public List<Despesas> filtrarPorCategoria(@RequestParam Long userId, @RequestParam Long catId) {
        return service.filtrarPorCategoria(userId, catId);
    }

    // 3. Filtro por Valor: GET /api/despesas/filtro/valor?userId=1&min=10&max=100
    @GetMapping("/filtro/valor")
    public List<Despesas> filtrarPorValor(@RequestParam Long userId, @RequestParam Double min, @RequestParam Double max) {
        return service.filtrarPorValor(userId, min, max);
    }

    // --- NOVO: ESTATÍSTICAS  ---
    // URL: GET /api/despesas/estatisticas/categoria?userId=1
    @GetMapping("/estatisticas/categoria")
    public Map<String, Double> getEstatisticas(@RequestParam Long userId) {
        return service.getTotaisPorCategoria(userId);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id, @RequestParam Long userId) {
        service.eliminarDespesa(id, userId);
    }
}