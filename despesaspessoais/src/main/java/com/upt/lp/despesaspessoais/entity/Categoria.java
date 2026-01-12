package com.upt.lp.despesaspessoais.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;       // Ex: "Transporte"
    private String nomeIcone;  // Ex: "car" ou "fuel.png"
    private String corHex;     // Ex: "#FF0000" (Vermelho)

    @OneToMany(mappedBy = "categoria")
    @JsonIgnore // Evita loop infinito ao converter para JSON
    private List<Despesas> despesas;

    public Categoria() {}

    public Categoria(String nome, String nomeIcone, String corHex) {
        this.nome = nome;
        this.nomeIcone = nomeIcone;
        this.corHex = corHex;
    }

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
}