package com.upt.lp.despesaspessoais.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.upt.lp.despesaspessoais.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}