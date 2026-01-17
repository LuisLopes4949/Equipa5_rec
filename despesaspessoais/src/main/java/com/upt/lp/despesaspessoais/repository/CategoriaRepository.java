package com.upt.lp.despesaspessoais.repository;

import com.upt.lp.despesaspessoais.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    // Esta Query diz: "Traz-me categorias deste User ID OU categorias sem dono (NULL)"
    @Query("SELECT c FROM Categoria c WHERE c.utilizador.id = :userId OR c.utilizador IS NULL")
    List<Categoria> findByUtilizadorIdOrGlobal(@Param("userId") Long userId);
}