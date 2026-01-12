package com.upt.lp.despesaspessoais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.upt.lp.despesaspessoais.entity.Utilizador;
import java.util.Optional;

public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
    // Adiciona esta linha para encontrar pelo email
    Optional<Utilizador> findByEmail(String email);
}