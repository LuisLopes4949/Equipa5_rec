package com.upt.lp.despesaspessoais.controller;

import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.service.UtilizadorService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/utilizadores")
public class UtilizadorController {
    private final UtilizadorService service;

    public UtilizadorController(UtilizadorService service) { this.service = service; }

    @GetMapping
    public List<Utilizador> listar() { return service.listarTodos(); }

    @PostMapping
    public Utilizador criar(@RequestBody Utilizador u) { return service.criar(u); }
}