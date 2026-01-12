package com.upt.lp.despesaspessoais.controller;

import com.upt.lp.despesaspessoais.entity.Categoria;
import com.upt.lp.despesaspessoais.service.CategoriaService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {
    private final CategoriaService service;

    public CategoriaController(CategoriaService service) { this.service = service; }

    @GetMapping
    public List<Categoria> listar() { return service.listarTodas(); }

    @PostMapping
    public Categoria criar(@RequestBody Categoria c) { return service.criar(c); }

    // --- NOVO: Editar Categoria [cite: 2420] ---
    @PutMapping("/{id}")
    public Categoria editar(@PathVariable Long id, @RequestBody Categoria c) {
        return service.editar(id, c);
    }

    // --- NOVO: Apagar Categoria [cite: 2421] ---
    @DeleteMapping("/{id}")
    public void apagar(@PathVariable Long id) {
        service.apagar(id);
    }
}	