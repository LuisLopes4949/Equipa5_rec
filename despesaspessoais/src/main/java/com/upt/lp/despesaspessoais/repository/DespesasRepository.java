package com.upt.lp.despesaspessoais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.upt.lp.despesaspessoais.entity.Despesas; // <--- Importa a classe com 's'
import java.time.LocalDate;
import java.util.List;

// Nota: Aqui dentro do <...> também tem de ser Despesas
public interface DespesasRepository extends JpaRepository<Despesas, Long> {

    List<Despesas> findByUtilizadorId(Long utilizadorId);

    // Filtros
    List<Despesas> findByUtilizadorIdAndDataBetween(Long userId, LocalDate inicio, LocalDate fim);
    List<Despesas> findByUtilizadorIdAndCategoriaId(Long userId, Long categoriaId);
    List<Despesas> findByUtilizadorIdAndValorBetween(Long userId, Double min, Double max);
    
    boolean existsByCategoriaId(Long categoriaId);

    // --- A CORREÇÃO ESTÁ AQUI EM BAIXO ---
    
    // Mudei "FROM Despesa" para "FROM Despesas" (com 's')
    @Query("SELECT c.nome, SUM(d.valor) " +
           "FROM Despesas d JOIN d.categoria c " + 
           "WHERE d.utilizador.id = :userId AND d.tipo = 'DESPESA' " +
           "GROUP BY c.nome")
    List<Object[]> somarDespesasPorCategoria(@Param("userId") Long userId);

    // Mudei "FROM Despesa" para "FROM Despesas" (com 's')
    @Query("SELECT MONTH(d.data), SUM(d.valor) " +
           "FROM Despesas d " +
           "WHERE d.utilizador.id = :userId AND d.tipo = 'DESPESA' " +
           "GROUP BY MONTH(d.data) " +
           "ORDER BY MONTH(d.data)")
    List<Object[]> somarDespesasPorMes(@Param("userId") Long userId);
}