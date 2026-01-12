package com.upt.lp.despesaspessoais.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.upt.lp.despesaspessoais.entity.Despesas;
import java.time.LocalDate;
import java.util.List;

public interface DespesasRepository extends JpaRepository<Despesas, Long> {

    // 1. Listar tudo de um utilizador
    List<Despesas> findByUtilizadorId(Long utilizadorId);

    // --- FILTROS (Requisito: Filtrar por data, categoria, valor) --- 

    // Filtrar por Ano (Busca entre 1 de Jan e 31 de Dez)
    List<Despesas> findByUtilizadorIdAndDataBetween(Long userId, LocalDate inicio, LocalDate fim);

    // Filtrar por Categoria
    List<Despesas> findByUtilizadorIdAndCategoriaId(Long userId, Long categoriaId);

    // Filtrar por Valor (Mínimo e Máximo)
    List<Despesas> findByUtilizadorIdAndValorBetween(Long userId, Double min, Double max);

    // --- ESTATÍSTICAS (Requisito: Totais por Categoria) --- [cite: 2424]
    
    // Esta Query personalizada soma os valores, agrupa pelo nome da categoria
    // e filtra apenas pelo utilizador e pelo tipo 'DESPESA' (ignorando receitas)
    @Query("SELECT c.nome, SUM(d.valor) " +
           "FROM Despesa d JOIN d.categoria c " +
           "WHERE d.utilizador.id = :userId AND d.tipo = 'DESPESA' " +
           "GROUP BY c.nome")
    List<Object[]> somarDespesasPorCategoria(@Param("userId") Long userId);
    
    // Verifica se existem despesas numa categoria (para impedir apagar categoria em uso) 
    boolean existsByCategoriaId(Long categoriaId);
}