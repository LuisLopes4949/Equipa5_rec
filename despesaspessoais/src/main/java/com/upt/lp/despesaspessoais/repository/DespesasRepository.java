package com.upt.lp.despesaspessoais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.upt.lp.despesaspessoais.entity.Despesas;
import java.time.LocalDate;
import java.util.List;

public interface DespesasRepository extends JpaRepository<Despesas, Long> {

    // MUDANÇA 1: Adicionei "AndAtivaTrue" ao nome para filtrar automaticamente
    List<Despesas> findByUtilizadorIdAndAtivaTrue(Long utilizadorId);

    // Filtros também só mostram ativas
    List<Despesas> findByUtilizadorIdAndDataBetweenAndAtivaTrue(Long userId, LocalDate inicio, LocalDate fim);
    List<Despesas> findByUtilizadorIdAndCategoriaIdAndAtivaTrue(Long userId, Long categoriaId);
    List<Despesas> findByUtilizadorIdAndValorBetweenAndAtivaTrue(Long userId, Double min, Double max);
    
    boolean existsByCategoriaIdAndAtivaTrue(Long categoriaId);

    // MUDANÇA 2: Adicionei "AND d.ativa = true" nas Queries manuais
    @Query("SELECT c.nome, SUM(d.valor) " +
           "FROM Despesas d JOIN d.categoria c " + 
           "WHERE d.utilizador.id = :userId AND d.tipo = 'DESPESA' AND d.ativa = true " +
           "GROUP BY c.nome")
    List<Object[]> somarDespesasPorCategoria(@Param("userId") Long userId);

    @Query("SELECT MONTH(d.data), SUM(d.valor) " +
           "FROM Despesas d " +
           "WHERE d.utilizador.id = :userId AND d.tipo = 'DESPESA' AND d.ativa = true " +
           "GROUP BY MONTH(d.data) " +
           "ORDER BY MONTH(d.data)")
    List<Object[]> somarDespesasPorMes(@Param("userId") Long userId);
}