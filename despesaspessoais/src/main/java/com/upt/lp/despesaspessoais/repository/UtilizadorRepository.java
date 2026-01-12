package com.upt.lp.despesaspessoais.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.upt.lp.despesaspessoais.entity.Utilizador;

public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
}