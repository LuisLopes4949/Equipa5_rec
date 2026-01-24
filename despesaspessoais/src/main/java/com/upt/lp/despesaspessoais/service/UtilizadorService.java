package com.upt.lp.despesaspessoais.service;

import com.upt.lp.despesaspessoais.entity.Utilizador;
import com.upt.lp.despesaspessoais.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtilizadorService {

    private final UtilizadorRepository repository;

    public UtilizadorService(UtilizadorRepository repository) {
        this.repository = repository;
    }

    public List<Utilizador> listarTodos() {
        return repository.findAll();
    }

    public Utilizador autenticar(String email, String password) {
        Utilizador user = repository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email não registado."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Password incorreta.");
        }

        return user;
    }

    public Utilizador criar(Utilizador u) {

        if (repository.findByEmail(u.getEmail()).isPresent()) {
            throw new IllegalArgumentException("EMAIL_EXISTENTE");
        }

        return repository.save(u);
    }

    public Utilizador buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
    }
}
