package com.upt.lp.despesaspessoais.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.upt.lp.despesaspessoais.entity.Despesas;

public interface DespesasRepository extends JpaRepository<Despesas, Long> {
}