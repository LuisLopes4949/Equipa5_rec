package com.upt.lp.despesaspessoais.repository;

import com.upt.lp.despesaspessoais.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    @Query("SELECT c FROM Categoria c WHERE (c.utilizador.id = :userId OR c.utilizador IS NULL) AND c.ativa = true")
    List<Categoria> findByUtilizadorIdOrGlobal(@Param("userId") Long userId);
}