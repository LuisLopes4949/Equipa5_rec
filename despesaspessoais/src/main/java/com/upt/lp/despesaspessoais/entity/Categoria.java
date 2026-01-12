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

    @OneToMany(mappedBy = "categoria")
    @JsonIgnore // Importante para não dar erro sem DTOs
    private List<Despesas> despesas;

    public Categoria() {}

    public Categoria(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public List<Despesas> getDespesas() { return despesas; }
    public void setDespesas(List<Despesas> despesas) { this.despesas = despesas; }
}