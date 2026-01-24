package com.upt.lp.despesaspessoais.controller;

import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.service.UtilizadorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilizadores")
public class UtilizadorController {

    private final UtilizadorService utilizadorService;

    public UtilizadorController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Utilizador u) {
        try {
            return ResponseEntity.ok(utilizadorService.criar(u));

        } catch (IllegalArgumentException e) {

            if (e.getMessage().equals("EMAIL_EXISTENTE")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Email já existente");
            }

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Utilizador u) {
        try {
            return ResponseEntity.ok(
                    utilizadorService.autenticar(u.getEmail(), u.getPassword())
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(e.getMessage());
        }
    }
}
