package com.upt.lp.despesaspessoais.controller;

import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.service.CategoriaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    // GET /api/categorias?userId=1
    @GetMapping
    public List<Categoria> listar(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            // Se não enviar ID, lista tudo (útil para admin ou debugging)
            return service.listarTodas(); 
        }
        return service.listarPorUtilizador(userId);
    }

    // POST /api/categorias?userId=1
    @PostMapping
    public Categoria criar(@RequestBody Categoria categoria, @RequestParam Long userId) {
        return service.criar(categoria, userId);
    }
    
    @GetMapping("/{id}")
    public Categoria buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }
}