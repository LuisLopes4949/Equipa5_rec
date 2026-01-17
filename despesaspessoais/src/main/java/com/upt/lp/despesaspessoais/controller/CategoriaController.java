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

    @GetMapping
    public List<Categoria> listar(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return service.listarTodas(); 
        }
        return service.listarPorUtilizador(userId);
    }

    @PostMapping
    public Categoria criar(@RequestBody Categoria categoria, @RequestParam Long userId) {
        return service.criar(categoria, userId);
    }
    
    @GetMapping("/{id}")
    public Categoria buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // --- NOVO ENDPOINT DE ELIMINAR ---
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id, @RequestParam Long userId) {
        service.eliminar(id, userId);
    }
}