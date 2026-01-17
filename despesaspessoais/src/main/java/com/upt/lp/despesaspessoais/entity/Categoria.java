package com.upt.lp.despesaspessoais.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String nomeIcone; 
    private String corHex;

    @OneToMany(mappedBy = "categoria")
    @JsonIgnore
    private List<Despesas> despesas;

    // --- NOVO: A QUEM PERTENCE A CATEGORIA ---
    // Se for NULL, a categoria é pública (todos veem)
    @ManyToOne
    @JoinColumn(name = "utilizador_id")
    private Utilizador utilizador; 

    public Categoria() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getNomeIcone() { return nomeIcone; }
    public void setNomeIcone(String nomeIcone) { this.nomeIcone = nomeIcone; }
    
    public String getCorHex() { return corHex; }
    public void setCorHex(String corHex) { this.corHex = corHex; }
    
    public List<Despesas> getDespesas() { return despesas; }
    public void setDespesas(List<Despesas> despesas) { this.despesas = despesas; }
    
    // Getters/Setters do Utilizador
    public Utilizador getUtilizador() { return utilizador; }
    public void setUtilizador(Utilizador utilizador) { this.utilizador = utilizador; }
}